package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
import de.macbury.expanse.core.entities.states.RobotInstructionState;

/**
 * This is main component that handles state machine of robot using {@link RobotInstructionState}.
 */
public class RobotInstructionStateComponent extends BaseFSMComponent<RobotInstructionState> {

  public static class Blueprint extends ComponentBlueprint<RobotInstructionStateComponent> {

    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {

    }

    @Override
    public void assignDependencies(Assets assets) {

    }

    @Override
    public void applyTo(RobotInstructionStateComponent component, Entity target, Messages messages) {
      component.init(target, messages, RobotInstructionState.Living, RobotInstructionState.WaitForInstruction);
    }

    @Override
    public void load(JsonValue source, Json json) {

    }

    @Override
    public void save(Json target, RobotInstructionStateComponent source) {

    }

    @Override
    public void dispose() {

    }
  }
}
