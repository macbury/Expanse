package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.TimerComponent;

/**
 * Just increase time in {@link de.macbury.expanse.core.entities.components.TimerComponent}
 */
public class TimerSystem extends IteratingSystem {

  public TimerSystem() {
    super(Family.all(TimerComponent.class).get());
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
}
