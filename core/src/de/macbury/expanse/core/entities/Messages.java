package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegraph;
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
    dispatchMessage(Gdx.graphics.getDeltaTime(), Components.RobotState.get(sender), event.ordinal(), payload);
  }

  /**
   * Alias to {@link MessageDispatcher#addListener(Telegraph, int)}
   * @param listener
   * @param event
   */
  public void addListener(Telegraph listener, TelegramEvents event) {
    addListener(listener, event.ordinal());
  }

  /**
   * Alias to {@link MessageDispatcher#removeListener(Telegraph, int)}
   * @param listener
   * @param event
   */
  public void removeListener(Telegraph listener, TelegramEvents event) {
    removeListener(listener, event.ordinal());
  }

}
