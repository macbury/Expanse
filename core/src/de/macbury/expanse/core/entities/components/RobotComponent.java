package de.macbury.expanse.core.entities.components;

import de.macbury.expanse.core.entities.states.RobotState;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * This is main component that handles state machine of robot using {@link RobotState}
 */
public class RobotComponent extends BaseFSMComponent<RobotState> {

  @Override
  public void reset() {
    super.reset();
  }
}
