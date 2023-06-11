

s.waitForBoot({
    
    var synth, osc_func, results, pattern, note, ampsList, threshold, amp_lst;
    var rout, arrOfArrs, all_amps, averaged_amps, i, interpolation_amt,
    peak_list;
    var divis;

    results = List.new;
    ampsList = List.new;
    arrOfArrs = List.new;
    all_amps = List.new;
    averaged_amps = List.new;
    peak_list = List.new;
    i = 0;
    threshold = 0.1;
    interpolation_amt = 25;
    divis = 250;

    s.latency = 0.1;

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
        amp = Amplitude.kr(in);
        SendReply.kr(Impulse.kr(divis), '/tr', amp.ampdb);
        
    });


    pattern = Pbind(
        'instrument', 'simple',
        'out', 0,
        'freq', 75.midicps,
        'dur', Pseq([0.25], inf),
        // 'dur', Pseq([~speed / 2], ~hits),
        // 'stretch', Env([1, 0.5], [10], \sin),

    );

    amp_lst.note.thresh_(0.107).play;

    osc_func = OSCFunc({
        arg msg, time;
        var amp;

        amp = msg[3];

        if (amp < -100, {amp = -100});
        ampsList.add(amp);
        all_amps.add(amp);
        if (i > interpolation_amt, {
            var sum_amps = 0;

            interpolation_amt.do({ |j|
                sum_amps = sum_amps + all_amps[i - j];
            });

            averaged_amps.add(sum_amps / interpolation_amt);
        });

        i = i + 1;
        results.add([amp, time]);
    }, '/tr');

    pattern.play();

    rout = Routine({
        {

            all_amps.do({|i, j| 

                if ((j > interpolation_amt) && (j < (all_amps.size -
                    interpolation_amt)) && (i > -60), {
                        var gt_ampj_to_j_minus_k = true, gt_ampj_to_j_plus_k = true;

                        // check to see if amp j - 1, ... , j - interpolation_amt 
                        // are gt_ampj_to_j_minus_k than i (amp j)

                        interpolation_amt.do({
                            |k|
                            if (all_amps[j - k - 1] > i, {
                                gt_ampj_to_j_minus_k = false;
                            });
                            if (all_amps[j + k + 1] > i, {
                                gt_ampj_to_j_plus_k = false;
                            });
                        });

                        if (gt_ampj_to_j_plus_k && gt_ampj_to_j_minus_k, {
                            "------------".postln;
                            "index = ".post; j.postln;
                            "value = ".post; i.postln;
                            if (peak_list.size > 0, {
                                var interval;
                                "interval between peaks: ".post;
                                interval = j - peak_list[peak_list.size - 1][1];
                                interval.postln;
                                "interval * 0.002 for second diff: ".post;
                                (interval * (1/divis)).postln;
                                if (j > 175, {
                                    "time diffs: ".post;
                                    (results[j][1] - results[j - 175][1]).postln;

                                })



                            });
                            peak_list.add([i, j]);
                            "------------".postln;
                            
                        });

                });
            });

            2.wait;
        }.loop;
    });

    (2.5).wait;
    rout.play;


    CmdPeriod.doOnce({
        var f, amps, interpolate_1, interpolate_2;

        amp_lst.free;

        all_amps.asArray.plot;
        averaged_amps.asArray.plot;

        peak_list.postln;

    })



})

(
~syn = CtkSynthDef.new(name:\sin, ugenGraphFunc:{
    var sig, out, env;
    sig = SinOsc.ar(freq:440.0, phase:0.0, mul:1.0, add:0.0);
    env = EnvGen.kr(
        Env.perc(attackTime:0.01, releaseTime:1.0, level:1.0, curve:-4.0)
    , gate:1.0, levelScale:1.0, levelBias:0.0, timeScale:1.0,
    doneAction:Done.freeSelf);
    out = sig * env * 0.5;
    Out.ar(0, out!2)
});

)

(
~r = Routine({
    var delta;
    loop {
        delta = rrand(1, 3) * 0.5;
        ~syn.note.play;
        delta.yield;
    }
});
)

~r.play;

