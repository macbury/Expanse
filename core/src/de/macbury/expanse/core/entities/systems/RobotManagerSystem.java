package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.RobotScriptComponent;
import de.macbury.expanse.core.entities.components.RobotStateComponent;
import de.macbury.expanse.core.entities.states.RobotState;
import de.macbury.expanse.core.scripts.ScriptRunner;
import de.macbury.expanse.core.scripts.modules.Console;
import de.macbury.expanse.core.scripts.modules.RobotActionController;
import org.mozilla.javascript.ScriptableObject;

/**
 * This system updates state machine, redirect telegrams for {@link TelegramEvents#RobotActionEvents} to each {@link Entity} and handles robot script controlling.
 * To control robot you need two components:
 * {@link RobotStateComponent}
 * {@link RobotScriptComponent}
 */
public class RobotManagerSystem extends IteratingSystem implements Disposable, EntityListener {
  private MessageDispatcher messages;

  public RobotManagerSystem(MessageDispatcher messages) {
    super(Family.all(RobotStateComponent.class, RobotScriptComponent.class).get());
    this.messages = messages;
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    Components.RobotState.get(entity).update();
  }

  /**
   * Reprograms entity to run updated script
   * @param entity
   */
  private void reprogram(Entity entity) {
    Array<ScriptableObject> globalObjectFunctions = new Array<ScriptableObject>();
    globalObjectFunctions.add(new Console());
    globalObjectFunctions.add(new RobotActionController(entity, messages));

    RobotScriptComponent robotScriptComponent = Components.RobotScript.get(entity);
    ScriptRunner robotScriptRunner            = new ScriptRunner(robotScriptComponent.getSource(), globalObjectFunctions, true);
    //robotScriptRunner.addListener(this);
    robotScriptRunner.setOwner(entity);
    robotScriptComponent.setScriptRunner(robotScriptRunner);
    robotScriptRunner.start();
  }

  /**
   * Register all robot {@link TelegramEvents#RobotActionEvents} for {@link de.macbury.expanse.Expanse#messages}
   * @param entity
   */
  private void registerListenersForEntity(Entity entity) {
    RobotStateComponent robotState = Components.RobotState.get(entity);

    for (TelegramEvents event : TelegramEvents.RobotActionEvents) {
      messages.addListener(robotState, event.ordinal());
    }
  }

  /**
   * Remove all robot {@link TelegramEvents#RobotActionEvents} from {@link de.macbury.expanse.Expanse#messages}
   * @param entity
   */
  private void unregisterListenersForEntity(Entity entity) {
    RobotStateComponent robotState = Components.RobotState.get(entity);

    for (TelegramEvents event : TelegramEvents.RobotActionEvents) {
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
}
