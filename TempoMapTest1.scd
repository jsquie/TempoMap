// init server s.quit;
ServerOptions.devices

(
~blockSize = 256;
o = Server.local.options;
o.sampleRate = 48000;
o.blockSize = ~blockSize;
o.inDevice = "Macbook Pro Internal Microphone";
o.outDevice = "External Headphones";
s.latency = 0.5;
~speed = 0.5;
~hits = 500;
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
        'dur', Pseq([~speed / 2, ~speed / 2, ~speed], ~hits),
        // 'dur', Pseq([~speed / 2], ~hits),
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

        "beat 16 (beat 0): ".post;
        (TempoMap.metArr.orgTime +
        TempoMap.metArr.totalDiff).postln;

        "*********** Number of not founds: ***********".postln;
        TempoMap.notFound.postln;

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
20.do {|i| (i % 4).postln;}
)
(
{
    SendPeakRMS.kr(Dust.ar(20), 20, 3, "/replyAddress")
}.play;
)

(
o = OSCFunc({ |msg|
    "peak: %, rms: %".format(msg[3], msg[4]).postln
}, '/replyAddress');
)
o.free;

(
var arr;

arr = [5, 6, 5, 5, 6, 5];

arr.do({|x,y| x.postln; y.postln; });


)
