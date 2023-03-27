
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
        TempoMap.ar(in);
        // SendTrig.kr(trig, 0, 0);

      });

      s.sync;

      realTimeIn.note.play;

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

      16.wait;

          Task({
              loop({
                  drumBuffer.note.buffer_(~buffers.at(3)).duration_(0.5).play;
                  drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
                  (TempoMap.currDelta).wait;
                  TempoMap.currDelta.postln;
                  drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
                  (TempoMap.currDelta).wait;
                  drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
                  drumBuffer.note.buffer_(~buffers.at(11)).duration_(0.5).play;
                  (TempoMap.currDelta).wait;
                  drumBuffer.note.buffer_(~buffers.at(6)).duration_(0.5).play;
                  (TempoMap.currDelta).wait;


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



