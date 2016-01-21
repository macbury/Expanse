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
  public float rotationAlpha = 1.0f;

  /**
   * Progress of movement
   */
  public float moveAlpha = 1.0f;
  /**
   * Speed of motor
   */
  public float speed;
  /**
   * Distance to overcome
   */
  public int distance;

  /**
   * How much it needs to rotate
   */
  public int rotateBy;

  public float startRotation;
  public float targetRotation;

  @Override
  public void reset() {
    startPosition.setZero();
    targetPosition.setZero();
    startRotation = 0.0f;
    targetRotation = 0.0f;
    moveAlpha = 1.0f;
    rotationAlpha = 1.0f;
    speed = 0;
    rotateBy = 0;
  }

  public boolean finishedMoving() {
    return moveAlpha >= 1.0;
  }

  public boolean finishedRotation() {
    return rotationAlpha >= 1.0;
  }
}
