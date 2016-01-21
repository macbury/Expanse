package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegraph;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.components.RobotInstructionStateComponent;

/**
 * Message dispatcher with nicer helper methods to send information
 */
public class Messages extends MessageDispatcher {

  /**
   * Dispatch message in two frames to sender
   * @param event
   * @param payload
   */
  public void dispatchInNextFrame(Entity sender, TelegramEvents event, Object payload) {
    synchronized (this) {
      RobotInstructionStateComponent robotInstructionStateComponent = Components.RobotInstructionState.get(sender);
      dispatchMessage(
        Gdx.graphics.getDeltaTime(),
        robotInstructionStateComponent,
        robotInstructionStateComponent,
        event.ordinal(),
        payload
      );
    }
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
