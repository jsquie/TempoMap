TempoMap {
    var oscFunc;
    var <numMatches;
    var meterArr; // dictionary representing the timed subdivisions of a tempo
    var <>bpm; // current beats per minute
    var <>tolerance = 0.015; // how much leeway are we alloting for inconsitent
                            // timing? could change this to be dynamic later
    var index; // the index of the time we are receiving 
    var total; // running total of deltas received thus far
    var makeAdjustmentRange;
    var <diffArr;
    var curr;
    var timesQueue;
    classvar <deltasQueue;
    classvar <diffQueue;
    classvar <bpmQueue;
    classvar <deltasTotAvg;

    // current measure informatoin
    var thisMeasureDiffMean;
    var thisNumMatches;
    var numUnder180;
    var runningDiffTot;
    classvar <numStandardMatches;
    classvar metArr;

    const meterResolution = 64;
    const threshold = 0.2;
    const threeStandardDevs = 0.015465;
    // var 

    *new {
        ^super.new.initTempoMap();
    }

    *ar { arg sig, blockSize;
       var trig;
       trig = Onsets.kr(FFT(LocalBuf(512), sig), threshold);
       // "HERE AT AR".postln;
       SendTrig.kr(trig, 0, 0);
       ^0.0;
    }
    
    initTempoMap {
        meterArr = Array.new(meterResolution);
        diffArr = Array.new();
        timesQueue = Queue.new(meterResolution);
        deltasQueue = Queue.new(meterResolution);
        deltasTotAvg = Queue.new();
        diffQueue = Queue.new(meterResolution);
        bpmQueue = Queue.new();
        metArr = MeterArr.new();
        numMatches = 0;
        index = 0;
        total = 0;
        bpm = 60;
        tolerance = 0.01;
        thisNumMatches = 0;
        thisMeasureDiffMean = 0;
        curr = 0;
        numStandardMatches = 0;
        numUnder180 = 0;
        runningDiffTot = 0;
        oscFunc = OSCFunc({
            arg msg, time;
            "---------------".postln;
            "time: ".post;
            time.postln;
            this.add(time);
           
        }, '/tr');
    }

    add { arg time;
        // first delta added
        'index: '.post;
        index.postln;

        if (index > 0, {
            var last, thisDiff;

            last = timesQueue.pop();
            thisDiff = time - last;

            

            "diff: ".post;
            (thisDiff).postln;
            "bpm derived from thisDiff: ".post;
            ((60 / thisDiff).round).postln;
            bpmQueue.push(60 / thisDiff);
            bpm = bpmQueue.runningAvg.round;
            "bpm is: ".post;
            bpm.postln;
            if (index == 1, {
                bpm = 60 / thisDiff;
                // init metArr

            });
            if (index > 1, {
                var lastDiff;
                var diffDiff;

                // check meter arr with current "time" to see if we have a match

                lastDiff = deltasQueue.pop();

              

                diffDiff = thisDiff - lastDiff;
                // "diffDiff: ".postln;
                // diffDiff.postln;
/*                 if (diffDiff.abs <= threeStandardDevs, { */
                /*     "SUCCESS: this diff is within 2 standard devs of last */
                /*     diff!".postln; */
                /*     numStandardMatches = numStandardMatches + 1; */
                /* }, {  */
                /*     "difference: ".post; */
                /*     (threeStandardDevs - diffDiff).postln; */
/*                 } */
                diffQueue.push(diffDiff);


                /* if (diffDiff.abs > deltasQueue.range, { */
                /*     "diffDiff is greater than range!".postln; */
                /*      */
                /* }); */

                if (index == 4, {
                    this.prInitMetArr(time, thisDiff);

                });
                if (index > 4, {
                    var cmpTime;

                    cmpTime = time;

                    runningDiffTot = runningDiffTot + thisDiff;

                    "metArr max diff: ".postln;
                    metArr.totalDiff.postln;

                    "runningDiffTot: ".post;
                    runningDiffTot.postln;

                    if (runningDiffTot > metArr.totalDiff, {
                        cmpTime = cmpTime - runningDiffTot;
                        runningDiffTot = 0;

                    });


                    "DID WE FIND A MATCH? ".post;
                    metArr.find(cmpTime).postln;

                })


            });
            deltasQueue.push(thisDiff);
            deltasTotAvg.push(thisDiff);
            // "Average delta: ".post;
            // deltasTotAvg.runningAvg.postln;
            // "Range: ".post;
            // deltasTotAvg.range.postln;
            // "Bpm running Avg: ".post;
            // bpmQueue.runningAvg.postln;
            if (bpmQueue.size > (bpm / 10), {
                bpmQueue.pop();
            });
            "-------------".postln;

        });

        timesQueue.push(time);
        index = index + 1;
    }

    prInitMetArr { arg initTime, diff;

        var meterValues, keyValuePairs, futureTimes;

        "Initializing from time: ".post;
        initTime.postln;

        meterValues = Array.new(meterResolution);

        (meterResolution / 4).do({|i|
            4.do({|x|
                meterValues.add(i + (0.1 * (x % 4)))
            });
        });

        futureTimes = Array.fill(meterResolution, { |i| initTime + (i * (0.25 * diff))});

        futureTimes.postln;

        meterResolution.do({ |i| metArr.add(futureTimes.at(i), meterValues.at(i))});
        
    }
prInitmeterArr { arg delta, currTotal = 0;
    

}

prGetAdjustmentRange { arg thisBpm;

    // 0.01300341
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
