package de.macbury.expanse.core;

import com.badlogic.gdx.ai.msg.Telegram;

/**
 * All events used in game!
 */
public enum TelegramEvents {
  ScriptStart,
  ScriptPause,
  ScriptAbort,
  ScriptStop,
  Test,//TODO remove

  Move;

  public static TelegramEvents RobotActionEvents[] = {
    Move,
    Test //TODO remove
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
