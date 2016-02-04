package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.Expanse;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.systems.*;
import de.macbury.expanse.core.graphics.LodModelBatch;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  private CollisionSystem collisionSystem;
  private RenderableSystem renderableSystem;
  private WorldOctreeSystem worldOctreeSystem;
  private MotorSystem motorSystem;
  private TimerSystem timerSystem;
  private RobotManagerSystem robotManagerSystem;
  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(Expanse expanse, GameCamera renderingCamera, WorldOctree octree, Terrain terrain) {
    super();
    this.timerSystem           = new TimerSystem(expanse.messages);
    this.spriteRenderingSystem = new SpriteRenderingSystem(renderingCamera);
    this.robotManagerSystem    = new RobotManagerSystem(expanse.messages);
    this.motorSystem           = new MotorSystem(expanse.messages);
    this.collisionSystem       = new CollisionSystem(octree, terrain);
    this.worldOctreeSystem     = new WorldOctreeSystem(octree);
    this.renderableSystem      = new RenderableSystem(octree, renderingCamera, new LodModelBatch(), expanse.fb); //TODO move initialization of model batch elswhere

    addEntityListener(robotManagerSystem);
    addEntityListener(collisionSystem);
    addSystem(robotManagerSystem);
    addSystem(timerSystem);
    addSystem(motorSystem);
    addSystem(worldOctreeSystem);
    addSystem(collisionSystem);

    addSystem(renderableSystem);
    addSystem(spriteRenderingSystem);
  }

  @Override
  public void dispose() {
    removeEntityListener(robotManagerSystem);
    removeEntityListener(collisionSystem);
    removeAllEntities();
    clearPools();

    timerSystem.dispose();
    spriteRenderingSystem.dispose();
    motorSystem.dispose();
    renderableSystem.dispose();
    worldOctreeSystem.dispose();
    robotManagerSystem.dispose();
    collisionSystem.dispose();

    collisionSystem = null;
    robotManagerSystem = null;
    spriteRenderingSystem = null;
    renderableSystem = null;
    timerSystem = null;
    motorSystem = null;
  }
}
