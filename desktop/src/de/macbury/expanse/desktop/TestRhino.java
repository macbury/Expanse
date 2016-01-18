package de.macbury.expanse.desktop;

import de.macbury.expanse.core.scripts.RobotScriptContextFactory;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * Created on 18.01.16.
 */
public class TestRhino {

  public static void main (String[] arg) {
    RobotScriptContextFactory.init();

    ScriptRunner runner = new ScriptRunner();
    runner.start();

    ScriptRunner runner2 = new ScriptRunner();
    runner2.start();
    try {
      Thread.sleep(2000);
      runner.finish();
      runner2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
}
