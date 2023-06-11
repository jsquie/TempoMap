TempoMap {
    var oscFunc;
    var meterArr; // dictionary representing the timed subdivisions of a tempo
    var <>bpm; // current beats per minute
                            // timing? could change this to be dynamic later
    classvar <index; // the index of the time we are receiving 
    var total; // running total of deltas received thus far
    var timesQueue;
    classvar <deltasQueue;
    classvar <diffQueue;
    classvar <bpmQueue;
    classvar <deltasTotAvg;
    classvar <deltasMeterArr;
    classvar <notFound;
    classvar <currDelta;
    classvar <amps;
    

    // current measure informatoin
    var runningDiffTot;
    var max;
    // TODO do not make this accessible THIS IS FOR DEBUG PURPOSES
    classvar <metArr;
    classvar timeAdjustment;
    classvar deltasHeap;


    const meterResolution = 16;
    const threeStandardDevs = 0.015867855243157;
    // var 

    *new {
        ^super.new.initTempoMap();
    }

    *ar { arg sig, thresh;
        var amp, time, rate;
        rate = 400;
        amp = Amplitude.kr(sig, attackTime: 0.01, releaseTime: 5);
        time = Sweep.kr(1, rate);
        SendReply.ar(Impulse.ar(rate), '/trig', [amp, time]);
        ^0.0
    }

    initTempoMap {
        amps = List.new;
        meterArr = Array.new(meterResolution);
        timesQueue = Queue.new(meterResolution);
        timesQueue.push(0);
        deltasQueue = Queue.new(meterResolution);
        deltasTotAvg = Queue.new();
        diffQueue = Queue.new(meterResolution);
        bpmQueue = Queue.new();
        metArr = MeterArr.new();
        deltasMeterArr = MeterArr();
        index = 0;
        total = 0;
        bpm = 60;
        runningDiffTot = 0;
        timeAdjustment = 0;
        deltasHeap = MaxMeterHeap.new();
        notFound = 0;
        max = -inf;

        oscFunc = OSCFunc({
            arg msg, time;
            //"---------------".postln;
            //"time: ".post;
            amps.add(msg[3]);
            // msg[3..].postln;
            this.new_add(msg[3..]);

        }, '/trig');
    }

    *plotAmps {
        amps.asArray.plot;
    }

    *getAmps {
        ^amps.asArray
    }

    new_add { arg amp_time;

        if (amp_time[0] < 0.001, {amps.add(0)}, {
            amps.add(amp_time[0])

        })


    }

    add { arg time;
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

}
