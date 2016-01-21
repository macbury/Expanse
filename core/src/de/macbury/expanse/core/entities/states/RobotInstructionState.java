package de.macbury.expanse.core.entities.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.TimerComponent;

/**
 * This enum have all logic for controling the robot.
 * All instructions from {@link de.macbury.expanse.core.scripts.ScriptRunner} are catched in {@link RobotInstructionState#WaitForInstruction} state
 */
public enum RobotInstructionState implements State<Entity> {
  /**
   * This is only global state that handles all messages that need to change state of current robot and are not captured by onMessage of other states
   * Here will be logic for unexpected collision handling, on attack event, batter low power event etc.
   */
  Living {
    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
      Gdx.app.log("RobotInstructionState", "Ignore telegram...");
      return false;
    }
  },

  /**
   * InstructionWait for {@link Telegram} with is one of {@link TelegramEvents#RobotActionEvents}. Then change to required state
   */
  WaitForInstruction {
    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
      switch (TelegramEvents.values()[telegram.message]) {
        /**
         * Get telegram payload that contains how long should it wait and change state to InstructionWait
         */
        case InstructionWait:
          Components.Timer.get(entity).setWaitFor((Float)telegram.extraInfo);
          Components.RobotState.get(entity).changeState(RobotInstructionState.Wait);
          return true;

        /**
         * Get telegram payload that contains distance and change state to InstructionMove
         */
        case InstructionMove:
          Components.Motor.get(entity).distance = (Integer)telegram.extraInfo;
          Components.RobotState.get(entity).changeState(RobotInstructionState.Move);
          return true;

        default:
          return false;
      }
    }
  },

  /**
   * Handle simple moving in direction that robot is facing. Update {@link de.macbury.expanse.core.entities.components.MotorComponent} state machine untils is not Idle
   * then return to {@link RobotInstructionState#WaitForInstruction}
   */
  Move {
    @Override
    public void enter(Entity entity) {
      Components.Motor.get(entity).changeState(RobotMotorState.Moving);
    }

    @Override
    public void update(Entity entity) {
      if (Components.Motor.get(entity).finishedMoving()) {
        Components.RobotState.get(entity).changeState(RobotInstructionState.WaitForInstruction);
      } else {
        Components.Motor.get(entity).update();
      }
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
      return Components.Motor.get(entity).handleMessage(telegram);
    }

    @Override
    public void exit(Entity entity) {
      Components.RobotScript.get(entity).resume(null);
    }
  },

  Turn,

  /**
   * Waits until {@link TimerComponent#haveFinishingWaiting()} then return to {@link RobotInstructionState#WaitForInstruction}
   * and resume {@link de.macbury.expanse.core.entities.components.RobotScriptComponent#resume(Object)} with null
   */
  Wait {
    @Override
    public void update(Entity entity) {
      if (Components.Timer.get(entity).haveFinishingWaiting()) {
        Components.RobotState.get(entity).changeState(RobotInstructionState.WaitForInstruction);
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
