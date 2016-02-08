package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegraph;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.RobotInstructionStateComponent;

/**
 * Message dispatcher with nicer helper methods to send information
 */
public class Messages extends MessageDispatcher {

  /**
   * Dispatch message from entity
   * @param sender
   * @param event
   * @param payload
   */
  public void dispatchMessage(Telegraph sender, TelegramEvents event, Object payload) {
    synchronized (this) {
      dispatchMessage(sender, event.ordinal(), payload);
    }
  }

  /**
   * Dispatch message from entity, Sender is {@link PositionComponent}
   * @param sender
   * @param event
   * @param payload
   */
  public void dispatchMessage(Entity sender, TelegramEvents event, Object payload) {
    synchronized (this) {
      PositionComponent positionComponent = Components.Position.get(sender);
      dispatchMessage(positionComponent, event.ordinal(), payload);
    }
  }

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

  @Override
  public void update() {
    synchronized (this) {
      super.update();
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

  /**
   * Sends {@link TelegramEvents#StartRobot}
   * @param senderEntity
   */
  public void dispatchStartRobot(Entity senderEntity) {
    RobotInstructionStateComponent robotInstructionStateComponent = Components.RobotInstructionState.get(senderEntity);
    dispatchMessage(robotInstructionStateComponent, null, TelegramEvents.StartRobot.ordinal());
  }

  /**
   * Sends {@link TelegramEvents#StopRobot}
   * @param senderEntity
   */
  public void dispatchStopRobot(Entity senderEntity) {
    RobotInstructionStateComponent robotInstructionStateComponent = Components.RobotInstructionState.get(senderEntity);
    dispatchMessage(robotInstructionStateComponent, null, TelegramEvents.StopRobot.ordinal());
  }
}
