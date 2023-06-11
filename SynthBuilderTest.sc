
    Server.default.options.inDevice_("Audient iD4");
    Server.default.options.outDevice_("Audient iD4");
s.quit
// s.boot;
s.waitForBoot({
    var tempoListener;


    TempoMap.new;

    tempoListener = CtkSynthDef('tempolistener',
        {
            var sig = In.ar(0);
            TempoMap.ar(sig);
        });

    tempoListener.note(addAction: \tail).play;

    ~a = SynthBuilder.new("james", s);
    ~a.play(hits: 2, speed: 1/64, env_times: [0.001, 0.01, 0.3].normalizeSum, env_levels: [0, 1, 0.9, 0], amp: 0.9);

    1.wait;

    CmdPeriod.run();

    (0.01).wait;

    ~f.value;



})



(
~f = {
var amps, diffs, start_idx, changes, tolerance, amt, end_idx, last_avg, next_avg;
var filtered_changes, c, heap;
var x, y, z;

x = List.new;
amt = 1;
amps = TempoMap.amps;
y = MaxMeterHeap.new;

z = 0;

amps.do({|i, index| if (index % 2 == 0) {x.add(i); y.insert(i, z, 0); z = z + 1}});

amps.asArray.plot;
x.asArray.plot;

y.findMax.postln;
y.deleteMax.postln;
y.deleteMax.postln;

y.deleteMax.postln;




// amps = amps.removeAt(y);
// z = amps.maxIndex;
// amps = amps.removeAt(z);


/* amps[start_idx..end_idx].do({ arg i, index; */
/*   var idx, last, next, last_diff, next_diff; */
/*  */
/*   idx = index + start_idx; */
/*  */
/*   last = 0; */
/*   next = 0; */
/*  */
/*   amt.do({|j|  */
/*     last = last + amps[idx-j]; */
/*     next = next + amps[idx+j];  */
/*   }); */
/*  */
/*   last_avg = (last / amt); */
/*   next_avg = (next / amt); */
/*    */
/*   // if the difference between the average and the current is what?? larger */
/*   // than some fixed constant c? */
/*   last_diff = (i - last_avg).abs; */
/*   next_diff = (i - next_avg).abs; */
/*  */
/*   if ((last_diff > c) && (next_diff > c), { */
/*  */
/*     changes.add(idx); */
/*  */
/*   }); */
/*  */
/*  */
/*  */
/* }); */
/*  */
/* changes.postln; */
/* // take max value among indices that are within 5 indices? */
/* changes.removeAllSuchThat({ */
/*   arg item, i, curr_val; */
/*   var diff, prev, next, prev_too_close, next_too_close, prev_bool, next_bool; */
/*  */
/*   next = changes[i+1]; */
/*   "item: ".post; item.postln; */
/*   "next: ".post; next.postln; */
/*   curr_val = amps[item]; */
/*   next_bool = false; */
/*  */
/*  */
/*   if (next.notNil, { */
/*     next_too_close = ((item - next).abs < 5); */
/*     "next_too_close: ".post; next_too_close.postln; */
/*  */
/*     if (next_too_close, { */
/*       var next_val; */
/*  */
/*       next_val = amps[next]; */
/*       "next_val is >= than curr? ".post; (next_val >= curr_val).postln; */
/*       next_bool = next_val >= curr_val */
/*     }) */
/*   }); */
/*   "next bool: ".post; next_bool.postln; */
/*   next_bool */
/*  */
/* }); */
/*  */
/* changes.postln; */
/*  */
/*  */
/*  */
/* changes.differentiate; */
/*  */
/*  */
/* changes.do({|i| filtered_changes.add(amps[i])}); */
/*  */


}
)

~f.value
/* ( */
/* ~r = Routine({ */
/*     loop { */
/*         ~a.play(speed: 1/4, hits: 2, freq: rrand(69, 78).midicps); */
/*         1.wait; */
/*     } */
/* }) */
/* ) */
/*  */
/* TempoClock.default.sched(0, ~r); */
/*  */
/*  */
/* // Just a normal 4 bar test */
/* ~a.play */
/*  */

(

var x, i, j;

i = 1;
j = 1;

x = case
{ i.isNil } { 1 }
{ j.isNil } { 2 }
{ true } { 3 };

x.postln;
)
(
var after, before;
before = [2, 5, 3, 6, 3, 7, 5, 8, 7];

before.plot;
after = ~smooth.value(before, 1);

before.size.postln;
after.size.postln;
after.asArray.plot;
before.postln;
after.postln;



)

(

var a = MaxMeterHeap.new;

a.insert(1, 2, 1);
a.insert(2, 1, 1);
a.insert(3, 1, 1);
// a.findMax();
a.deleteMax();
a.deleteMax();
a.deleteMax();
)

(
var x, y;



case
  { x.isNil && y.isNil } { 1 }
  { y.isNil } { 2 }
  { x.isNil } { 3 }
  { true } { 4 };
)

(
var x = List.new();

x.add(1);
x.add(2);
x.add(3);

x.maxIndex.postln;
x.removeAt(x.maxIndex);
x.maxIndex.postln;
x.removeAt(x.maxIndex);
x.maxIndex.postln;
x
)
