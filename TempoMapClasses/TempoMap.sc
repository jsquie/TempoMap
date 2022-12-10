TempoMap {
    var timesArr; // raw time data received from OSCFunc
    var <numMatches;
    var meterArr; // dictionary representing the timed subdivisions of a tempo
    var mMatchArr;
    var <bpm; // current beats per minute
    var <>tolerance = 0.015; // how much leeway are we alloting for inconsitent
                            // timing? could change this to be dynamic later
    var index; // the index of the time we are receiving 
    var total; // running total of deltas received thus far
    var prevMatch;
    var diffTot;
    var <diffArr;
    var <endOfMeasureDiffs;

    // current measure informatoin
    var thisMeasureDiffMean;
    var thisNumMatches;

    const meterResolution = 8;
    // var 

    *new {
        ^super.new.initTempoMap();
    }

    initTempoMap {
        timesArr = Array.new(meterResolution);
        meterArr = Array.new(meterResolution);
        mMatchArr = Array.fill(meterResolution, { false });
        diffArr = Array.new();
        endOfMeasureDiffs = Array.new();
        numMatches = 0;
        diffTot = 0;
        index = 0;
        prevMatch = 0;
        total = 0;
        bpm = 0;
        tolerance = 0.01;
        thisMeasureDiffMean = 0;
        thisNumMatches = 0;
    }

    add { arg time;
        // first delta added
        timesArr = timesArr.add(time);

        if (index == 1, {
            // this could be 'maps' representing different subdivisions of the
            // estimated meter to compare against  
            var delta_0;
            delta_0 = 60 / (timesArr.at(1) - timesArr.at(0));
            // add to deltas array
            this.prInitmeterArr(delta_0);

        });

        if (index > 1, { 
            var delta;
            var curr; 
            var indexOfMatch;

            delta = 60 / (timesArr.at(index) - timesArr.at(index - 1));
            total = total + delta;
            
            "----------------------".postln;
            "index: ".post;
            index.postln;
            "----------------------".postln;

            "total: ".post;
            total.postln;
            /*
            "prevMatch: ".post;
            prevMatch.postln;
            */
            curr = prevMatch + 1;


            curr = curr + (meterArr[curr..].detectIndex({
                arg item;
                var diff;
                diff = total - item;
                if((diff.abs < (item * tolerance)), {
                    "Found match between item: ".post;
                    item.post;
                    thisNumMatches = thisNumMatches + 1;
                    " and total: ".post;
                    total.postln;
                    diffArr = diffArr.add(diff);
                    thisMeasureDiffMean = thisMeasureDiffMean + diff;

                    

                    "diff: ".post;
                    diff.postln;
                });
                (total - item).abs < (item * tolerance);
            }) ? -1);

            "curr after calculation: ".post;
            curr.postln;

            if (thisNumMatches == 0, {
               "Not enough tempo adjustment"; 
            });
            // no match was found, adjust bpm
            if (curr == 0, {
                var avgDiff;

                avgDiff = (thisMeasureDiffMean / thisNumMatches);
                "No matches found.".postln;
                if (thisNumMatches != 0, {
                    "We had some matches, so lets adjust based on the average
                    diff between those matches".postln;
                    avgDiff.postln;
                    this.prInitmeterArr(bpm + avgDiff);

                }, {
                    var thisDiff;
                    "We have had no matches this measure, lets just adjust".post;
                    "bpm based off of difference between this hit and last one".post;
                    "by last diff: ".postln;

                    thisDiff = 60 / (timesArr.at(index) - timesArr.at(index-1));
                    "thisDiff: ".post;
                    thisDiff.postln;

                    // this doesnt work if its a synchopation 
                    // how do we fix this? 
                    this.prInitmeterArr(thisDiff);
                });
                thisNumMatches = 0;
                thisMeasureDiffMean = 0;
                    total = 0;

            });

            if (curr == (meterResolution - 1), {
                "this measure diff mean: ".post;
                (thisMeasureDiffMean / thisNumMatches).postln;
                thisNumMatches = 0;
                thisMeasureDiffMean = 0;
                total = 0;
            });

    });

    index = index + 1;
}



inRange {

    total.postln;

}

prInitmeterArr { arg delta;
    // calculate first delta
    // set initial bpm to that first delta
    bpm = delta;

    "beat 1: 0".postln;

    meterArr = Array.new(meterResolution);

    tolerance = 0.00025 * delta;
    "new tolerance: ".postln;
    tolerance.postln;

    meterResolution.do({
        arg i;
        "beat ".post;
        (i+2).post;
        ": ".post;
        (delta * 0.5 * (i + 1)).post;
        " (arr index: ".post;
            i.post;
            " )".postln;
            meterArr = meterArr.add(delta * 0.5 * (i+1));
        });

        meterArr.postln;
        mMatchArr = mMatchArr.put(0, true);
        

        //meterArr.postln;

        total = delta;

    }

}
