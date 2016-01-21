package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.RobotInstructionStateComponent;
import de.macbury.expanse.core.entities.components.TimerComponent;

/**
 * Just increase time in {@link de.macbury.expanse.core.entities.components.TimerComponent}
 */
public class TimerSystem extends IteratingSystem implements Disposable, Telegraph {
  private Messages messages;

  public TimerSystem(Messages messages) {
    super(Family.all(TimerComponent.class).get());
    this.messages = messages;
    messages.addListener(this, TelegramEvents.StopRobot);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    TimerComponent timerComponent = Components.Timer.get(entity);
    timerComponent.runTime += deltaTime;
    timerComponent.waitFor -= deltaTime;
    if (timerComponent.waitFor < 0) {
      timerComponent.waitFor = 0;
    }
  }

  @Override
  public void dispose() {
    messages.removeListener(this, TelegramEvents.StopRobot);
    messages = null;
  }

  /**
   * If receive {@link TelegramEvents#StopRobot} stop timer
   * @param msg
   * @return
   */
  @Override
  public boolean handleMessage(Telegram msg) {
    if (TelegramEvents.StopRobot.is(msg)) {
      RobotInstructionStateComponent robotInstructionStateComponent = (RobotInstructionStateComponent)msg.sender;
      Components.Timer.get(robotInstructionStateComponent.getEntity()).finishWaiting();
      return true;
    }
    return false;
  }
}
