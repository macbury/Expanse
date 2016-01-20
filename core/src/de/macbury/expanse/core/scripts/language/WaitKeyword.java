package de.macbury.expanse.core.scripts.language;

import com.badlogic.ashley.core.Entity;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Messages;

/**
 * Created on 20.01.16.
 */
public class WaitKeyword extends BaseKeyword {
  public WaitKeyword(Messages messages, Entity entity) {
    super(messages, entity, "core", "wait");
  }

  public void wait(float waitInSeconds) {
    messages.dispatchInNextFrame(entity, TelegramEvents.Wait, waitInSeconds);
  }
}
