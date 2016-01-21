package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.RobotScriptComponent;
import de.macbury.expanse.core.entities.components.RobotInstructionStateComponent;
import de.macbury.expanse.core.scripts.ScriptRunner;
import de.macbury.expanse.core.scripts.ScriptRunnerListener;
import de.macbury.expanse.game.Keywords;
import org.mozilla.javascript.ContinuationPending;

/**
 * This system updates state machine, redirect telegrams for {@link TelegramEvents#RobotInstructionEvents} to each {@link Entity} and handles robot script controlling.
 * To control robot you need two components:
 * {@link RobotInstructionStateComponent}
 * {@link RobotScriptComponent}
 */
public class RobotManagerSystem extends IteratingSystem implements Disposable, EntityListener, ScriptRunnerListener {
  private static final String TAG = "RobotManagerSystem";
  private Messages messages;

  public RobotManagerSystem(Messages messages) {
    super(Family.all(RobotInstructionStateComponent.class, RobotScriptComponent.class).get());
    this.messages = messages;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Components.RobotInstructionState.get(entity).update();
  }

  /**
   * Reprograms entity to run updated script
   * @param entity
   */
  private void reprogram(Entity entity) {
    RobotScriptComponent robotScriptComponent = Components.RobotScript.get(entity);
    ScriptRunner robotScriptRunner            = new ScriptRunner(robotScriptComponent.getSource(), new Keywords(entity, messages), true);
    robotScriptRunner.addListener(this);
    robotScriptRunner.setOwner(entity);
    robotScriptComponent.setScriptRunner(robotScriptRunner);
    robotScriptRunner.start();
  }

  /**
   * Register all robot {@link TelegramEvents#RobotInstructionEvents} for {@link de.macbury.expanse.Expanse#messages}
   * @param entity
   */
  private void registerListenersForEntity(Entity entity) {
    RobotInstructionStateComponent robotState = Components.RobotInstructionState.get(entity);

    for (TelegramEvents event : TelegramEvents.RobotInstructionEvents) {
      messages.addListener(robotState, event.ordinal());
    }
  }

  /**
   * Remove all robot {@link TelegramEvents#RobotInstructionEvents} from {@link de.macbury.expanse.Expanse#messages}
   * @param entity
   */
  private void unregisterListenersForEntity(Entity entity) {
    RobotInstructionStateComponent robotState = Components.RobotInstructionState.get(entity);

    for (TelegramEvents event : TelegramEvents.RobotInstructionEvents) {
      messages.removeListener(robotState, event.ordinal());
    }
  }

  /**
   * Add all required telegram listeners and reprogram robot
   * @param entity
   */
  @Override
  public void entityAdded(Entity entity) {
    if (getFamily().matches(entity)) {
      registerListenersForEntity(entity);
      reprogram(entity);
    }
  }

  @Override
  public void entityRemoved(Entity entity) {
    if (getFamily().matches(entity)) {
      unregisterListenersForEntity(entity);
      Components.RobotScript.get(entity).stop();
    }
  }

  @Override
  public void dispose() {
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
}
