package de.macbury.expanse.game.language;

import com.badlogic.ashley.core.Entity;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.scripts.BaseKeyword;

/**
 * Gives standard methods from {@link BaseKeyword} under robot namespace and additionaly turn method
 */
public class TurnKeyword extends BaseKeyword {
  public TurnKeyword(Messages messages, Entity entity) {
    super(messages, entity, "robot", "turn");
  }

  public void turn(int degrees) {
    dispatchInNextFrame(entity, TelegramEvents.InstructionTurn, degrees);
  }
}
