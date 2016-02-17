package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.MotorComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.RobotCPUComponent;

/**
 * Updates {@link PositionComponent} with information from {@link MotorComponent}
 */

public class MotorSystem extends IteratingSystem implements Disposable, Telegraph {
  private Messages messages;
  private Matrix4 tempMat = new Matrix4();

  public MotorSystem(Messages messages) {
    super(Family.all(MotorComponent.class, PositionComponent.class).get());
    this.messages = messages;

    messages.addListener(this, TelegramEvents.MotorMovementStart);
    messages.addListener(this, TelegramEvents.MotorMovementStop);
    messages.addListener(this, TelegramEvents.MotorTurnStart);
    messages.addListener(this, TelegramEvents.MotorTurnStop);
    messages.addListener(this, TelegramEvents.StopRobot);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MotorComponent motorComponent       = Components.Motor.get(entity);
    PositionComponent positionComponent = Components.Position.get(entity);

    if (!motorComponent.finishedMoving()) {
      motorComponent.moveAlpha += motorComponent.speed * deltaTime;
      if (motorComponent.moveAlpha > 1.0){
        motorComponent.moveAlpha = 1.0f;
      }

      positionComponent.set(motorComponent.startPosition).lerp(
        motorComponent.targetPosition,
        motorComponent.moveAlpha
      );

    } else if (!motorComponent.finishedRotation()) {
      motorComponent.rotationAlpha += motorComponent.speed * deltaTime;
      if (motorComponent.rotationAlpha > 1.0){
        motorComponent.rotationAlpha = 1.0f;
      }

      positionComponent.rotationDeg = MathUtils.lerp(
        motorComponent.startRotation,
        motorComponent.targetRotation,
        motorComponent.rotationAlpha
      );
    }
  }

  /**
   * Calculate target rotateBy. If entity do not match {@link MotorSystem#getFamily()} then return false
   * @param entity
   */
  private boolean calculateRotation(Entity entity) {
    if (getFamily().matches(entity)) {
      MotorComponent motorComponent       = Components.Motor.get(entity);
      PositionComponent positionComponent = Components.Position.get(entity);

      motorComponent.startRotation  = positionComponent.rotationDeg;
      motorComponent.targetRotation = motorComponent.startRotation + motorComponent.rotateBy;
      motorComponent.rotationAlpha  = 0f;

      return true;
    } else {
      return false;
    }
  }

  /**
   * Calculate target position. If entity do not match {@link MotorSystem#getFamily()} then return false
   * @param entity
   */
  private boolean calculateTargetPosition(Entity entity) {
    if (getFamily().matches(entity)) {
      MotorComponent motorComponent       = Components.Motor.get(entity);
      PositionComponent positionComponent = Components.Position.get(entity);

      motorComponent.startPosition.set(positionComponent);
      motorComponent.targetPosition.set(positionComponent).add(0, 0, motorComponent.distance); //TODO change this
      motorComponent.moveAlpha = 0.0f;

      motorComponent.targetPosition.set(0, 0, motorComponent.distance);
      motorComponent.targetPosition.rotate(Vector3.Y, positionComponent.rotationDeg);
      motorComponent.targetPosition.add(motorComponent.startPosition);

      return true;
    } else {
      return false;
    }
  }

  @Override
  public void dispose() {
    messages.removeListener(this, TelegramEvents.MotorMovementStart);
    messages.removeListener(this, TelegramEvents.MotorMovementStop);
    messages.removeListener(this, TelegramEvents.MotorTurnStart);
    messages.removeListener(this, TelegramEvents.MotorTurnStop);
    messages.removeListener(this, TelegramEvents.StopRobot);
    messages = null;
  }


  @Override
  public boolean handleMessage(Telegram msg) {
    if (TelegramEvents.MotorMovementStart.is(msg)) {
      MotorComponent motorComponent = (MotorComponent)msg.sender;
      return calculateTargetPosition(motorComponent.getEntity());
    } else if (TelegramEvents.MotorTurnStart.is(msg)) {
      MotorComponent motorComponent = (MotorComponent)msg.sender;
      return calculateRotation(motorComponent.getEntity());
    } else if (TelegramEvents.StopRobot.is(msg)) {
      RobotCPUComponent robotCPUComponent = (RobotCPUComponent)msg.sender;
      MotorComponent motorComponent = Components.Motor.get(robotCPUComponent.getEntity());
      motorComponent.finishedMoving();
      motorComponent.finishedRotation();
      return true;
    }
    return false;
  }


}
