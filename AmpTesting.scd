
s.waitForBoot({
    
    var synth, osc_func, results, pattern, note, diffQueue, threshold, amp_lst;
    results = List.new;
    diffQueue = List.new;
    threshold = 0.1;

    s.latency = 0.0;

    synth = CtkSynthDef(\simple, {
        arg freq;
        var snd, env, trig, sig;
        snd = SinOsc.ar(freq:440 + Rand(), mul: 0.2);
        env = EnvGen.kr(Env.perc(0.001, 0.5, 0.9, -4.0), doneAction: 2);
        sig = snd * env;
        Out.ar(0, Pan2.ar(sig, 0))
    });

    amp_lst = CtkSynthDef(\amp_in, { arg thresh = 0.1;
        var in, amp, trig; 
        in = In.ar(0);
        amp = Amplitude.kr(in:in, attackTime:0.01, releaseTime:0.01, mul:1.0,
            add:0.0);
        amp.poll;
        trig = amp >= thresh;
        SendTrig.kr(in:trig, id:0, value:amp)
    });


    pattern = Pbind(
        'instrument', 'simple',
        'out', 0,
        'freq', 75.midicps,
        'dur', Pseq([0.5], inf),
        // 'dur', Pseq([~speed / 2], ~hits),
        // 'stretch', Env([1, 0.5], [10], \sin),

    );

    amp_lst.note.play;

    osc_func = OSCFunc({
        arg msg, time;

        msg[3].postln;
        if (results.size > 0, {
            var diff = results[results.size - 1][1] - time;


            diff.abs.postln;
            threshold.postln;

            if (diff.abs < 0.1, { 
                var new_threshold;
                s.numSynths.postln;

                new_threshold = threshold + 0.001;
                amp_lst.free;
                "new threshold: ".post;
                new_threshold.postln;
                amp_lst.note.thresh_(new_threshold);
                amp_lst.note.play;
                threshold = new_threshold;
            });
            // (results[results.size - 1][1] - time).postln;
            diffQueue.add(diff);
        });
        results.add([msg[3], time]);
        
    }, '/tr');

    pattern.play();


    CmdPeriod.doOnce({
        var f, amps;

        amp_lst.free;

        results.do({|i| i.postln;});
        diffQueue.do({|i| i.postln; })

        // f = File.open("~/test.csv".standardizePath, "w");
        // f.write("amp,time\n");
        // results.do({|i| f.write(i[0].asCompileString + "," + i[1].asCompileString + "\n"); });

        // f.close;

    })



})
Server
