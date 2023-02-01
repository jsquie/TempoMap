// init server 
(
o = Server.local.options;
o.sampleRate = 48000;
o.blockSize = 512;
o.inDevice = "Macbook Pro Internal Microphone";
o.outDevice = "Macbook Pro Speakersk";
s.latency = 0.05;
)
s.waitForBoot({
    var synth, controlBus;

    l = List.new;

    // tm = TempoMap.new;
    TempoMap.new;

    synth = CtkSynthDef(\simple, {
        arg freq;
        var snd, env, trig, sig;
        snd = SinOsc.ar(freq:440, mul: 1.0);
        env = EnvGen.kr(Env.perc(0.001, 0.5, 0.9, -4.0), doneAction: 2);
        sig = snd * env;
        TempoMap.ar(sig);
        Out.ar(0, Pan2.ar(sig, 0))
    });

    s.sync;
    
    Pbind(
        'instrument', 'simple',
        'out', 0,
        'freq', 75.midicps,
        'dur', Pseq([1], 10),
        'stretch', Env([1, 0.5], [10], \sin),

    ).play();


    CmdPeriod.doOnce({
        "Ended with CMDPERIOD".postln;
    })

})

s.plotTree
