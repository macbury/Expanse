package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
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

  public void finishAlpha() {
    moveAlpha     = 1.0f;
    rotationAlpha = 1.0f;
  }

  public static class Blueprint extends ComponentBlueprint<MotorComponent> {
    private float rotationAlpha;
    private float moveAlpha;
    private float speed;
    private Vector3 startPosition;
    private Vector3 targetPosition;

    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {

    }

    @Override
    public void assignDependencies(Assets assets) {

    }

    @Override
    public void applyTo(MotorComponent component, Entity target, Messages messages) {
      component.speed = speed;
      component.init(target, messages, null, RobotMotorState.Idle);
    }

    @Override
    public void load(JsonValue source, Json json) {
      //TODO serializing whole motor
      //this.rotationAlpha = source.getFloat("rotationAlpha", 0.0f);
      //this.moveAlpha     = source.getFloat("moveAlpha", 0.0f);
      this.speed         = source.getFloat("speed", 0.0f);

      //this.startPosition  = json.readValue("startPosition", Vector3.class, new Vector3(), source);
      //this.targetPosition = json.readValue("targetPosition", Vector3.class, new Vector3(), source);
    }

    @Override
    public void save(Json target, MotorComponent source) {

    }

    @Override
    public void dispose() {

    }
  }
}
