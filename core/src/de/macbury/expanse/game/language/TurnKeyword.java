package de.macbury.expanse.game.language;

import com.badlogic.ashley.core.Entity;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.scripts.BaseKeyword;

/**
 * Created on 20.01.16.
 */
public class TurnKeyword extends BaseKeyword {
  public TurnKeyword(Messages messages, Entity entity) {
    super(messages, entity, "robot", "turn");
  }
}
