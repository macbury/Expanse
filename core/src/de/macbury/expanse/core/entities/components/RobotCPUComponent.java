package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
import de.macbury.expanse.core.entities.states.RobotInstructionState;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * This is main component that handles state machine of robot using {@link RobotInstructionState}.
 */
public class RobotCPUComponent extends BaseFSMComponent<RobotInstructionState> {
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

  public static class Blueprint extends ComponentBlueprint<RobotCPUComponent> {
    private String scriptSource;

    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {

    }

    @Override
    public void assignDependencies(Assets assets) {

    }

    @Override
    public void applyTo(RobotCPUComponent component, Entity target, Messages messages) {
      component.init(target, messages, RobotInstructionState.Living, RobotInstructionState.WaitForInstruction);
      component.setSource(scriptSource);
    }

    @Override
    public void load(JsonValue source, Json json) {
      scriptSource = Gdx.files.internal(source.getString("source")).readString();
    }

    @Override
    public void save(Json target, RobotCPUComponent source) {

    }

    @Override
    public void dispose() {

    }
  }
}
