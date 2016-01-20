function move(distance) {
  log("Sending telegram");
  telegramTest();
  log("Pausing script");
  log(yield());
  log("Finished!");
}

