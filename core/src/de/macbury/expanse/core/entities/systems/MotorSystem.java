package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.MotorComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.states.RobotMotorState;

/**
 * Updates {@link PositionComponent} with information from {@link MotorComponent}
 */

public class MotorSystem extends IteratingSystem implements Disposable, Telegraph {
  private Messages messages;

  public MotorSystem(Messages messages) {
    super(Family.all(MotorComponent.class, PositionComponent.class).get());
    this.messages = messages;
    messages.addListener(this, TelegramEvents.MotorStart);
    messages.addListener(this, TelegramEvents.MotorStop);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MotorComponent motorComponent       = Components.Motor.get(entity);
    PositionComponent positionComponent = Components.Position.get(entity);

    if (!motorComponent.finishedMoving()) {
      motorComponent.alpha += motorComponent.speed * deltaTime;
      if (motorComponent.alpha > 1.0){
        motorComponent.alpha = 1.0f;
      }

      positionComponent.set(motorComponent.startPosition).lerp(
        motorComponent.targetPosition,
        motorComponent.alpha
      );
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
      motorComponent.alpha = 0.0f;

      return true;
    } else {
      return false;
    }
  }

  @Override
  public void dispose() {
    messages.removeListener(this, TelegramEvents.MotorStart);
    messages.removeListener(this, TelegramEvents.MotorStop);
    messages = null;
  }


  @Override
  public boolean handleMessage(Telegram msg) {
    if (TelegramEvents.MotorStart.is(msg)) {
      MotorComponent motorComponent = (MotorComponent)msg.sender;
      return calculateTargetPosition(motorComponent.getEntity());
    }
    return false;
  }
}
