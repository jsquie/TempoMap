MLP {
  var num_inputs, hidden_layers;
  var num_outputs, weights, activations, derivatives;
  var <>errors, <>learning_rates;
  

  *new { arg numInputs = 3, hiddenLayers = [3, 3], numOutputs=2;
    ^super.new.initMLP(numInputs, hiddenLayers, numOutputs)
  }

  initMLP { arg numInputs, hiddenLayers, numOutputs;
    var layers;

    num_inputs = numInputs;
    hidden_layers = hiddenLayers;
    num_outputs = numOutputs;
    learning_rates = List.new();
    errors = List.new();

    // create generic representation of the layers
    layers = [numInputs] ++ hiddenLayers ++ [numOutputs];

    weights = List.new;
    (layers.size - 1).do {|i|
      var w;
      w = Matrix.fill(layers[i], layers[i+1], { 0.9.rand });
      weights.add(w);
    };

      derivatives = List.new;
    (layers.size - 1).do { |i|
      var a = Matrix.newClear(layers[i], layers[i+1]);
      derivatives.add(a);
    };

    activations = List.new;
    layers.size.do {|i|
      var a = Matrix.newClear(1, layers[i]);
      activations.add(a);
    };


    /* 
    weights.postln;
    weights.size.postln;
    activations.postln;
    activations.size.postln;
    derivatives.postln;
    derivatives.size.postln;
    */

  }

  forward_propagate { arg inputs;
    var curr_activations;

    // assert input shape is same as activations shape?

    if (inputs.size != num_inputs) 
    { Error("Input size does not match MLP input size").throw };

    curr_activations = inputs; 
    activations[0] = inputs; 

    weights.do {|w, idx| 
      var net_inputs;

      net_inputs = (curr_activations * w).sum; 

      curr_activations = net_inputs.collect {|i| this.prSigmoid(i) };

      activations[idx + 1] = curr_activations;

    };
    // return output layer activation
    ^curr_activations.flatten
  }

  back_propagate { arg error, verbose=false;
    var new_error;

    
    new_error = Matrix.with([error]);

    for (derivatives.size - 1, 0) { |i, idx|
      var prev_act, curr_act, delta, apply_deriv_sig;

      prev_act = activations[i+1];

      apply_deriv_sig = Matrix.with(
          [prev_act.collect {|i| this.prSigmoidDerivative(i) }]
      );

      delta = new_error.asArray * apply_deriv_sig.asArray; 
      curr_act = activations[i];

      // dot prod 
      if (verbose) {
          "----------------".postln;
          curr_act.postln;
          delta.postln;
          "----------------".postln;
          "deriv result:".postln;
          derivatives[i].postln;
          "----------------".postln;
          "----------------".postln;
      };
      derivatives[i] = curr_act * delta;

      // dot between delta and weights 
      new_error = Matrix.with(delta) * weights[i].flop;
      }
      ^new_error

  }

  gradient_descent { arg learning_rate, verbose=false;

      // loop through all weight matrices
      weights.size.do {|i|
          var scaled_d;
          var curr_w = weights[i];
          var curr_d = derivatives[i];

          if (verbose) {
              "before weights:".postln; curr_w.postln;
              "before derivs:".postln; 
              curr_d.do {|i| i.postln;};

          };


          scaled_d = curr_d * learning_rate;
          scaled_d = Matrix.with(scaled_d);
          //curr_w.shape.postln; scaled_d.shape.postln;

          curr_w = curr_w + scaled_d;
          weights[i] = curr_w;
          if (verbose) {
              "updated weights:".postln; weights[i].postln;
          };
      };

  }

  train { arg inputs, targets, epochs, learning_rate;
      var total_error = 0;


      if (inputs.size != targets.size) 
      { Error("Number of inputs must match number of targets").throw};

      epochs.do {|i|

          var sum_error = 0;
          var rand = 0.2.rand;
          var learning_rate_variance = (learning_rate * rand);
          var curr_learning_rate = (learning_rate + learning_rate_variance) / (i + 1);
          curr_learning_rate = curr_learning_rate.max(0.05);
          if (0.99.rand > 0.8) { 
              curr_learning_rate = curr_learning_rate * (0.99.rand + 1);
          };
          learning_rates.add(curr_learning_rate);

          inputs.do {|in, j|
              var output, error;
              var curr_target = targets[j];

              // "curr epoc: ".post; j.postln;
              // in.postln;
              // curr_target.postln;

              output = this.forward_propagate(in);
              error = curr_target - output;

              // "output: ".post; output.postln;
              // "error: ".post; error.postln;
              this.back_propagate(error);
              this.gradient_descent(curr_learning_rate);

              sum_error = sum_error + this.prMse(curr_target, output);
          };

          if (i % 1 == 0) {
              "Error: ".post; 
              (sum_error / inputs.size).post; " at epoch: ".post; i.postln; 
          };
          errors.add(sum_error / inputs.size);
          

          total_error = (sum_error / inputs.size);

      };
      "Total Error: ".post; total_error.postln;
      ^total_error

  }

  prMse { arg target, output;

      ^((target - output)**2).mean;

  }

  prSigmoid {
      arg x;
      ^(1 / (1 + (-1 * x).exp))
  }

  prSigmoidDerivative {
      arg x;
      ^(x * (1 - x))
  }

  prReLU {
      arg x;
      ^x.max(0)
  }

  prReLUDerivative {
      arg x;
      ^(x >= 0).if(1, 0)
  }

  prDebugWeights {
      ^weights
  }





}

