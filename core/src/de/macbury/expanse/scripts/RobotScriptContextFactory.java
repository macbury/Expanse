package de.macbury.expanse.scripts;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * This class wraps {@link ContextFactory} to return {@link RobotScriptContext}. Additionaly it track instruction invocations and
 * if {@link RobotScriptContext#isStop()} then throws {@link ManualScriptStopException}
 */
public class RobotScriptContextFactory extends ContextFactory {
  public static boolean initialized = false;

  public static void init() {
    if (!initialized) {
      ContextFactory.initGlobal(new RobotScriptContextFactory());
      initialized = true;
    }
  }

  @Override
  protected void observeInstructionCount(Context cx, int instructionCount) {
    if (RobotScriptContext.class.isInstance(cx)) {
      RobotScriptContext robotScriptContext = (RobotScriptContext)cx;
      if (robotScriptContext.isStop()) {
        throw new ManualScriptStopException();
      }
    }
  }

  @Override
  protected Context makeContext() {
    return new RobotScriptContext(this);
  }

  public class ManualScriptStopException extends RuntimeException {

  }
}
