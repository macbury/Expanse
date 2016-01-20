package de.macbury.expanse.core.scripts.language;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import de.macbury.expanse.core.entities.Messages;


public class LogKeyword extends BaseKeyword {
  public LogKeyword(Messages messages, Entity entity) {
    super(messages, entity, "core", "log");
  }

  public void log(String message) {
    Gdx.app.log("console", message);
  }
}
