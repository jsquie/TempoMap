// server boot
s.boot;
s.quit;
Server.default.options.inDevice_("Macbook Pro Microhpone");
Server.default.options.outDevice_("Macbook Pro Speakers");


~a = 0;
// SynthDef definition for simple synth that creates the noise
s.waitForBoot({
var tm, c, d, w, t, e = 0, i = 0;
var accuracyThreshold = 0.01;
var stabilityTracker = 4;


~a = CtkSynthDef(\simpleSinPercEnv, {
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
  'dur', Pseq([0.5], 33),
  'duration', 0.25,
  //'stretch', Env([1, 62/60], [5], 'lin'),
).play(AppClock);

CmdPeriod.doOnce {
  var sumArr;
  var min, max;
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
