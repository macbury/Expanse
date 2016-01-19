package de.macbury.expanse.core.scripts;

import com.badlogic.gdx.utils.GdxRuntimeException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

/**
 * This class extends standard {@link Context} and adds script pause and stopping
 * http://stackoverflow.com/questions/25837444/rhino-ability-to-pause-save-state-and-resume-javascript
 */
public class RobotScriptContext extends Context {
  private enum State {
    Running, Paused, Stopping, Stopped
  }
  private State state;

  protected RobotScriptContext(ContextFactory factory) {
    super(factory);
    setInstructionObserverThreshold(1);
    setOptimizationLevel(-1);
    state = State.Stopped;
  }

  /**
   * Triggers script to stop
   */
  public void stop() {
    state = State.Stopping;
  }

  /**
   * Triggers script to pause
   */
  public void pause() {
    state = State.Paused;
  }


  /**
   * Triggers script to resume
   */
  public void resume() {
    state = State.Running;
  }

  /**
   * Run script that can be paused and resumed
   * @param script
   * @param scope
   */
  public void execPausableScript(Script script, ScriptableObject scope) {
    if (state == State.Stopped) {
      state = State.Running;
      script.exec(this, scope);
      state = State.Stopped;
    } else {
      throw new GdxRuntimeException("Context already is running script!");
    }
  }

  @Override
  protected void observeInstructionCount(int instructionCount) {
    super.observeInstructionCount(instructionCount);

    if (state == State.Stopping) {
      throw new ManualScriptStopException();
    } else {
      if (state == State.Running) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          state = State.Stopping;
          e.printStackTrace();
        }
      } else {
        while(state == State.Paused) {
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            state = State.Stopping;
            e.printStackTrace();
          }
        }
      }
    }
  }

  public class ManualScriptStopException extends RuntimeException {

  }
}
