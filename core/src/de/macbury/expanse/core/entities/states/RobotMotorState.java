package de.macbury.expanse.core.entities.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.MotorComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.RobotScriptComponent;

/**
 * This enum contains all states that robot motor can be in.
 */
public enum RobotMotorState implements State<Entity> {
  Idle,

  Turn {

  },

  /**
   * on enter calculate target position that motor should go; Triggers on enter {@link TelegramEvents#MotorStart}
   * and on exit {@link TelegramEvents#MotorStop}
   */
  Moving {
    @Override
    public void enter(Entity entity) {
      MotorComponent motorComponent       = Components.Motor.get(entity);
      PositionComponent positionComponent = Components.Position.get(entity);
      motorComponent.calculateTarget(positionComponent);
      motorComponent.dispatchMessage(TelegramEvents.MotorStart);
    }

    @Override
    public void update(Entity entity) {
      if (Components.Motor.get(entity).finishedMoving()) {
        Components.Motor.get(entity).changeState(Idle);
      }
    }

    @Override
    public void exit(Entity entity) {
      Components.Motor.get(entity).dispatchMessage(TelegramEvents.MotorStop);
    }
  }
  ;

  @Override
  public void enter(Entity entity) {

  }

  @Override
  public void update(Entity entity) {

  }

  @Override
  public void exit(Entity entity) {

  }

  @Override
  public boolean onMessage(Entity entity, Telegram telegram) {
    return false;
  }
}
