package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.systems.*;
import de.macbury.expanse.core.graphics.LodModelBatch;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  private RenderableSystem renderableSystem;
  private WorldOctreeSystem worldOctreeSystem;
  private MotorSystem motorSystem;
  private TimerSystem timerSystem;
  private RobotManagerSystem robotManagerSystem;
  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(GameCamera renderingCamera, Messages messages, WorldOctree octree) {
    super();

    this.timerSystem           = new TimerSystem(messages);
    this.spriteRenderingSystem = new SpriteRenderingSystem(renderingCamera);
    this.robotManagerSystem    = new RobotManagerSystem(messages);
    this.motorSystem           = new MotorSystem(messages);
    this.worldOctreeSystem     = new WorldOctreeSystem(octree);
    this.renderableSystem      = new RenderableSystem(octree, renderingCamera, new LodModelBatch()); //TODO move initialization of model batch elswhere
    addEntityListener(robotManagerSystem);

    addSystem(robotManagerSystem);
    addSystem(timerSystem);
    addSystem(motorSystem);
    addSystem(worldOctreeSystem);

    addSystem(renderableSystem);
    addSystem(spriteRenderingSystem);
  }

  @Override
  public void dispose() {
    timerSystem.dispose();
    spriteRenderingSystem.dispose();
    motorSystem.dispose();
    renderableSystem.dispose();
    worldOctreeSystem.dispose();
    robotManagerSystem.dispose();

    robotManagerSystem = null;
    spriteRenderingSystem = null;
    renderableSystem = null;
    timerSystem = null;
    motorSystem = null;

    removeEntityListener(robotManagerSystem);
    removeAllEntities();
    clearPools();
  }
}
