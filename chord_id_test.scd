
s.quit

~mags = List.new

s.sampleRate / 64 
Buffer.freeAll
s.freeAll
s.options.numBuffers = 2.pow(13) 
s.options.numBuffers



(
var b, tbl, lst, sigSize = 2.pow(5);

b = Buffer.alloc(s, sigSize * 2);
lst = List.new;

tbl = Signal.sineFill(size:sigSize, amplitudes:Array.exprand(sigSize, 0.001,
    1),phases:Array.exprand(sigSize, 0.001, pi / 2)).asWavetable;

lst.add(tbl);

b.loadCollection(tbl);

lst.postln;

{ Out.ar(0, Osc.ar(b, 440) * 0.2) }.play

)

Array.exprand(25, 0.001, pi / 2)


s.waitForBoot({

    var s, tonic, chord, buffs, lst, fft_size;
    var listener, numBuffs;
    var syn, resolution;


    fft_size = 2048 * 2;
    numBuffs = 250;
    resolution = 48000 / 64;
    resolution.postln;
    ~mags = List.new(numBuffs);

    buffs = Array.fill(numBuffs, { |i| Buffer.alloc(s, fft_size) });

    lst = List.new(numBuffs);

    tonic = 75;
    chord = [tonic.midicps, (tonic + 4).midicps, (tonic + 7).midicps];

    syn = CtkSynthDef('simpleSynth', {
        var env, sig;
        env = EnvGen.kr(Env.new([0, 1, 0.85, 0], [0.001, 0.1, 1].normalizeSum));
        sig = SinOsc.ar(440, 0, 0.2);
        Out.ar(0, Pan2.ar(sig * env))
    });


    listener = CtkSynthDef('listener',
        {
            arg buff = 0;
            var sig, chain, fftSize = 1024, windowStarts, stream;
            sig = In.ar(0);
            chain = FFT(buff, sig);
        }
    );


    // for each note in the chord
    // lets look at a number of ffts and see the difference at bin
    // maybe we can get some detailed envelope info
    s = syn.note(starttime: (100 / resolution), duration: 1).play;
    buffs.do {
        |b, idx|
        listener.note(starttime: ((idx + 1) / resolution), duration: (2 /
        resolution)).buff_(b).play;
    };

    1.wait;

    buffs.do {
        |b|
        //b.normalize;
        b.getToFloatArray(action: { |i|
            var frames = i.clump(2).flop;

            lst.add(
                Complex(Signal.newFrom(frames[0]), Signal.newFrom(frames[1]))
            )
        })
    };

    lst.do {|i| ~mags.add(i.magnitude)};

    ~mags.asArray[..50].plot;

    CmdPeriod.run();

})

(
~mags.collect { |i| i.mean == 0 }.do({|i| i.postln; })
)
~mags[0].size;
~mags[0].mean
[1, 2, 3].
~mags.size

(
~fft_bin_amt = ~mags[1].size;

~freqBins = List.new(~fft_bin_amt);


~fft_bin_amt.do { |i|
    ~mags.do {
        |j|
        ~freqBins.add(j[i])
    }

}
)


~mags.flatten

~lst = List.new
~mags[0][..100].maxIndex
~freqBins = ~freqBins.clump(~mags.size)
~freqBins[20].do {|i| if (i != 0.0) { ~lst.add(i) }}
Env.new(~lst.asArray.normalizeSum).plot
~freqBins[17..25].plot
~freqBins[25..35].plot
~freqBins[35..45].plot
~freqBins[55..65].plot
~freqBins[75..85].plot
~mags.size

~freqBins.size
s.freeAll
Buffer.freeAll




















//////////////////////////
(
~x = CtkSynthDef('testdef', {
    Out.ar(0, Pan2.ar(SinOsc.ar(440, 0, 0.2)))
})
)

n = ~x.note().play
n.free


(
{
    var trig, seq, decay_seq, decay_trig;
    trig = Impulse.kr(8);
    seq = Drand([
        Dseq([4,0,0,1,2,1,0,1]),
        Dseq([4,0,2,0,1,0,1,1]),
        Dseq([4,0,0,2,0,0,1,1]),
        Dseq([4,0,1,2,0,1,2,0]),
        Dseq([4,1,1,1,2,2,3,3]),
        Dseq([4,1,0,1,0,1,0,1])
    ], inf);
    decay_seq = Drand([
        Dseq([0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5]),
        Dseq([1, 0.5, 0.5, 0.5, 1, 0.5, 0.5, 0.5]),
        Dseq([1, 0.5, 0.5, 0.5, 1, 0.5, 1, 0.5])
    ], inf);
    trig = Demand.kr(trig, 0, seq * 0.4) * trig;
    decay_trig = Demand.kr(trig, 0, decay_seq * 0.4);
    
    {LPF.ar(PinkNoise.ar, 5000)}.dup * Decay.kr(trig, decay_trig);
}.play;
)

