package de.macbury.expanse.core.entities.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;

/**
 * This enum have all logic for controling the robot
 */
public enum RobotState implements State<Entity> {
  WaitForInstruction {
    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
      if (TelegramEvents.Test.is(telegram)) {
        Components.RobotScript.get(entity).resume("Done!");
        return true;
      }
      return super.onMessage(entity, telegram);
    }
  },
  Move,
  Turn,
  Wait
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
