MeterArr {
  var <internalArr;
  var <size;
  
  const threeStandardDevs = 0.015867855243157;
  const meterResolution = 16;

  *new {
    ^super.new.initMeterArr();
  }

  initMeterArr {
    internalArr = Array.new(meterResolution);
    size = 0;
  }

  add { arg key, value;
    var wasFound;

    wasFound = this.prFindIndex(key);

    if (wasFound != nil, {
      var sum, avg;

      sum = internalArr.at(wasFound)[2] + key;
      avg = sum / value;

      internalArr = internalArr.put(wasFound, [key, value, sum, avg]);
      
    }, {
      internalArr = internalArr.add([key, value, key, key]);
      size = size + 1;
    });

  }

  maxTime {
    var diff;

    diff = internalArr.at(1)[0] - internalArr.at(0)[0];
    ^(internalArr.at(meterResolution - 1)[0] + threeStandardDevs + diff)
  }

  orgTime {
    ^internalArr.at(0)[0]
  }

  find { arg key;

    var start, end;

    start = 0;
    end = size - 1;

    while({ start <= end }, {
      // body func
      var mid;
      var thisKv;

      mid = ((start + end) / 2).floor;

      thisKv = internalArr.at(mid);

      if (this.prEqual(thisKv[0], key), {
        ^thisKv[1]
      });

      if (thisKv[0] < key, {
        start = mid + 1;
      });

      if (thisKv[0] > key, {
        end = mid - 1;
      });

    });
    ^nil

  }

  prFindIndex { arg key;
    var start, end;

    start = 0;
    end = size - 1;

    while({ start <= end }, {
      // body func
      var mid;
      var thisKv;

      mid = ((start + end) / 2).floor;

      thisKv = internalArr.at(mid);

      if (this.prEqual(thisKv[0], key), {
        ^mid
      });

      if (thisKv[0] < key, {
        start = mid + 1;
      });

      if (thisKv[0] > key, {
        end = mid - 1;
      });

    });
    ^nil

  }

  totalDiff {
    ^((internalArr.at(meterResolution - 1)[0] + threeStandardDevs) - internalArr.at(0)[0]);
  }

  prEqual { arg key, other;
    ^((key - other).abs <= threeStandardDevs)
  }

  print {
    internalArr.postln;
  }

  getArr {
    ^internalArr;
  }

}
