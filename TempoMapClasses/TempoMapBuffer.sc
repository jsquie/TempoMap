TempoMapBuffer {

    *new {
        ^super.new.initTempoBuf();
    }

    *ar { arg sig, thresh;
        var amp, trig, a;
        amp = Amplitude.kr(sig, 0.25, 0.25).ampdb;
        amp = K2A.ar(amp);
        //amp.poll;
        trig = amp >= thresh;
        SendTrig.ar(trig, 0, 0);

        ^0.0
    }

    initTempoBuf {
      

    }



}
