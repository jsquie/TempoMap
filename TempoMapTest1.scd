// init server 
s.quit;
(
~blockSize = 256;
o = Server.local.options;
o.sampleRate = 48000;
o.blockSize = ~blockSize;
o.inDevice = "Macbook Pro Internal Microphone";
o.outDevice = "Audient iD4";
s.latency = 0.5;
~speed = 0.25;
~hits = 16;
)
s.waitForBoot({
    
    var synth;

    l = List.new;

    // tm = TempoMap.new;
    TempoMap.new;

    synth = CtkSynthDef(\simple, {
        arg freq;
        var snd, env, trig, sig;
        snd = SinOsc.ar(freq:440, mul: 0.2);
        env = EnvGen.kr(Env.perc(0.001, 0.5, 0.9, -4.0), doneAction: 2);
        sig = snd * env;
        TempoMap.ar(sig, ~blockSize);
        Out.ar(0, Pan2.ar(sig, 0))
    });

    s.sync;
    
    Pbind(
        'instrument', 'simple',
        'out', 0,
        'freq', 75.midicps,
        'dur', Pseq([~speed], ~hits),
        // 'stretch', Env([1, 0.5], [10], \sin),

    ).play();


    CmdPeriod.doOnce({
        var standardDev, mean, n;
        "Ended with CMDPERIOD".postln;
        "mean: ".post;
        mean = TempoMap.deltasTotAvg.runningAvg;
        mean.postln;
        "size: ".post;
        n = TempoMap.deltasTotAvg.size();
        n.postln;

        standardDev = {
            var sum, squared;
            sum = 0;
            TempoMap.deltasTotAvg.size.do({ 
                squared = (TempoMap.deltasTotAvg.pop() - mean).squared;
                sum = sum + squared;
            });

            (sum / n).sqrt;
        };

        "standard deviaton: ".post;

        standardDev = standardDev.value;

        standardDev.postln;
        "three standard devs: ".post;
        (standardDev * 3).postln;

        "supposed to be bpm: ".post;
        (60 / ~speed).postln;
        "Percentage of mean that is 3 standard devs: ".postln;
        ((standardDev * 3) / mean).postln;

        "number of hits within 3 standard devs: ".post;
        TempoMap.numStandardMatches.postln;

    })

})
s.quit
s.plotTree

~myFile = File.new("~/Music/SuperCollider/testFile".standardizePath, "w");

(
File.use("~/Music/SuperCollider/testFile.txt".standardizePath, "w", { |f|
    f.write("check this out")})
)

(
(60 / ~speed).postln;
)

0.005155 * 3
