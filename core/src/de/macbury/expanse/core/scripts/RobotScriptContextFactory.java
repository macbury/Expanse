package de.macbury.expanse.core.scripts;

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

  /**
   * If script is required to stop, stop it, otherwise just sleep 10 miliseconds
   * @param cx
   * @param instructionCount
   */
  @Override
  protected void observeInstructionCount(Context cx, int instructionCount) {

  }

  @Override
  protected Context makeContext() {
    return new RobotScriptContext(this);
  }

}
