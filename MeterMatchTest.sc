// server boot
s.boot;
s.quit;

(
  ~buffers = [];
  PathName("/Users/jamessquires/Music/SuperCollider/TempoMapProject/Buffers/Real Light/").filesDo{ arg pathname;

    ~buffers = ~buffers.add(CtkBuffer(pathname.fullPath).load);

  }
  )

  // SynthDef definition for simple synth that creates the noise
  s.waitForBoot({
    var w, t, e = 0, i = 0;
    var a;
    var synth;
    var drumBuffer;
    var realTimeIn;
    var accuracyThreshold = 0.01;
    var stabilityTracker = 4;
    var respondingSynth;
    ~buffers = [];
    PathName("/Users/jamessquires/Music/SuperCollider/TempoMapProject/Buffers/Real Light/").filesDo{ arg pathname;

      ~buffers = ~buffers.add(CtkBuffer(pathname.fullPath).load);
    };

    TempoMap.new;

    a = CtkSynthDef(\simpleSinPercEnv, {
      arg freq, duration;
      var snd, amp, env, trig, sig;
      var threshold = 0.9;
      snd = SinOsc.ar(freq:440);
      env = EnvGen.kr(
        envelope:Env.perc(attackTime:0.01, 
          releaseTime:duration - 0.01, level:0.5, curve:-4.0), 
          gate:1.0, 
          levelScale:1.0, 
          levelBias:0.0, 
          timeScale:1.0, 
          doneAction:2
        );
        TempoMap.ar(snd * env); 
        Out.ar(0, Pan2.ar(snd * env, 0))
      });

      drumBuffer = CtkSynthDef(\plbuf, {
        arg buffer = 0, duration;
        var sig, env;
        env = EnvGen.kr(Env.perc(0.01, duration - 0.01));
        sig = PlayBuf.ar(1, buffer, BufRateScale.kr(buffer));
        Out.ar(0, (sig * env)!2)
      });


      realTimeIn = CtkSynthDef(\realTime, {
        var in, trig;
        var threshold = 0.9;
        in = SoundIn.ar(0);
        trig = Onsets.kr(FFT(LocalBuf(512), in), threshold);
        // SendTrig.kr(trig, 0, 0);

      });

      synth = CtkSynthDef(\synth, {
        var snd, env;
        snd = SinOsc.ar(freq: 220);
        env = EnvGen.kr(Env.perc(), doneAction: 2);
        TempoMap.ar(snd * env);
      });
      // register to reveive the message

      w = Window.new.front;
      t = StaticText(w, Rect(10, 10, 80, 30)).string_("Not started");

      /*
      OSCFunc({ arg msg, time;

        ~tm.add(time);
        { t.string = ~tm.currDelta }.defer;



      }, '\tr', s.addr);

      */
      s.sync;

      Pbind(
        'instrument', 'simpleSinPercEnv',
        'out', 0, /// out bus
        'freq', 75.midicps,
        'dur', Pseq([1], inf),
        'duration', 0.15,
        'stretch', Env([1, 1, 0.5], [10, 15], 'lin'),
      ).play(SystemClock);


      //   realTimeIn.note.play;


      4.wait;
      //s.sync;
      /*
      Task(
        var thisWait;
        loop({
          synth.note.play;
          (~tm.currDelta).wait;
        })
      }).play(SystemClock);
      */

      Task({
        loop({
          drumBuffer.note.buffer_(~buffers.at(3)).duration_(0.5).play;
          drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
          (TempoMap.currDelta / 4).wait;
          TempoMap.currDelta.postln;
          drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
          (TempoMap.currDelta / 4).wait;
          drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
          drumBuffer.note.buffer_(~buffers.at(11)).duration_(0.5).play;
          (TempoMap.currDelta / 4).wait;
          drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
          (TempoMap.currDelta / 4).wait;


        })
      }).play(SystemClock);


      CmdPeriod.doOnce {
        var sumArr;
        var min, max;
        "------------ END ------------".postln;
        ~buffers.do({arg item; item.free; });


      };

    });

    ~buffers.do({arg item; item.free; })

    (

      var a = 0;


      a.postln;

    )
    //~tm.diffArr.plot

    (
      var a = [1/4, 1/3, 1/2, 1/8, 1/16];

      a.collect({
        arg item;



      })


    )

    { SoundIn.ar(0)!2}.play
    s.meter

    s.boot;
    { SinOsc.ar(freq:440, phase:0.0, mul:0.2, add:0.0)!2 }.play;

