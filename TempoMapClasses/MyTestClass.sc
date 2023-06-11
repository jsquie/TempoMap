MyTestClass {
  var <>x = 0;

  *new { arg x;
    ^super.newCopyArgs(x)
  }
}

