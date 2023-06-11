
/* Heap with Array Representation  */
/* From node i, left child: (2 * i) + 1, right child: (2 * i) + 2, parent: (i-1) / 2 */

MaxMeterHeap {
  var size, maxSize, internalArr;

  *new {
    ^super.new.initMaxHeap()
  }

  initMaxHeap {
    internalArr = Array.newClear(10);
    size = 0;
  }

  findMax {
    ^internalArr.at(0);
  }

  // percolate down
  deleteMax {
    var result, current;

    result = internalArr.at(0);

    if (size > 0, {

      // replace max item with last index
      internalArr = internalArr.put(0, internalArr.at(size - 1));
      internalArr = internalArr.put(size - 1, nil);
      size = size - 1;
      this.prMaxHeapify(0)

    });

    // "Afterwards: ".postln;
    // internalArr.postln;
    ^result;
  }

  // percolate up
  insert { arg key, value, metArrIndex;
    var current;
    // put new node in next position on bottom row to restore structure property
    // "Percolate up" to restore heap order property
    if (internalArr.size == (size - 1), {
      internalArr = internalArr ++ Array.newClear(10);
    });

    // check that the internalArr.at(pos) has 0, 1, or more elements
    internalArr = internalArr.insert(size, [key, value, metArrIndex]);

    current = size;

    if (size > 0, {
      // this.prParent(current).postln;
      // internalArr.at(current).postln;
      // internalArr.at(this.prParent(current)).postln;
      while ({ current > 0 }, { 
        if (
          internalArr.at(current)[0] > internalArr.at(this.prParent(current))[0], {
          // "Here".postln;
          this.prSwap(current, this.prParent(current));
          // "Here after prSwap".postln;
          // internalArr.postln;
          // "current after: ".post;
          // current.postln;
        });
          current = this.prParent(current);
      });
    });

    size = size + 1;

  }

  prParent { arg pos;
    var decrement, result;

    decrement = pos - 1;

    result = decrement.div(2);
    
    ^result;

  }

  prLeftChild { arg pos;
    var result;

    result = (2 * pos) + 1;

    ^result;
  }

  prRightChild { arg pos;
  var result;

  result = (2 * pos) + 2;

  ^result;
  }

  prIsLeaf { arg pos;

    var left, right;

    left = internalArr.at(this.prLeftChild(pos));
    right = internalArr.at(this.prRightChild(pos));

    if (left.isNil && right.isNil, {
      ^true;
    });
    ^false
  }

  prSwap { arg fpos, spos;
    var tmp;

    tmp = internalArr.at(fpos);


    // what is at fpos, we're replacing with whats at spos
    internalArr = internalArr.put(fpos, internalArr.at(spos));
    // what is at spos we're replacing with tmp
    internalArr = internalArr.put(spos, tmp);
  }

  prMaxHeapify { arg pos;

    var leftChild, rightChild, curr, leftChildPos, rightChildPos;

    if (this.prIsLeaf(pos), {
      ^nil
    });

    leftChildPos = this.prLeftChild(pos);
    rightChildPos = this.prRightChild(pos);

    curr = internalArr.at(pos)[0];
    "curr: ".post; curr.postln;
    leftChild = internalArr.at(leftChildPos);
    // "leftChild: ".post; leftChild.postln;
    rightChild = internalArr.at(rightChildPos);
    // "rightChild: ".post; rightChild.postln;

    case
    { leftChild.isNil && rightChild.isNil } { }
    { rightChild.isNil } {
      var lc = leftChild[0];

      if (curr < lc) {
        "swapping curr and left child cus right child is nil".postln;
        this.prSwap(pos, this.prLeftChild(pos));
        this.prMaxHeapify(this.prLeftChild(pos));
      }
      { leftChild.isNil } {  
        var rc = rightChild[0];
        if (curr < rc) {
          "swapping curr and right child cus left child is nil".postln;
          this.prSwap(pos, this.prRightChild(pos));
          this.prMaxHeapify(this.prRightChild(pos));
        }
      }
      { true } {
        var rc = rightChild[0];
        var lc = leftChild[0];
        if (lc > rc, {
          if (curr < lc, {
            "swapping curr and left child cus left is more than curr".postln;
            this.prSwap(pos, this.prLeftChild(pos));
            this.prMaxHeapify(this.prLeftChild(pos));
          });
        }, {
          if (curr < rc, {
            "swapping curr and right child cus right child is more than curr".postln;
            this.prSwap(pos, this.prRightChild(pos));
            this.prMaxHeapify(this.prRightChild);
          });
        })
      }
    };

    // if left is less than right, target is left
    // else, right is target
    // if curr is less than target, swap, return pos
    // if curr is less than other target, swap

    /* if (leftChild.notNil && rightChild.notNil, { */
    /*   if (leftChild > rightChild, { */
    /*     if (curr < leftChild, { */
    /*       this.prSwap(pos, this.prLeftChild(pos)); */
    /*       this.prMaxHeapify(this.prLeftChild(pos)); */
    /*     }); */
    /*   }, { */
    /*     if (curr < rightChild, { */
    /*       this.prSwap(pos, this.prRightChild(pos)); */
    /*       this.prMaxHeapify(this.prRightChild); */
    /*     }); */
    /*   }) */
    /* }); */

  }

}



