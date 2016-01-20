/**
* The instruction wait(); instructs the bot to wait for some seconds, according to the value written in brackets.
*/
function wait(seconds) {
  core.wait(seconds);
  core.yield();
}
