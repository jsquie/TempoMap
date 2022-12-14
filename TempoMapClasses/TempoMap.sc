TempoMap {
    var timesArr; // raw time data received from OSCFunc
    var <numMatches;
    var meterArr; // dictionary representing the timed subdivisions of a tempo
    var <standardDev;
    var <>bpm; // current beats per minute
    var <>tolerance = 0.015; // how much leeway are we alloting for inconsitent
                            // timing? could change this to be dynamic later
    var index; // the index of the time we are receiving 
    var total; // running total of deltas received thus far
    var makeAdjustmentRange;
    var <diffArr;
    var curr;
    var timesQueue;
    var <>currDelta;
    var deltasStack;

    // current measure informatoin
    var thisMeasureDiffMean;
    var thisNumMatches;
    var numUnder180;

    const meterResolution = 8;
    // var 

    *new {
        ^super.new.initTempoMap();
    }

    initTempoMap {
        timesArr = Array.new(meterResolution);
        meterArr = Array.new(meterResolution);
        diffArr = Array.new();
        timesQueue = Queue.new(meterResolution);
        deltasStack = Stack.new(meterResolution);
        numMatches = 0;
        index = 0;
        total = 0;
        bpm = 60;
        tolerance = 0.01;
        thisNumMatches = 0;
        thisMeasureDiffMean = 0;
        curr = 0;
        numUnder180 = 0;
        currDelta = 0.5;
        standardDev = 0.0045310655 * 2;
    }

    add { arg time;
        // first delta added
        timesQueue.push(time);


        if (index == 1, {
            // this could be 'maps' representing different subdivisions of the
            // estimated meter to compare against  
            var delta_0;
            delta_0 = (timesQueue.pop() - timesQueue.peek()).neg;
            deltasStack.push(delta_0);
            // add to deltas array
            //this.prInitmeterArr(delta_0);

        });

        if (index > 1, { 
            var delta;
            var sumArr = [];

            delta = (timesQueue.pop() - timesQueue.peek()).neg;
            "DELTA: ".post;
            (delta).postln;

            currDelta = delta;

            diffArr = diffArr.add(delta);
            deltasStack.push(delta);
            total = total + delta;
            bpm = (60 / deltasStack.runningAvg);

            
            if (diffArr.size > 3, {
                for (2, diffArr.size - 3, {
                    arg i;

                    (diffArr.at(i) - diffArr.at(i-1)).postln;


                });
            });

            
            "----------------------".postln;
            "index: ".post;
            index.postln;
            "----------------------".postln;
            "deltaStack avg: ".post;
            deltasStack.runningAvg.postln;

            "curr after calculation: ".post;
            curr.postln;

            curr = curr + 1;

            if (curr == (meterResolution - 1), {
                total = 0;
                curr = 0;
            });


            
    });

    index = index + 1;
}

prInitmeterArr { arg delta, currTotal = 0;
    // calculate first delta
    // set initial bpm to that first delta
    // this is where we want to resolve the delta against the range that we want
    // what is this range???
//    if (delta > 180, {bpm = delta / 2});
    //if ((delta < 180) && (delta > 50), {bpm = delta});
    //if (delta < 50, {bpm = delta * 2});
    bpm = 120;

    "beat 1: 0".postln;
    // 0.000135x^{2}+0.0105x-0.61333

    meterArr = Array.new(meterResolution);

    tolerance = 0.00025 * bpm;
    "new tolerance: ".postln;
    tolerance.postln;
    // need to rethink this:
    meterResolution.do({
        arg i;
        "beat ".post;
        (i+2).post;
        ": ".post;
        (bpm * 0.25 * (i + 1)).post;
        " (arr index: ".post;
            i.post;
            " )".postln;
            meterArr = meterArr.add(delta * 0.25 * (i+1));
    });

    meterArr.postln;

    //meterArr.postln;
    if (currTotal == 0, {
        total = delta;
    });

}

prGetAdjustmentRange { arg thisBpm;
    var result;

    result = ((0.000135*thisBpm.squared) + (0.0105*thisBpm) - 0.61333) * 2;
    ^result
}

prFindMatch { arg curr;
    var result;

    result = curr + (meterArr[curr..].detectIndex({
        arg item, i;
        var diff;
        diff = total - item;

        if((diff.abs < (item * tolerance)), {
            "Found match between item: ".post;
            item.post;
            " and total: ".post;
            total.postln;
            diffArr = diffArr.add(diff);
            thisMeasureDiffMean = thisMeasureDiffMean + diff;
            if (diff.abs > makeAdjustmentRange, {
                // make an adjustment
                "outside of adjustmentRange".postln;
                this.prInitmeterArr(bpm + diff);
            });
            "diff: ".post;
            diff.postln;
        });
        (total - item).abs < (item * tolerance);
    }) ? -1);

    ^result;
}

}
