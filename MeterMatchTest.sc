// server boot
s.boot;
s.quit;
Server.default.options.inDevice_("Macbook Pro Microhpone");
Server.default.options.outDevice_("Macbook Pro Speakers");

~buffer = CtkBuffer("~/Music/SuperCollider/TempoMapProject/Buffers/Rim-909-Clean.wav".standardizePath).load;
~tm = TempoMap.new(60);
~tm.add(60);
~tm.add(60);
(
~buffPlay = CtkSynthDef(\bufPlay, {
  var snd;

  snd = PlayBuf.ar(1, ~buffer, BufRateScale.kr(~buffer), doneAction:2);
  Out.ar(0, snd * 1!2)
})
)
(
      ~t = Task({
        loop({
          var note, next;

          next = ~tm.bpm / 60;
          note = ~buffPlay.note();
          note.play;
          next.wait;
          note.free;
        })
        
  )
~t.play;
~t.stop;
~buffer.free;

// SynthDef definition for simple synth that creates the noise
s.waitForBoot({
  var tm, c, d, w, t, e = 0, i = 0, buffer, f;
  var a;
  var accuracyThreshold = 0.01;
  var stabilityTracker = 4;
  var respondingSynth;

  buffer = CtkBuffer("~/Music/SuperCollider/TempoMapProject/Buffers/Rim-909-Clean.wav".standardizePath).load;


  a = CtkSynthDef(\simpleSinPercEnv, {
    arg freq, duration;
    var snd, amp, env, trig, sig;
    var threshold = 0.9;
    snd = SinOsc.ar(freq:440);
    env = EnvGen.kr(
      envelope:Env.perc(attackTime:0.01, releaseTime:duration - 0.01, level:0.5, curve:-4.0), 
      gate:1.0, 
      levelScale:1.0, 
      levelBias:0.0, 
      timeScale:1.0, 
      doneAction:2
    );
    //sig = snd * env;
    trig = Onsets.kr(FFT(LocalBuf(512), snd), threshold);
    SendTrig.kr(trig, 0, 0);
    // also sending audio out
    Out.ar(0, Pan2.ar(snd * env, 0))

  });

  respondingSynth = CtkSynthDef(\responseBuffer, {
    var snd;
    snd = PlayBuf.ar(numChannels:1, bufnum:buffer, rate: BufRateScale(buffer), trigger:1.0, startPos:0.0, loop:0.0, doneAction:2);
    Out.ar(0, snd * 1!2);
    });
    // register to reveive the message

    tm = TempoMap.new();
    w = Window.new.front;
    t = StaticText(w, Rect(10, 10, 80, 30)).string_("Not started");


    OSCFunc({ arg msg, time;

      tm.add(time);
      { t.string = tm.bpm }.defer;


    }, '\tr', s.addr);


    s.sync;

    Pbind(
      'instrument', 'simpleSinPercEnv',
      'out', 0, /// out bus
      'freq', 75.midicps,
      'dur', Pseq([0.25], 66),
      'duration', 0.15,
      //'stretch', Env([1, 62/60], [5], 'lin'),
    ).play(AppClock);



    CmdPeriod.doOnce {
      var sumArr;
      var min, max;
      buffer.free;
      "------------ END ------------".postln;
      // find standard deviation? 
      sumArr = tm.diffArr.collect({ arg item, i;
        (item - tm.diffArr.mean).squared;
      });
      sumArr.postln;
      "standard dev: ".post;
      ((sumArr.sum / tm.diffArr.size).sqrt).postln;

      "tm diffs max diff: ".post;
      max = tm.diffArr.maxItem({arg item; item });
      max.postln;
      "tm diffs min diff: ".post;
      min = tm.diffArr.minItem({arg item; item });
      min.postln;
      "tm diff range: ".post;
      (max.abs + min.abs).postln;

      "tm mean: ".post;
      tm.diffArr.mean.postln;
      tm.diffArr.plot;

    }
  }
);



Env([0,1], [1], 'lin').plot
/*
Env([1, 5], [240], 'lin').plot


s.boot;
// 120 - - - 60 - 60 -
(
~bind = Pbind(
  'instrument', 'simpleSinPercEnv',
  'out', 0, /// out bus
  'freq', 75.midicps,
  'dur', Pseq([1], inf),
  'duration', 0.25,
).play;
)
~bind.play;
// SynthDef for machine listening and analysis
b = ~a.note().duration_(0.25);

b.freq_(75.midicps).duration_(0.25);v
b.play;
b.free;
*/

(

var a = 0;

a = a + (List[2, 0, 0, 2].detectIndex({arg item; item == 3}) ? -1);

a.postln;

)
