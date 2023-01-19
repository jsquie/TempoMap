s.waitForBoot({
    var synth, controlBus;

    l = List.new;

    controlBus = Bus.control(s, 1);

    s.sync;

    // tm = TempoMap.new;
    TempoMap.new;

    synth = CtkSynthDef(\simple, {
        arg freq;
        var snd, env, trig, sig;
        snd = SinOsc.ar(freq:440, mul: 0.39);
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

    ).play();

    10.wait;

})

s.plotTree