(
{
    var trig, seq;
    trig = Impulse.kr(4);
    seq = Drand([
        Dseq([4,0,0,1,2,1,0,1]),
        Dseq([4,0,2,0,1,0,1,1]),
        Dseq([4,0,0,2,0,0,1,1]),
        Dseq([4,0,1,2,0,1,2,0]),
        Dseq([4,1,1,1,2,2,3,3]),
        Dseq([4,1,0,1,0,1,0,1])
    ], inf);
    trig = Demand.kr(trig, 0, seq * 0.4) * trig;
    {LPF.ar(PinkNoise.ar, 2500)}.dup * Decay.kr(trig, 0.5);
}.play;
)


// size of FFT 
~size = 2048;
~res = List.new;

// add bus
(
~bus = Bus.audio(s, 1)
)

// add synthdef 
(
SynthDef(\simple, { arg freq=440, bus=0; 
    var out;
    out = SinOsc.ar(freq) * 0.75;
    Out.ar(bus, 0.5 * Pan2.ar(out))
}).add
)

s.freeAll

// start synth and set bus
x = Synth(\simple)
x.set(\bus, ~bus)
// debug bus channel
~bus.scope

// allocate buffer
~c = Buffer.alloc(s,~size);
~res = List.new
(
~c.getn(0, ~size, { arg frames; 
    var flop, sig;
    flop = frames.clump(2).flop;
    ~res = Complex(Signal.newFrom(flop[0]), Signal.newFrom(flop[1]));
})
)
~res.magnitude[..25].plot
~res.real.peak

~bus
// play the FFT via the bus 
~test_sound = { Out.ar(0, Pan2.ar(In.ar(~bus))) }.play(s, addAction: 'addToTail')
~test_sound.free

~c.plot

(
~write_fft = SynthDef(\writeFftFromBus, { 
    arg bus = 0;
    var in, chain, sig;
    in = In.ar(~bus);
    chain = FFT(~c, in);
}).add
)

~fft_synth = Synth.newPaused(\writeFftFromBus, addAction: \addToTail);

~fft_synth.run;
~fft_synth.free


s.freeAll
Buffer.freeAll

~c.plot

(
~sig = Signal.newClear(~size);
~c.getn(0, ~size, { arg frames; ~sig.waveFill({arg val,old,index;
    frames[index]} )}
);
)
~sig[0..100].plot


(
~c.getToFloatArray(action: { arg array;

    ~res = array;

})
)
~res.plot

~s = SynthBuilder.new(\test, s)
~buff = Buffer.alloc(s, s.sampleRate * 1);
(

~r = SynthDef(\basicRecBuff, { arg bufnum=0, bus=0;
    var in, rec;
    in = In.ar(bus);
    rec = RecordBuf.ar(in, bufnum, loop: 0);
    Out.ar(0, Pan2.ar(in))
}).add

)
Buffer.freeAll

~buff = Buffer.alloc(s, s.sampleRate * 1);
~rec = Synth.newPaused(\basicRecBuff, [\bufnum, ~buff, \bus, 0], addAction: \addToTail);
~rec.run;
~s.play(speed: 0.25, hits: 2, amp: 0.1)
~rec.free
~buff.plot
~buff.play

~buff.numFrames / 13000

~lst = List.new
~buff_chunk_size = 13000;

(
~buff.getn(0, ~buff_chunk_size, {|arr| ~lst.add(arr)  });
~buff.getn(~buff_chunk_size, ~buff_chunk_size, {|arr| ~lst.add(arr) });
~buff.getn(~buff_chunk_size * 2, ~buff_chunk_size, {|arr| ~lst.add(arr) });
~buff.getn(~buff_chunk_size * 3, ~buff.numFrames - (~buff_chunk_size * 3), {|arr| ~lst.add(arr) });

)
~lst[1].size
~lst = ~lst.flatten.abs
~lst.differentiate.abs.plot

~lst.size
s.freeAll
~c.free
~c.plot
s.freeAll




FreqScope.new
(
SynthDef("playBuf", {
    Out.ar(0, PlayBuf.ar(1, ~c, BufRateScale.kr(~c), loop: 0))
}).play;
)
~c.free

~start = 0
~end = ~start + ~size 
~c.plot
~c.play
~real
~real.plot

(
Window.closeAll;
~imag = Signal.newClear(~size);
~cosTable = Signal.fftCosTable(~size);
~complex = fft(~real, ~imag, ~cosTable);
~mag = ~complex.magnitude;
~phs = ~complex.phase + (pi/2) % (2pi);
)

