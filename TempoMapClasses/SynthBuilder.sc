SynthBuilder {

  var synthDef;
  var pbind;
  var name;
  var tm;

  *new { arg synth_name, server;
    ^super.new.initSynthBuilder(synth_name, server);
  }

  initSynthBuilder { arg s_name, s;

    name = s_name.asSymbol;

    synthDef = SynthDef(s_name, {
      arg freq = 440, amp = 0.1, attack_lvl, decay_lvl, sustain_lvl, release_lvl,
      attack_time, decay_time, release_time;

      var sig, env;

      env = EnvGen.kr(
          Env.new([attack_lvl, decay_lvl, sustain_lvl, release_lvl], 
          [attack_time, decay_time, release_time]), doneAction: Done.freeSelf);
      sig = SinOsc.ar(freq, 0, amp);

      Out.ar(0, (sig * env)!2)
    });

    synthDef.load(s);

  }

  play { 
    arg amp = 0.1, freq = 440, speed = 1, hits = 4, env_levels = [0, 1, 1, 0], env_times = [0.001, 0.1, 1] ;

    var times = env_times.normalize(0, speed);

    Pbind(
      'instrument', name,
      'out', 0,
      'amp', amp,
      'freq', freq,
      'attack_lvl', env_levels[0],
      'decay_lvl', env_levels[1],
      'sustain_lvl', env_levels[2],
      'release_lvl', env_levels[3],
      'attack_time', times[0],
      'decay_time', times[1],
      'release_time', times[2],
      'dur', Pseq([speed], hits)
    ).play();
  }

}


