package de.macbury.expanse.core.scripts.modules;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;

/**
 * This class contains all modules
 */
public class RobotActionController extends BaseModule {
  private MessageDispatcher messages;
  private Entity entity;

  public RobotActionController(Entity entity, MessageDispatcher messages) {
    this.entity = entity;
    this.messages = messages;
  }

  /**
   * Dispatch message in two frames
   * @param event
   * @param payload
   */
  private void dispatchMessage(TelegramEvents event, Object payload) {
    messages.dispatchMessage(Gdx.graphics.getDeltaTime() * 2, Components.RobotState.get(entity), event.ordinal(), payload);
  }

  @ExposeAsGlobalFunction
  public void testRobotFunction() {
    Components.Position.get(entity).add(0.02f * Gdx.graphics.getDeltaTime(),0,0);
  }

  @ExposeAsGlobalFunction
  public void telegramTest() {
    dispatchMessage(TelegramEvents.Test, null);
  }

  @ExposeAsGlobalFunction
  public void startMoving(int distanceInMeters) {
    dispatchMessage(TelegramEvents.Move, distanceInMeters);
  }

  @Override
  public String getClassName() {
    return getClass().getCanonicalName();
  }

  @Override
  public void dispose() {
    this.entity = null;
    this.messages = null;
  }
}
