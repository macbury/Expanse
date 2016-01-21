package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.MotorComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.states.RobotMotorState;

/**
 * Updates {@link PositionComponent} with information from {@link MotorComponent}
 */

public class MotorSystem extends IteratingSystem implements Disposable {
  public MotorSystem() {
    super(Family.all(MotorComponent.class, PositionComponent.class).get());
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    MotorComponent motorComponent       = Components.Motor.get(entity);
    PositionComponent positionComponent = Components.Position.get(entity);

    if (motorComponent.is(RobotMotorState.Moving)) {
      motorComponent.alpha += motorComponent.speed * deltaTime;
      if (motorComponent.alpha > 1.0){
        motorComponent.alpha = 1.0f;
      }

      positionComponent.set(motorComponent.startPosition).lerp(
        motorComponent.targetPosition,
        motorComponent.alpha
      );
    }
  }

  @Override
  public void dispose() {

  }
}
