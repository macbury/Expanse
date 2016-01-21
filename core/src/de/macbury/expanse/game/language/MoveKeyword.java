package de.macbury.expanse.game.language;

import com.badlogic.ashley.core.Entity;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.scripts.BaseKeyword;

/**
 * Gives standard methods from {@link BaseKeyword} under robot namespace and additionaly move method
 */
public class MoveKeyword extends BaseKeyword {
  public MoveKeyword(Messages messages, Entity entity) {
    super(messages, entity, "robot", "move");
  }

  public void move(int meters) {
    dispatchInNextFrame(entity, TelegramEvents.InstructionMove, meters);
  }
}
