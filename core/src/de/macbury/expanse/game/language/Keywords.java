package de.macbury.expanse.game.language;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.scripts.BaseKeyword;

/**
 * Just list of all keywords used by game
 */
public class Keywords extends Array<BaseKeyword> {

  public Keywords(Entity entity, Messages messages) {
    super();

    add(new WaitKeyword(messages, entity));
    add(new MessageKeyword(messages, entity));
  }
}
