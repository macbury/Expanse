package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.RobotCPUComponent;
import de.macbury.expanse.core.scripts.ScriptRunner;
import de.macbury.expanse.core.scripts.ScriptRunnerListener;
import de.macbury.expanse.game.Keywords;
import org.mozilla.javascript.ContinuationPending;

/**
 * This system updates state machine, redirect telegrams for {@link TelegramEvents#RobotInstructionEvents} to each {@link Entity} and handles robot script controlling.
 * To control robot you need two components:
 * {@link RobotCPUComponent}
 */
public class RobotManagerSystem extends IteratingSystem implements Disposable, EntityListener, ScriptRunnerListener, Telegraph {
  private static final String TAG = "RobotManagerSystem";
  private Messages messages;

  public RobotManagerSystem(Messages messages) {
    super(Family.all(RobotCPUComponent.class).get());
    this.messages = messages;

    messages.addListener(this, TelegramEvents.StartRobot);
    messages.addListener(this, TelegramEvents.StopRobot);
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Components.RobotCPU.get(entity).update();
  }

  /**
   * Stops script in entity
   */
  private void stopScript(Entity entity) {
    Components.RobotCPU.get(entity).stop();
  }

  /**
   * Reprograms entity to run updated script
   * @param entity
   */
  private void reprogram(Entity entity) {
    RobotCPUComponent robotCPUComponent       = Components.RobotCPU.get(entity);
    ScriptRunner robotScriptRunner            = new ScriptRunner(robotCPUComponent.getSource(), new Keywords(entity, messages), false);
    robotScriptRunner.addListener(this);
    robotScriptRunner.setOwner(entity);
    robotCPUComponent.setScriptRunner(robotScriptRunner);
    robotScriptRunner.start();
  }

  /**
   * Register all robot {@link TelegramEvents#RobotInstructionEvents} for {@link de.macbury.expanse.Expanse#messages}
   * @param entity
   */
  private void registerListenersForEntity(Entity entity) {
    RobotCPUComponent robotCPUComponent       = Components.RobotCPU.get(entity);

    for (TelegramEvents event : TelegramEvents.RobotInstructionEvents) {
      messages.addListener(robotCPUComponent, event.ordinal());
    }
  }

  /**
   * Remove all robot {@link TelegramEvents#RobotInstructionEvents} from {@link de.macbury.expanse.Expanse#messages}
   * @param entity
   */
  private void unregisterListenersForEntity(Entity entity) {
    RobotCPUComponent robotCPUComponent       = Components.RobotCPU.get(entity);

    for (TelegramEvents event : TelegramEvents.RobotInstructionEvents) {
      messages.removeListener(robotCPUComponent, event.ordinal());
    }
  }

  /**
   * Build all required telegram listeners and send message to reprogram robot!
   * @param entity
   */
  @Override
  public void entityAdded(Entity entity) {
    if (getFamily().matches(entity)) {
      registerListenersForEntity(entity);
      messages.dispatchStartRobot(entity);
    }
  }

  @Override
  public void entityRemoved(Entity entity) {
    if (getFamily().matches(entity)) {
      unregisterListenersForEntity(entity);
      messages.dispatchStopRobot(entity);
    }
  }

  @Override
  public void dispose() {
    messages.removeListener(this, TelegramEvents.StartRobot);
    messages.removeListener(this, TelegramEvents.StopRobot);
    messages = null;
  }

  @Override
  public void onScriptStart(ScriptRunner scriptRunner) {
    Entity entity = (Entity)scriptRunner.getOwner();
    messages.dispatchInNextFrame(entity, TelegramEvents.ScriptStart, null);
  }

  @Override
  public void onScriptException(ScriptRunner scriptRunner, Exception exception) {
    Entity entity = (Entity)scriptRunner.getOwner();
    messages.dispatchInNextFrame(entity, TelegramEvents.ScriptException, exception);
    Gdx.app.error(TAG, "onScriptException", exception);
  }

  @Override
  public void onScriptAbort(ScriptRunner scriptRunner) {
    Entity entity = (Entity)scriptRunner.getOwner();
    messages.dispatchInNextFrame(entity, TelegramEvents.ScriptAbort, null);
  }

  @Override
  public void onScriptPause(ScriptRunner scriptRunner, ContinuationPending continuationPending) {
    Entity entity = (Entity)scriptRunner.getOwner();
    messages.dispatchInNextFrame(entity, TelegramEvents.ScriptPause, null);
  }

  @Override
  public void onScriptFinish(ScriptRunner scriptRunner) {
    Entity entity = (Entity)scriptRunner.getOwner();
    messages.dispatchInNextFrame(entity, TelegramEvents.ScriptStop, null);
  }

  @Override
  public boolean handleMessage(Telegram msg) {
    RobotCPUComponent robotCPUComponent = (RobotCPUComponent)msg.sender;
    Entity targetEntity                                           = robotCPUComponent.getEntity();

    if (TelegramEvents.StartRobot.is(msg)) {
      reprogram(targetEntity);
      return true;
    } else if (TelegramEvents.StopRobot.is(msg)) {
      stopScript(targetEntity);
      return true;
    }

    return false;
  }
}
