package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import de.macbury.expanse.core.TelegramEvents;

/**
 * Message dispatcher with nicer helper methods to send information
 */
public class Messages extends MessageDispatcher {

  /**
   * Dispatch message in two frames
   * @param event
   * @param payload
   */
  public void dispatchInNextFrame(Entity sender, TelegramEvents event, Object payload) {
    dispatchMessage(Gdx.graphics.getDeltaTime() * 2, Components.RobotState.get(sender), event.ordinal(), payload);
  }

}
