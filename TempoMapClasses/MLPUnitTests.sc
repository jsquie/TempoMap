MLPUnitTests : UnitTest {

  setUp {

  }
  test_forward_prop {
    var output, error, mlp;
    var before_weights, after_weights;
    var input = [0.3, 0.1];
    var target = [0.4];

    mlp = MLP.new(2, [5], 1);
    before_weights = List.copyInstance(mlp.prDebugWeights);
    output = mlp.forward_propagate(input);
    this.assert(output.notNil, "get a result for output");
    error = target - output;
    mlp.back_propagate(error);
    mlp.gradient_descent(1);
    after_weights = mlp.prDebugWeights;
    this.assert(before_weights != after_weights, "weights should have changed")

  }

  test_trainOnArithmetic {
    var mlp, items, hold_out_items, targets, hold_out_targets;
    var training_error, hold_out_error;

    // init mlp
    mlp = MLP.new(2, [5], 1);

    // create data sets
    items = Array.fill(1000, { [5.rand/10, 5.rand/10] });
    targets = Array.fill(1000, {|i| [items[i][0] + items[i][1]] });

    // hold out 50 items
    hold_out_items = items[..50];
    hold_out_targets = targets[..50];

    // adjust data set to exclude held out items
    items = items[50..];
    targets = targets[50..];

    this.assert(items.size == targets.size, "asserting test quality");
  
    // train the data 
    training_error = mlp.train(items, targets, 200, 1);

    hold_out_error = this.prTestHoldOut(
      mlp, hold_out_items, hold_out_targets, training_error);

    this.assert(
      this.prCompareTrainingHoldOutError(
        training_error, hold_out_error, hold_out_items), 
        "training error is within acceptable bounds of held out data");
    ^mlp

  }

  prCompareTrainingHoldOutError { arg training_error, hold_out_error, hold_out_items;

    ^(
      (training_error - (hold_out_error / hold_out_items.size)).abs < (training_error * 20)
    );
  }

  prTestHoldOut { arg mlp, hold_out_items, hold_out_targets, training_error;
    var hold_out_error = 0;

    hold_out_items.do {|i, idx|
      var output, error;
      output = mlp.forward_propagate(i);
      error = hold_out_targets[idx] - output;
      hold_out_error = hold_out_error + mlp.prMse(hold_out_targets[idx], output);
    };
    ^hold_out_error
  }

  test_xor {
    var mlp, items, hold_out_items, targets, hold_out_targets;
    var training_error, hold_out_error, num_hold_outs;
    var training_data_size = 1000, training_epochs = 300, learning_rate = 1;
    var training_data_hold_out_perc = 0.1;

    num_hold_outs = (training_data_size * training_data_hold_out_perc).div(1);

    // init mlp
    mlp = MLP.new(2, [3, 2], 1);

    // init training data
    items = Array.fill(training_data_size, { Array.fill(2, { 2.rand }) });
    targets = items.collect {|i| [(i.sum == 1).if(1, 0)] };

    hold_out_items = items[..num_hold_outs];
    hold_out_targets = targets[..num_hold_outs];

    // exclude hold_out_items and hold_out_targets from training data
    items = items[num_hold_outs..];
    targets = targets[num_hold_outs..];

    // train the MLP
    training_error = mlp.train(items, targets, training_epochs, learning_rate);

    // compare training result to held out data
    hold_out_error = this.prTestHoldOut(
      mlp, hold_out_items, hold_out_targets, training_error);

    this.assert(
      this.prCompareTrainingHoldOutError(
        training_error, hold_out_error, hold_out_items), 
        "training error is within acceptable bounds of held out data");

    // make some predictions
    "we predict that ".post; hold_out_items[0][0].post; " xor ".post; 
    hold_out_items[0][1].post; " == ".post; 
    mlp.forward_propagate(hold_out_items[0]).postln; 
    ^mlp

  }

  test_miniFFTNoteClassifier {
    var mlp, items, hold_out_items, targets, hold_out_targets;
    var training_error, hold_out_error, num_hold_outs;
    var training_data_size = 1000, training_epochs = 50, learning_rate = 0.1;
    var training_data_hold_out_perc = 0.1;

    num_hold_outs = (training_data_size * training_data_hold_out_perc).div(1);

    // init mlp
    // identify 5 different "notes" 
    mlp = MLP.new(10, [20], 5);

    // init training data
    items = Array.fill(training_data_size, {
      var idx = (8.rand + 1);
      var i1 = (1 - 0.25.rand);
      var i2 = 0.25.rand, i3 = 0.25.rand;
      var arr = Array.zeroFill(10);

      arr = (arr[idx] = i1);
      arr = (arr[idx - 1] = i2);
      arr[idx + 1] = i3
    });

    targets = items.collect {|i|
      var idx = i.maxIndex.div(2); 
      Array.zeroFill(5)[idx] = 1.0
    };

    this.assert(items[0].size == 10, "assert that input data is correct size");
    this.assert(targets[0].size == 5, "assert that target data is correct size");

    hold_out_items = items[..num_hold_outs];
    hold_out_targets = targets[..num_hold_outs];

    // exclude hold_out_items and hold_out_targets from training data
    items = items[num_hold_outs..];
    targets = targets[num_hold_outs..];

    // train the MLP
    training_error = mlp.train(items, targets, training_epochs, learning_rate);

    // compare training result to held out data
    hold_out_error = this.prTestHoldOut(
      mlp, hold_out_items, hold_out_targets, training_error);

    this.assert(
      this.prCompareTrainingHoldOutError(
        training_error, hold_out_error, hold_out_items), 
        "training error is within acceptable bounds of held out data");

    ^mlp
  }

  test_512FFTsize { arg training_epochs = 100, hidden_layer=[15], learning_rate=1.0;
    var mlp, items, hold_out_items, targets, hold_out_targets;
    var training_error, hold_out_error, num_hold_outs, pred_1, pred_2;
    var training_data_size = 1000; 
    var training_data_hold_out_perc = 0.1, rfft_size = 2048;
    

    num_hold_outs = (training_data_size * training_data_hold_out_perc).div(1);
    
    // init mlp
    // identify 5 different "notes" 
    mlp = MLP.new(30, hidden_layer, 30);

    // init training data
    targets = List.new(training_data_size);
    items = Array.fill(training_data_size, 
      { var rand_midi_note = (30.rand + 60);
      targets.add(Array.zeroFill(30).[rand_midi_note - 60] = 1.0);
      this.prCreateSinFFT(rfft_size, rand_midi_note, 5.2.rand)[..29].normalize  
    });

    targets = targets.asArray;


    //this.assert(items[0].size == 257, "assert that input data is correct size");
    //this.assert(targets[0].size == 30, "assert that target data is correct size");

    hold_out_items = items[..num_hold_outs];
    hold_out_targets = targets[..num_hold_outs];

    // exclude hold_out_items and hold_out_targets from training data
    items = items[num_hold_outs..];
    targets = targets[num_hold_outs..];

    
    // train the MLP
    training_error = mlp.train(items, targets, training_epochs, learning_rate);

    // compare training result to held out data
    hold_out_error = this.prTestHoldOut(
      mlp, hold_out_items, hold_out_targets, training_error);

    this.assert(
      this.prCompareTrainingHoldOutError(
        training_error, hold_out_error, hold_out_items), 
        "training error is within acceptable bounds of held out data");

    // print a prediction for middle C
    pred_1 = mlp.forward_propagate(
      this.prCreateSinFFT(rfft_size, 65, 0.1)[..29].normalize).maxIndex;
    pred_2 = mlp.forward_propagate(
      this.prCreateSinFFT(rfft_size, 77, 0.1)[..29].normalize).maxIndex;
    this.assert(pred_1 == 5, "should be 5");
    pred_1.postln;
    this.assert(pred_1 == 17, "should be 17");
    pred_2.postln;
    ^mlp

  }

  prGet_freq_func { arg midi, size;
    midi.midicps / (48000 / size)
  }

  prCreateSinFFT { arg size=512, midi_note=60, noise_amt=0.01;
    var rfftsize, real, cosTable, complex;

    real = Signal.newClear(size);
    real.waveFill({
      arg x;
      sin(x * (midi_note.midicps / (48000 / size)))
    }, 0, 2pi);

    real.overDub(Signal.fill(size, { noise_amt.bilinrand }));

    rfftsize = (size / 2) + 1;

    cosTable = Signal.rfftCosTable(rfftsize);

    complex = rfft(real, cosTable);
    ^complex.magnitude
  }

}
