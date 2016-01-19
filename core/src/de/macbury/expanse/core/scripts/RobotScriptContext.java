package de.macbury.expanse.core.scripts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.mozilla.javascript.*;

/**
 * This class extends standard {@link Context} and adds script pause and stopping
 * http://stackoverflow.com/questions/25837444/rhino-ability-to-pause-save-state-and-resume-javascript
 */
public class RobotScriptContext extends Context {
  private static final String TAG = "RobotScriptContext";

  private boolean abort;

  protected RobotScriptContext(ContextFactory factory) {
    super(factory);
    setInstructionObserverThreshold(1);
    setGenerateObserverCount(true);
    setOptimizationLevel(-1);
  }

  /**
   * Triggers script to stop
   */
  public void abort() {
    abort = true;
  }

  /**
   * here is performed script aborting and eventualy script speed thortling
   * @param instructionCount
   */
  @Override
  protected void observeInstructionCount(int instructionCount) {
    super.observeInstructionCount(instructionCount);

    if (abort) {
      throw new ManualScriptStopException();
    } else {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        abort = true;
      }
    }
  }

  public class ManualScriptStopException extends RuntimeException {

  }
}
