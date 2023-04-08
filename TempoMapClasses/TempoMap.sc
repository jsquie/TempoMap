TempoMap {
    var oscFunc;
    var <numMatches;
    var meterArr; // dictionary representing the timed subdivisions of a tempo
    var <>bpm; // current beats per minute
    var <>tolerance = 0.015; // how much leeway are we alloting for inconsitent
                            // timing? could change this to be dynamic later
    classvar <index; // the index of the time we are receiving 
    var total; // running total of deltas received thus far
    var makeAdjustmentRange;
    var <diffArr;
    var curr;
    var timesQueue;
    classvar <deltasQueue;
    classvar <diffQueue;
    classvar <bpmQueue;
    classvar <deltasTotAvg;
    classvar <deltasMeterArr;
    classvar <notFound;
    classvar <currDelta;

    // current measure informatoin
    var thisMeasureDiffMean;
    var thisNumMatches;
    var numUnder180;
    var runningDiffTot;
    var a;
    classvar <numStandardMatches;
    // TODO do not make this accessible THIS IS FOR DEBUG PURPOSES
    classvar <metArr;
    classvar timeAdjustment;
    classvar deltasHeap;

    const meterResolution = 16;
    const threshold = 0.2;
    const threeStandardDevs = 0.015867855243157;
    // var 

    *new {
        ^super.new.initTempoMap();
    }

    *ar { arg sig, thresh;
        var amp, trig, a;
        amp = Amplitude.kr(sig, 0.25, 0.25).ampdb;
        amp = K2A.ar(amp);
        //amp.poll;
        trig = amp >= thresh;
        SendTrig.ar(trig, 0, 0);

        ^0.0
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
        deltasMeterArr = MeterArr();
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
        timeAdjustment = 0;
        deltasHeap = MaxMeterHeap.new();
        notFound = 0;
        oscFunc = OSCFunc({
            arg msg, time;
            // "---------------".postln;
            // "time: ".post;
            // time.postln;
            this.add(time);

        }, '/tr');
    }

    simp_add { arg time;

        if (index > 0, {
            var last, thisDiff;
            last = timesQueue.pop();
            thisDiff = time - last;
            if (index > 1, {
                diffQueue.push(deltasQueue.pop() - thisDiff);
            });
            deltasQueue.push(thisDiff);
            // thisDiff.postln;
        });

        timesQueue.push(time);
        index = index + 1;
    }

    add { arg time;
        // first delta added
        // 'index: '.post;
        // index.postln;

        if (index > 0, {
            var last, thisDiff, deltasCount;

            last = timesQueue.pop();
            thisDiff = time - last;

            deltasCount = deltasMeterArr.find(thisDiff);

            if (deltasCount != nil, {
                var newCount;
                newCount = deltasCount + 1;
                deltasMeterArr.add(thisDiff, newCount);

            }, {
                deltasMeterArr.add(thisDiff, 1);
            });

            // "deltasCount: ".post;
            // deltasMeterArr.print;

            // "diff: ".post;
            // (thisDiff).postln;

            if (index == 1, {
                bpm = 60 / thisDiff;
                // init metArr

            });

            if (index > 1, {
                var lastDiff;
                var diffDiff;
                // check meter arr with current "time" to see if we have a match
                lastDiff = deltasQueue.pop();
                // difference between current diff and last diff
                diffDiff = thisDiff - lastDiff;
                // add to the queue of difference differences
                diffQueue.push(diffDiff);

                // after aquiring enough differences to make a decision on what
                // the tempo might be
                if (index == 16, {
                    var deltaCountContents, deltasAvg, maxDeltaCount;

                    deltaCountContents = deltasMeterArr.getArr();
                    /// add everything from deltasCount into the heap 
                    // identify what the max value is.
                    // add them all with the backwards version 
                    // "*****deltasCount.size: ".post;
                    // deltaCountContents.size.postln;

                    deltaCountContents.size.do({|i|
                        // "INDEX: ".post;
                        // i.postln;
                        deltasHeap.insert(
                            deltaCountContents.at(i)[1], 
                            deltaCountContents.at(i)[0],
                            i
                        );
                    });

                    // determine the most commonly occuring delta 
                    maxDeltaCount = deltasHeap.findMax();


                    deltasAvg = deltaCountContents.at(maxDeltaCount[2])[3];

                    // "deltas average: ".post;
                    // deltasAvg.postln;

                    this.prInitMetArr(time, deltasAvg);

                    currDelta = deltasAvg;


                });

                if (index > 16, {
                    var cmpTime, found;

                    runningDiffTot = runningDiffTot + thisDiff;
                    if (runningDiffTot > metArr.totalDiff, {
                        timeAdjustment = timeAdjustment - runningDiffTot;
                        runningDiffTot = 0;
                    });
                    cmpTime = time + timeAdjustment;                     

                    // "cmpTime: ".post;
                    // cmpTime.postln;

                    // "metArr max diff: ".postln;
                    // metArr.totalDiff.postln;

                    // "runningDiffTot: ".post;
                    // runningDiffTot.postln;

                    // "DID WE FIND A MATCH? ".post;
                    found = metArr.find(cmpTime);
                    // found.postln;

                    if (found.isNil, {

                        notFound = notFound + 1;

                    });
                    

                });



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
            // "-------------".postln;

        });

        timesQueue.push(time);
        index = index + 1;
    }

    prInitMetArr { arg initTime, diff;

        var meterValues, keyValuePairs, futureTimes;

        // "Initializing from time: ".post;
        initTime.postln;

        meterValues = Array.new(meterResolution);

        (meterResolution / 4).do({|i|
            4.do({|x|
                meterValues.add((i+1) + (0.1 * ((x % 4) + 1)))
            });
        });

        futureTimes = Array.fill(meterResolution, { |i| initTime + (i * (0.25 * diff))});

        futureTimes.do({ |x, y| x.post; ", ".post;  meterValues.at(y).postln; });

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
            // "Found match between item: ".post;
            // item.post;
            // " and total: ".post;
            // total.postln;
            diffArr = diffArr.add(diff);
            thisMeasureDiffMean = thisMeasureDiffMean + diff;
            // if (diff.abs > makeAdjustmentRange, {
                // make an adjustment
                // "outside of adjustmentRange".postln;
                // this.prInitmeterArr(bpm + diff);
            // });
            // "diff: ".post;
            // diff.postln;
        });
        (total - item).abs < (item * tolerance);
    }) ? -1);

    ^result;
}

}
