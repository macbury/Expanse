package de.macbury.expanse.core;

import com.badlogic.gdx.ai.msg.Telegram;

/**
 * All events used in game!
 */
public enum TelegramEvents {
  ScriptStart,
  ScriptException,
  ScriptPause,
  ScriptAbort,
  ScriptStop,
  Test,//TODO remove

  /**
   * Here are events triggered by {@link de.macbury.expanse.core.entities.states.RobotMotorState}.
   * They can be used for starting and stoping sound/animation etc
   */
  MotorStart,
  MotorStop,

  /**
   * Make robot wait, payload is float with seconds
   */
  InstructionWait,
  /**
   * Start robot moving
   */
  InstructionMove;

  public static TelegramEvents RobotActionEvents[] = {
    InstructionMove,
    InstructionWait
  };

  /**
   * Return true if {@link Telegram#message} equals this
   * @param telegram
   * @return
   */
  public boolean is(Telegram telegram) {
    return telegram.message == ordinal();
  }
}
