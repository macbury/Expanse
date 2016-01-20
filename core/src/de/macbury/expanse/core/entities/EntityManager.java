package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.systems.RobotManagerSystem;
import de.macbury.expanse.core.entities.systems.SpriteRenderingSystem;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  private RobotManagerSystem robotManagerSystem;
  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(Camera renderingCamera, MessageDispatcher messages) {
    super();


    this.spriteRenderingSystem = new SpriteRenderingSystem(renderingCamera);
    this.robotManagerSystem = new RobotManagerSystem(messages);

    addEntityListener(robotManagerSystem);
    addSystem(spriteRenderingSystem);
    addSystem(robotManagerSystem);
  }

  @Override
  public void dispose() {
    spriteRenderingSystem.dispose();
    spriteRenderingSystem = null;


    robotManagerSystem.dispose();
    robotManagerSystem = null;

    removeEntityListener(robotManagerSystem);
    removeAllEntities();
    clearPools();
  }
}
