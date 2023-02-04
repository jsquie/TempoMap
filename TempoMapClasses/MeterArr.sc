MeterArr {
  var internalArr;
  var size;
  
  const threeStandardDevs = 0.015465;
  const meterResolution = 16;

  *new {
    ^super.new.initMeterArr();
  }

  initMeterArr {
    internalArr = Array.new(meterResolution);
    size = 0;
  }

  add { arg key, value;
    internalArr = internalArr.add([key, value]);
    size = size + 1;
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

  prEqual { arg key, other;
    ^((key - other).abs <= threeStandardDevs)
  }

  print {
    internalArr.postln;
  }


  

}
