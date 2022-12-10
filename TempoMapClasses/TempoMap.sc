TempoMap {
    var timesArr; // raw time data received from OSCFunc
    var <numMatches;
    var meterArr; // dictionary representing the timed subdivisions of a tempo
    var mMatchArr;
    var <bpm; // current beats per minute
    var <>tolerance = 0.02; // how much leeway are we alloting for inconsitent
                            // timing? could change this to be dynamic later
    var index; // the index of the time we are receiving 
    var total; // running total of deltas received thus far
    var prevMatch;
    var diffTot;
    const meterResolution = 8;
    // var 

    *new {
        ^super.new.initTempoMap();
    }

    initTempoMap {
        timesArr = Array.new(meterResolution);
        meterArr = Array.new(meterResolution);
        mMatchArr = Array.fill(meterResolution, { false });
        numMatches = 0;
        diffTot = 0;
        index = 0;
        prevMatch = 0;
        total = 0;
        bpm = 0;
        tolerance = 0.01;
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

            // now we want to compare against prevMatch + 1 
            // Inv: while curr < meterArr.size - 1
            // total == meterArr.at(curr) || meterArr.at(curr + 1)
            // || ... || meterArr.at(prevMatch.at(curr + n))
           
            /*
            block { | break |

                var theseDiffs = [];
                var minItem = 0;

                while ({ (curr < (meterArr.size)) }, {
                    var diff;
                    var range;

                    
                    diff = total - meterArr.at(curr);
                    theseDiffs = theseDiffs.add(diff);
                     
                    range = meterArr.at(curr) * tolerance;

                    "diff at curr: ".post;
                    curr.post;
                    ": ".post;
                    diff.postln;

                    // if this 
                    if (diff.abs < range, {
                        diffTot = diffTot + diff;
                        prevMatch = curr;
                        "Found match at curr: ".post;
                        curr.postln;
                        mMatchArr = mMatchArr.put(curr, true);
                        break.value();
                    });
                    curr = curr + 1;
                });

                // no match was found
                "no match found".postln;
                minItem = theseDiffs.minItem({ arg item; item.abs });
                (minItem / bpm).postln;
                this.prInitmeterArr(bpm + (bpm * (minItem / bpm)));


            };
            */


            // running mean ?? keep track of differences as they come in and log
            // if the average starts leaning a certain way, change the tempo?
            // This would mean that I would have to track each difference
            // absolute value?? or no... or yes... we could separately track how
            // far off things were?? 
            curr = curr + (meterArr[curr..].detectIndex({
                arg item;

                

                (total - item).abs < (item * tolerance);
            }) ? 0);

            "curr after calculation: ".post;
            curr.postln;

            // do stuff with curr


            // if curr == meterArr.size then we've matched a new beat 1 of a new
            // measure 

            // if curr == 0?? 

                       
            // if total becomes greater than meterArr.at(4) then we need to
            // reset total because we don't  want to look at times that are
            // greater than the amount of time in the meter. 
            // if total is within a 1% range of meterArr.at(3) 
            if ((total - meterArr.at(meterResolution - 1)).abs <
            (meterArr.at(meterResolution - 1) * 0.03125), {
                total = 0;
                prevMatch = -1; 
                mMatchArr = [false, false, false, false];                
                "diffTot: ".post;
                diffTot.postln;

                if (diffTot.abs > 1, {
                    var bpmPercentDiff;

                    bpmPercentDiff = diffTot.abs / bpm;


                    if (diffTot > 0, {
                     this.prInitmeterArr(bpm + (bpm * bpmPercentDiff));
                    }, {
                     this.prInitmeterArr(bpm - (bpm * bpmPercentDiff));

                    })
                });

                diffTot = 0;
                }, {
                    if(total > meterArr.at(meterResolution - 1), {
                        total = 0;
                        prevMatch = -1;
                    });
                }
            );

            mMatchArr.postln;

        // times that match meterArr.at(4) result in matches at "beat 1" of
        // a "new measure"


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

    meterArr = Array.new(4);

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
