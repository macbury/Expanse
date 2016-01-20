package de.macbury.expanse.core.scripts.modules;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;

/**
 * This class contains all modules
 */
public class RobotActionController extends BaseModule {
  private Messages messages;
  private Entity entity;

  public RobotActionController(Entity entity, Messages messages) {
    this.entity = entity;
    this.messages = messages;
  }

  @ExposeAsGlobalFunction
  public void testRobotFunction() {
    Components.Position.get(entity).add(0.02f * Gdx.graphics.getDeltaTime(),0,0);
  }

  @ExposeAsGlobalFunction
  public void telegramTest() {
    messages.dispatchInNextFrame(entity, TelegramEvents.Test, null);
  }

  @ExposeAsGlobalFunction
  public void startMoving(int distanceInMeters) {
    messages.dispatchInNextFrame(entity, TelegramEvents.Move, null);
  }

  @ExposeAsGlobalFunction
  public void startWaiting(float waitInSeconds) {
    messages.dispatchInNextFrame(entity, TelegramEvents.Wait, waitInSeconds);
  }

  @Override
  public String getClassName() {
    return "robot";
  }

  @Override
  public void dispose() {
    this.entity = null;
    this.messages = null;
  }
}
