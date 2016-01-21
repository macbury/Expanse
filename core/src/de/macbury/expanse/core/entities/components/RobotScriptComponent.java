package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * This component contains {@link ScriptRunner} and redirect all listener outputs using telegram messages
 */
public class RobotScriptComponent implements Component, Pool.Poolable {
  private String source;
  private ScriptRunner scriptRunner;

  @Override
  public void reset() {
    stop();
    source = null;
  }

  public void setScriptRunner(ScriptRunner scriptRunner) {
    if (this.scriptRunner != scriptRunner) {
      stop();
      this.scriptRunner = scriptRunner;
    }
  }

  /**
   * Starts script runner thread
   */
  public void start() {
    if (scriptRunner != null)
      this.scriptRunner.start();
  }

  /**
   * Stops script runner thread and runs {@link ScriptRunner#dispose()}
   */
  public void stop() {
    if (scriptRunner != null)
      this.scriptRunner.dispose();
    scriptRunner = null;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void resume(Object result) {
    if (this.scriptRunner != null)
      this.scriptRunner.resume(result);
  }
}
