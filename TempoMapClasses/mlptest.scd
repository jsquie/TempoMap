(
~a = MLP.new(2, [5], 1);
~input = Array.fill(10, { [5.rand/10, 5.rand/10] });
~targets = ~input.collect {|i| [i[0] + i[1]] };

~input.do {|in, idx|
    var out, e;

    out = ~a.forward_propagate(in);
    e = ~targets[idx] - out;
    ~a.back_propagate(e, true);
    ~a.gradient_descent(1, true);
};

~t = ~input[0];
~t.postln;
~a.forward_propagate(~t).postln;
)

(
~a = MLP.new(2, [5], 1);
~items = Array.fill(1000, { [5.rand/10, 5.rand/10] });
~hold_out_items = ~items[..50];
~targets = Array.fill(1000, {|i| [~items[i][0] + ~items[i][1]] });
~hold_out_targets = ~targets[..50];
~items = ~items[50..];
~targets = ~targets[50..];
~items.size == ~targets.size;
)
~a.train(~items, ~targets, 50, 0.1);

(
var err_sum = 0;
~hold_out_items.do {|i, idx|
    var output, error;
    output = ~a.forward_propagate(i);
    error = ~hold_out_targets[idx] - output;
    err_sum = err_sum + ~a.prMse(~hold_out_targets[idx], output)
};

"error: ".post; (err_sum / ~hold_out_items.size).postln;

)

(
MLPUnitTests.new.runTestMethod(MLPUnitTests.findMethod(\test_trainOnArithmetic))
)

MLPUnitTests.new.runTestMethod(MLPUnitTests.findMethod(\test_xor))
MLPUnitTests.new.runTestMethod(MLPUnitTests.findMethod(\test_miniFFTNoteClassifier))


MLPUnitTests.new.runTestMethod(MLPUnitTests.findMethod(\test_512FFTsize))

~a = MLPUnitTests.new
~b = ~a.test_512FFTsize(100, [15], 10)
(
~tests = [
    (num_epoc: 5, hidden_layers: [15], mlp: {|self|
        var a = MLPUnitTests.new;
        self.a.test_512FFTsize(self.num_epoc, self.hidden_layers)}, errors: {|self|
            self.errors.asArray.plot })

]
)
~tests[0].mlp
~tests[0].
Thunk

(
(a: 3).at(\a)
)
IdentityDictionary.new(n:8, proto:nil, parent:nil, know:false)
[\yeet,1].as(Dictionary)[\yeet]
Association
Dictionary
[]
~b = 5.collect {|i| ~a.test_512FFTsize(25, [5 + (i * 5)]).errors.asArray }
~b.errors.asArray.plot
~b = Array.fill(10, { |i| ~a.prCreateSinFFT(2048, 60 + i, 0.01)[..30].normalize })

~a.prCreateSinFFT(2048, 60, 5.5).plot

5.4.rand
Array.fill(30, { 30.rand}).maxIndex
~b.plot
a.midicps
76.midicps
Server
SimpleNumber.new(75)

512.div(2)
~b.learning_rates.asArray.plot
~b.errors.asArray.plot
(
var is, ts;
is = Array.fill(5, { 
    var idx = (8.rand + 1); 
    var i1 = (1 - 0.25.rand); 
    var i2 = 0.25.rand;
    var i3 = 0.25.rand;
    var arr = Array.zeroFill(size:10);

    arr = arr[idx] = i1;
    arr = arr[idx - 1] = i2;
    arr = arr[idx + 1] = i3;

});
is.postln;

ts = is.collect {|i| 
    var idx = i.maxIndex.div(2);
    Array.zeroFill(5)[idx] = 1.0
};
)

0.25.rand
(1 - 0.25.rand)
(9.rand + 1)

(
~x = { |y| 
    (y >= 0).if(1, 0)
};
)

~x.value(1)
(
(1.sum3rand).max(0)
)



(

for (5, 0) { |i, idx|

    idx.postln;

}


)

