package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.systems.*;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  private WorldOctreeSystem worldOctreeSystem;
  private MotorSystem motorSystem;
  private TimerSystem timerSystem;
  private RobotManagerSystem robotManagerSystem;
  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(Camera renderingCamera, Messages messages, WorldOctree octree) {
    super();

    this.timerSystem           = new TimerSystem();
    this.spriteRenderingSystem = new SpriteRenderingSystem(renderingCamera);
    this.robotManagerSystem    = new RobotManagerSystem(messages);
    this.motorSystem           = new MotorSystem(messages);
    this.worldOctreeSystem     = new WorldOctreeSystem(octree);
    addEntityListener(robotManagerSystem);

    addSystem(robotManagerSystem);
    addSystem(timerSystem);
    addSystem(motorSystem);
    addSystem(worldOctreeSystem);

    addSystem(spriteRenderingSystem);
  }

  @Override
  public void dispose() {
    spriteRenderingSystem.dispose();
    spriteRenderingSystem = null;

    motorSystem.dispose();

    worldOctreeSystem.dispose();

    robotManagerSystem.dispose();
    robotManagerSystem = null;

    timerSystem = null;
    motorSystem = null;
    removeEntityListener(robotManagerSystem);
    removeAllEntities();
    clearPools();
  }
}
