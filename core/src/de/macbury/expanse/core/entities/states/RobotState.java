package de.macbury.expanse.core.entities.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.TimerComponent;

/**
 * This enum have all logic for controling the robot.
 * All instructions from {@link de.macbury.expanse.core.scripts.ScriptRunner} are catched in {@link RobotState#WaitForInstruction} state
 */
public enum RobotState implements State<Entity> {
  /**
   * Wait for {@link Telegram} with is one of {@link TelegramEvents#RobotActionEvents}. Then change to required state
   */
  WaitForInstruction {
    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
      if (TelegramEvents.Test.is(telegram)) {
        Components.RobotState.get(entity).changeState(RobotState.Move);
        return true;
      } else if (TelegramEvents.Wait.is(telegram)) {
        Components.Timer.get(entity).setWaitFor((Float)telegram.extraInfo);
        Components.RobotState.get(entity).changeState(RobotState.Wait);
        return true;
      }
      return false;
    }
  },

  Move {
    @Override
    public void update(Entity entity) {
      Components.RobotState.get(entity).changeState(RobotState.WaitForInstruction);
    }

    @Override
    public void exit(Entity entity) {
      Components.RobotScript.get(entity).resume("Done!");
    }
  },
  Turn,

  /**
   * Waits until {@link TimerComponent#haveFinishingWaiting()} then return to {@link RobotState#WaitForInstruction}
   * and resume {@link de.macbury.expanse.core.entities.components.RobotScriptComponent#resume(Object)} with null
   */
  Wait {
    @Override
    public void update(Entity entity) {
      if (Components.Timer.get(entity).haveFinishingWaiting()) {
        Components.RobotState.get(entity).changeState(RobotState.WaitForInstruction);
      }
    }

    @Override
    public void exit(Entity entity) {
      Components.RobotScript.get(entity).resume(null);
    }
  },
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