~mag[1..]
p = ~mag[..20].plot.plotMode_(\bars)

p.domainSpecs_([1, 20, \exp].asSpec).refresh
~mag.size
s.sampleRate
x = 5/~size * s.sampleRate 
~get_freq_bin.value(s.sampleRate.div(2)).floor


~get_bin_freq.value(9).asInteger
(
~get_freq_bin = { arg freq;
    (freq / s.sampleRate) * ~size
}
)
(
~get_bin_freq = { arg bin;
    (bin / ~size) * s.sampleRate


}
)
(
{
    var in, real, imag, cosTable, complex;


    ~c.getn(0, ~size, ~real.waveFill({arg val,old,index; frames[index]}));
    imag = Signal.newClear(~size);

    cosTable = Signal.fftCosTable(~size);
    ~complex = fft(~real, imag, cosTable);


}.play(s)
)
fft
~res.clump(2)
~cplx = ~res.clump(2).collect {|i| Complex(i[0], i[1]) } 
~cplx[1..~cplx.size.div(2)].magnitude.plot
plot 
~res.size.div(2)
~res.clump(2).flop[0].abs[1..].plot
~res.asArray.plot
FreqScope.new
{Impulse.ar(1)}.play



~res[0][1].
// idx * sampleRate / c.size
~real.plot
~real.play
(

Routine({


    100.do {
        ~real.play;
        0.01.wait;
 
    }
   
}).play;

)

~res[1].plot
x.free
~res = ~res.magnitude[0]
~nodc = ~res[0].real[1..]
~nodc.plot
~sz = ~nodc.size.div(2)
~nodc[..~sz].reverse.abs.size
~res.size
~res.real.plot
~res.asArray.plot;
~res.maxValue;
~max = [-inf, 0];
~res.do({|i, idx| ~max = [~max[0].max(i), idx] })
~max
~res[33]
~res.maxIndex
~freq_res = s.sampleRate / 512 
21 * 93
~freq_res
(s.sampleRate / 2) / 46.875
~res.size / 2
~res[..512].plot;

~nodc.size
200 / ~freq_res

~res.maxIndex * ~freq_res

~a = Complex(1, -1)
~a.real
~a.magnitude
// the FFT return signal is -1, and for each starting window,
// it is the FFT buffer number

b = Buffer.alloc(s, 512); // allocate FFT buffer
b.bufnum; // note the buffer number

(
var dt = s.options.blockSize / s.sampleRate * 16; // plot 16 blocks
var min = -2, max = b.bufnum + 4;
// input is SoundIn, but we don't see this signal
{ FFT(b, SoundIn.ar) }.plot(dt, minval:min, maxval:max).plotMode_(\steps);
)


c = Buffer.alloc(s,2048,1);

(
x = { var in, chain, chainB, chainC;
    in = WhiteNoise.ar;
    chain = FFT(c, in);
    0.01 * Pan2.ar(IFFT(chain));
}.play(s);
)

(
Routine({
    3.do{arg i;
        c.getToFloatArray(action: { arg array;
            var z, x;
            z = array.clump(2).flop;
            // Initially data is in complex form
            z = [Signal.newFrom(z[0]), Signal.newFrom(z[1])];
            x = Complex(z[0], z[1]);

            { x.magnitude.plot('Initial', Rect(200, 600-(200*i), 700, 200)) }.defer
        });
        0.1.wait;
}}).play
)

x.free;

~sig.plot
(
var size = ~size;
var real, imag, cosTable, frqAmpPhs, complex;


imag = Signal.newClear(size); // zeros
cosTable = Signal.fftCosTable(size);

// Perform fft
complex = fft(~sig, imag, cosTable);

// Plot signals and spectrum
[
    ~sig,
    imag,
    (complex.magnitude) / size
]
.plot("fft", Window.screenBounds.insetBy(*200!2))
.axisLabelX_(["Signal (real, samples)", "Signal (imaginary, samples)", "FFT spectrum (bins)"])
.axisLabelY_(["Amplitude", "Amplitude", "Magnitude"])
.plotMode_([\linear, \linear, \steps]);
)

(
var size = ~size, real, imag, cosTable, complex;
var peakMag;

        // some harmonics
        // add a little noise

imag = Signal.newClear(size);
cosTable = Signal.fftCosTable(size);

complex = fft(~sig.normalizeMagnitude, imag, cosTable);
complex.asFreqSpectrum.magnitude.plot
//(complex.magnitude / 100)[..50].flat.plot

/* [real, imag, (complex.magnitude) / 100 ].flop.flat */
    /* .plot("fft: peak magnitude = %".format(peakMag), Rect(0, 0, 512 + 8, 500), numChannels: 3); */

)

~sig.plot
~c.plot
