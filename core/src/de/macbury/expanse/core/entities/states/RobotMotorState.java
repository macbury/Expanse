package de.macbury.expanse.core.entities.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;

/**
 * This enum contains all states that robot motor can be in.
 */
public enum RobotMotorState implements State<Entity> {
  Idle,

  /**
   * Triggers on enter {@link TelegramEvents#MotorTurnStart}
   * and on exit {@link TelegramEvents#MotorTurnStop}. If finished rotateBy change state to idle
   */
  Turn {
    @Override
    public void enter(Entity entity) {
      Components.Motor.get(entity).dispatchMessage(TelegramEvents.MotorTurnStart);
    }

    @Override
    public void update(Entity entity) {
      if (Components.Motor.get(entity).finishedRotation()) {
        Components.Motor.get(entity).changeState(Idle);
      }
    }

    @Override
    public void exit(Entity entity) {
      Components.Motor.get(entity).dispatchMessage(TelegramEvents.MotorTurnStop);
    }
  },

  /**
   * Triggers on enter {@link TelegramEvents#MotorMovementStart}
   * and on exit {@link TelegramEvents#MotorMovementStop}. If finished moving change state to idle
   */
  Moving {
    @Override
    public void enter(Entity entity) {
      Components.Motor.get(entity).dispatchMessage(TelegramEvents.MotorMovementStart);
    }

    @Override
    public void update(Entity entity) {
      if (Components.Motor.get(entity).finishedMoving()) {
        Components.Motor.get(entity).changeState(Idle);
      }
    }

    @Override
    public void exit(Entity entity) {
      Components.Motor.get(entity).dispatchMessage(TelegramEvents.MotorMovementStop);
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
