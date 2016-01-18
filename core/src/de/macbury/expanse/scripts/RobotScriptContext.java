package de.macbury.expanse.scripts;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * This class extends standard {@link Context} and adds script pause and stopping
 */
public class RobotScriptContext extends Context {
  private boolean stop;

  protected RobotScriptContext(ContextFactory factory) {
    super(factory);
    setInstructionObserverThreshold(5);
  }

  public void stop() {
    stop = true;
  }

  public boolean isStop() {
    return stop;
  }
}
