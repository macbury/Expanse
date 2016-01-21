package de.macbury.expanse.core.entities.components;

import com.badlogic.gdx.math.Vector3;
import de.macbury.expanse.core.entities.states.RobotMotorState;

/**
 * This component contains all required information for movement using robot motor
 */
public class MotorComponent extends BaseFSMComponent<RobotMotorState> {
  public Vector3 startPosition  = new Vector3();
  public Vector3 targetPosition = new Vector3();
  /**
   * Progress of movement
   */
  public float alpha = 1.0f;
  /**
   * Speed of motor
   */
  public float speed;
  /**
   * Distance to overcome
   */
  public int distance;

  @Override
  public void reset() {
    startPosition.setZero();
    targetPosition.setZero();
    alpha = 1.0f;
    speed = 0;
  }

  public boolean finishedMoving() {
    return alpha >= 1.0;
  }
}
