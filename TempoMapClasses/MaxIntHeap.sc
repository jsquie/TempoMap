
/* Heap with Array Representation  */
/* From node i, left child: (2 * i) + 1, right child: (2 * i) + 2, parent: (i-1) / 2 */

MaxIntHeap {
  var root, internalArr;
  var size, maxSize;

  *new {
    ^super.new.initMaxHeap()
  }

  initMaxHeap {
    root = TreeNode();
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

    "Afterwards: ".postln;
    internalArr.postln;
    ^result;
  }

  // percolate up
  insert { arg value;
    var current;
    // put new node in next position on bottom row to restore structure property
    // "Percolate up" to restore heap order property
    if (internalArr.size == (size - 1), {
      internalArr = internalArr ++ Array.newClear(10);
    });

    internalArr = internalArr.insert(size, value);

    // internalArr.postln;

    current = size;

    if (size > 0, {
      // this.prParent(current).postln;
      // internalArr.at(current).postln;
      // internalArr.at(this.prParent(current)).postln;
      while ({ current > 0 }, { 
        if (internalArr.at(current) > internalArr.at(this.prParent(current)), {
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
    internalArr.postln;

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
    if ((pos > (size.div(2))) && (pos <= size), {
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

    var leftChild, rightChild, curr;

    if (this.prIsLeaf(pos), {
      ^nil
    });

    curr = internalArr.at(pos);
    leftChild = internalArr.at(this.prLeftChild(pos));
    rightChild = internalArr.at(this.prRightChild(pos));

/*     "Curr: ".post; */
    /* curr.post; */
    /* " left: ".post; */
    /* leftChild.post; */
    /* " right: ".post; */
    /* rightChild.postln; */
    /* internalArr.postln; */
    

    if (leftChild != nil, {
      if (curr < leftChild, {
        this.prSwap(pos, this.prLeftChild(pos));
        this.prMaxHeapify(this.prLeftChild(pos));
      });
    });

    if (rightChild != nil, {
      if (curr < rightChild, {
        this.prSwap(pos, this.prRightChild(pos));
        this.prMaxHeapify(this.prRightChild(pos));
      });
    });


  }

}



