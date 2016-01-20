package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.systems.RobotManagerSystem;
import de.macbury.expanse.core.entities.systems.SpriteRenderingSystem;
import de.macbury.expanse.core.entities.systems.TimerSystem;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  private TimerSystem timerSystem;
  private RobotManagerSystem robotManagerSystem;
  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(Camera renderingCamera, Messages messages) {
    super();

    this.timerSystem           = new TimerSystem();
    this.spriteRenderingSystem = new SpriteRenderingSystem(renderingCamera);
    this.robotManagerSystem = new RobotManagerSystem(messages);

    addEntityListener(robotManagerSystem);
    addSystem(spriteRenderingSystem);
    addSystem(robotManagerSystem);
    addSystem(timerSystem);
  }

  @Override
  public void dispose() {
    spriteRenderingSystem.dispose();
    spriteRenderingSystem = null;


    robotManagerSystem.dispose();
    robotManagerSystem = null;

    timerSystem = null;

    removeEntityListener(robotManagerSystem);
    removeAllEntities();
    clearPools();
  }
}
