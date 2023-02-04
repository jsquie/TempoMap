
/* THIS IS HOW WE WILL CREATE SAMPLE TIMES TO TEST WITH*/
(
// sample times
var root;

root = 1;

~times = Array.new(16);

16.do({ |i|
    ~times = ~times.add(root + (root * (0.125 * i)));
});

~times.postln;


/* THIS IS HOW WE WILL CREATE THE METER REPRESENTATION */

  
~meters = Array.new(16);

4.do({ |i|
    var onesDigit;

    onesDigit = (i % 4) + 1;
    
    4.do({ |x|
        var subDigit;

        subDigit = (x % 4) * 0.1;

        ~meters.add(onesDigit + subDigit);

    })

});

~meters.postln;



/* THIS IS HOW WE CREATE THE KEY VALUE PAIRS FOR TESTING */ 

~keyValuePairs = Array.new(16);

16.do({ |i|

    ~keyValuePairs.add([~times.at(i) + (0.015465.sum3rand), ~meters.at(i)]);
});

~keyValuePairs.postln;
)

// TESTING
(
~meterArr = MeterArr.new();



16.do({ |i| 
    ~meterArr.add(~keyValuePairs.at(i)[0], ~keyValuePairs.at(i)[1]);
});

~meterArr.print;

)



(
16.do({ |i|
    ~meterArr.find(~times.at(i)).postln;
})
)

(
var arr;

arr = MeterArr.new();

arr.prEqual(1, 0.99);

)

(

var arr, root;
var arrInsertOrder;
var keyValues;
~tree = AVLNavTree.new();

arrInsertOrder = [7, 3, 11, 13, 9, 5, 1, 0, 2, 4, 6, 8, 10, 12, 14,15];
keyValues = Array.newClear(16);
root = 1;

arr = Array.new(16);

16.do({ |i|
    arr = arr.add(root + (root * (0.125 * i)));
});

arr.postln;

16.do({ |i|
    var key, value;

    value = i % 16; 
    key = arr.at(i);
    keyValues = keyValues.insert(i, [key, value]);
});

16.do({ |i|
    var thisKeyValue;

    thisKeyValue = keyValues.at(arrInsertOrder.at(i));

    "i: ".post;
    i.post;
    " keyValue: ".post;
    thisKeyValue[0].post;
    ", ".post;
    thisKeyValue[1].postln;

    ~tree.insert(thisKeyValue[0], thisKeyValue[1]);
});

~tree.getItems;

)

(
// simple test 
var binTree;
var keys, values;

binTree = AVLNavTree.new();

keys = Array.fill(7, {|i| 100.rand});
values = Array.fill(7, {|i| 100.rand});

7.do({ |i|
    binTree.insert(keys[i], 1);

});

binTree.getItems;

)