Signal.fftCosTable(512).plot

(
var rfftsize, real, imag, cosTable, complexFft, complexRfft, real2;
var numharms, amps, phases, harms, list, size = 50, fft_size = 1024;

rfftsize = size/2 + 1;

numharms = 1;
harms = Array.rand(numharms, 1, 16);
amps = Array.rand(numharms, 0, -12).dbamp;
phases = Array.rand(numharms, 0, 2pi);

list = [harms, amps, phases].lace.clump(3);
real = Signal.newClear(fft_size).sineFill2(list);
real.overDub(Signal.fill(fft_size, { 0.2.bilinrand }));
real2 = Signal.newClear(fft_size).sineFill2(list); 
real2 = real2.resize(real2.size.nextPowerOfTwo);
plot(
    [real.zeroPad(fft_size.nextPowerOfTwo), real2] 
);

imag = Signal.newClear(fft_size);
cosTable = Signal.fftCosTable(fft_size);

complexFft = fft(real, imag, cosTable);
complexRfft = complexFft.real.fftToRfft(complexFft.imag);
)

(
var size = 512, rfftsize, real, cosTable, complex, freq;

freq = { arg midi;
    midi.midicps / (s.sampleRate / size);
};
freq.value(60).postln;

real = Signal.newClear(size);
real.waveFill({
    arg x;
    sin(x * freq.value(60))
}, 0, 2pi);
real.overDub(Signal.fill(size, { 0.2.bilinrand }));

rfftsize = size/2 + 1;
cosTable = Signal.rfftCosTable(rfftsize);

complex = rfft(real, cosTable);
complex.magnitude[..50].plot

)
s.sampleRate


(
var rand = (30.rand + 60);
Array.zeroFill(30)[rand - 60] = 1.0;

)
30.rand + 60

s.boot;

~b = 10.collect { Buffer.alloc(s, 1024); };

(
var sig, wt;

10.do {|i|
sig = Signal.newClear(1024);
sig.waveFill({
    arg x, old;
    sin(x * (i + 1));
}, 0, 2pi);
wt = sig.asWavetable;
~b[i].loadCollection(wt);
}
)

60.midicps / (s.sampleRate / 512)
59.midicps

60.midicps 
80.midicps
(
261 / (s.sampleRate / 512) 
)

(
{
    var sig;
    sig = 10.collect {|i| 
        Osc.ar(~b[i], MouseX.kr(100, 1000, 1) * (1 - 0.01.bilinrand) + LFGauss.ar(duration:1, width:0.1, iphase:0.0, loop:1, doneAction:0), mul:
        (0.2/10)!2 ) * MouseY.kr(0.01, 10, 1)
    };
    sig.sum;
}.play;

)

(
var golden = (2 / (1 + sqrt(5)));
{
    FreeVerb.ar(in:
    LPF.ar(
        WhiteNoise.ar(
            mul:(golden**4)!2, add:0.1) * 
                SinOsc.kr(golden**7, mul: golden**3, add: 1.0), 
            freq: golden**(-11) + SinOsc.kr(golden**10, 1, golden**(-13))
        ),
    mix:SinOsc.kr(0.02, 0, 0.5, 0.75), room:1.5, damp:0.5, mul:1.1, add:0.0)
    }.play
)

(
{SinOsc.kr(0.5, 0, 0.5, 1).poll}.play
)

(
{SinOsc.ar(MouseX.kr(10, 1000), 0, MouseY.kr(0.1, 10) * 0.2!2, 0)}.play
)

(


FreeVerb2.ar(in:nil, in2:nil, mix:0.33, room:0.5, damp:0.5, mul:1.0, add:0.0)

FreeVerb.ar(in:nil, mix:0.33, room:0.5, damp:0.5, mul:1.0, add:0.0)


)


~golden = (2 / (1 + sqrt(5)))
~golden**(-17)

(
((1 + sqrt(5))/2)**(-2)
)

(

(2 / (1 + sqrt(5)))**2

)
