package de.macbury.expanse.core.entities.states;

import com.badlogic.ashley.core.Entity;
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
    public boolean onMessage(Entity receiverEntity, Telegram telegram) {
      if (TelegramEvents.ScriptStart.is(telegram)) {
        Components.RobotInstructionState.get(receiverEntity).changeState(RobotInstructionState.WaitForInstruction);
        return true;
      } else if (TelegramEvents.ScriptStop.is(telegram)) {
        Components.RobotInstructionState.get(receiverEntity).changeState(RobotInstructionState.Stopped);
        return true;
      }
      return false;
    }
  },

  /**
   * If robot is stopped by user just dont do anything
   */
  Stopped,

  /**
   * InstructionWait for {@link Telegram} with is one of {@link TelegramEvents#RobotInstructionEvents}. Then change to required state
   */
  WaitForInstruction {
    @Override
    public boolean onMessage(Entity reciverEntity, Telegram telegram) {
      switch (TelegramEvents.values()[telegram.message]) {
        /**
         * Get telegram payload that contains how long should it wait and change state to Wait
         */
        case InstructionWait:
          Components.Timer.get(reciverEntity).setWaitFor((Float)telegram.extraInfo);
          Components.RobotInstructionState.get(reciverEntity).changeState(RobotInstructionState.Wait);
          return true;

        /**
         * Get telegram payload that contains distance and change state to Move
         */
        case InstructionMove:
          Components.Motor.get(reciverEntity).distance = (Integer)telegram.extraInfo;
          Components.RobotInstructionState.get(reciverEntity).changeState(RobotInstructionState.Move);
          return true;

        /**
         * Get telegram payload that contains distance and change state to Turn
         */
        case InstructionTurn:
          Components.Motor.get(reciverEntity).rotateBy = (Integer)telegram.extraInfo;
          Components.RobotInstructionState.get(reciverEntity).changeState(RobotInstructionState.Turn);
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
        Components.RobotInstructionState.get(entity).changeState(RobotInstructionState.WaitForInstruction);
      } else {
        Components.Motor.get(entity).update();
      }
    }

    @Override
    public boolean onMessage(Entity reciverEntity, Telegram telegram) {
      return Components.Motor.get(reciverEntity).handleMessage(telegram);
    }

    @Override
    public void exit(Entity entity) {
      //Components.Motor.get(entity).finishAlpha();
      Components.RobotScript.get(entity).resume(null);
    }
  },

  /**
   * Uses {@link de.macbury.expanse.core.entities.components.MotorComponent} internal state machine to update rotateBy of {@link de.macbury.expanse.core.entities.components.PositionComponent}
   */
  Turn {
    @Override
    public void enter(Entity entity) {
      Components.Motor.get(entity).changeState(RobotMotorState.Turn);
    }

    @Override
    public void update(Entity entity) {
      if (Components.Motor.get(entity).finishedRotation()) {
        Components.RobotInstructionState.get(entity).changeState(RobotInstructionState.WaitForInstruction);
      } else {
        Components.Motor.get(entity).update();
      }
    }

    @Override
    public void exit(Entity entity) {
      //Components.Motor.get(entity).finishAlpha();
      Components.RobotScript.get(entity).resume(null);
    }

    @Override
    public boolean onMessage(Entity reciverEntity, Telegram telegram) {
      return Components.Motor.get(reciverEntity).handleMessage(telegram);
    }
  },

  /**
   * Waits until {@link TimerComponent#haveFinishingWaiting()} then return to {@link RobotInstructionState#WaitForInstruction}
   * and resume {@link de.macbury.expanse.core.entities.components.RobotScriptComponent#resume(Object)} with null
   */
  Wait {
    @Override
    public void update(Entity entity) {
      if (Components.Timer.get(entity).haveFinishingWaiting()) {
        Components.RobotInstructionState.get(entity).changeState(RobotInstructionState.WaitForInstruction);
      }
    }

    @Override
    public void exit(Entity entity) {
      //Components.Timer.get(entity).finishWaiting();
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
