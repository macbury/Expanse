package de.macbury.expanse.game.language;

import com.badlogic.ashley.core.Entity;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.scripts.BaseKeyword;

/**
 * Gives standard methods from {@link BaseKeyword} under core namespace and additionaly wait method
 */
public class WaitKeyword extends BaseKeyword {
  public WaitKeyword(Messages messages, Entity entity) {
    super(messages, entity, "core", "wait");
  }

  public void wait(float waitInSeconds) {
    dispatchInNextFrame(entity, TelegramEvents.InstructionWait, waitInSeconds);
  }
}
