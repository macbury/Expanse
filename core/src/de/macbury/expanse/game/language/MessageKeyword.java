package de.macbury.expanse.game.language;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.scripts.BaseKeyword;


public class MessageKeyword extends BaseKeyword {
  public MessageKeyword(Messages messages, Entity entity) {
    super(messages, entity, "core", "message");
  }

  public void message(String message) {
    Gdx.app.log("console", message);
  }
}
