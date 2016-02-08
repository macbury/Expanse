package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.Expanse;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.entities.systems.*;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  private SelectableSystem selectableSystem;
  private CollisionSystem collisionSystem;
  private RenderableSystem renderableSystem;
  private WorldOctreeSystem worldOctreeSystem;
  private MotorSystem motorSystem;
  private TimerSystem timerSystem;
  private RobotManagerSystem robotManagerSystem;
  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(World world, Expanse game) {
    super();
    this.selectableSystem      = new SelectableSystem(world.octree, game.hud, world.camera, game.messages);
    this.timerSystem           = new TimerSystem(game.messages);
    this.spriteRenderingSystem = new SpriteRenderingSystem(world.camera);
    this.robotManagerSystem    = new RobotManagerSystem(game.messages);
    this.motorSystem           = new MotorSystem(game.messages);
    this.collisionSystem       = new CollisionSystem(world.octree, world.terrain);
    this.worldOctreeSystem     = new WorldOctreeSystem(world.octree);
    this.renderableSystem      = new RenderableSystem(world.octree, world.camera, world.modelBatch, game.fb, world.env);

    addEntityListener(selectableSystem);
    addEntityListener(robotManagerSystem);
    addEntityListener(collisionSystem);
    addEntityListener(worldOctreeSystem);
    addEntityListener(renderableSystem);
    addSystem(robotManagerSystem);
    addSystem(selectableSystem);
    addSystem(timerSystem);
    addSystem(motorSystem);

    addSystem(collisionSystem);
    addSystem(worldOctreeSystem);


    addSystem(renderableSystem);
    addSystem(spriteRenderingSystem);
  }

  @Override
  public void dispose() {
    removeEntityListener(robotManagerSystem);
    removeEntityListener(collisionSystem);
    removeEntityListener(worldOctreeSystem);
    removeEntityListener(renderableSystem);
    removeEntityListener(selectableSystem);
    removeAllEntities();
    clearPools();

    timerSystem.dispose();
    spriteRenderingSystem.dispose();
    motorSystem.dispose();
    renderableSystem.dispose();
    worldOctreeSystem.dispose();
    robotManagerSystem.dispose();
    collisionSystem.dispose();
    selectableSystem.dispose();

    selectableSystem = null;
    collisionSystem = null;
    robotManagerSystem = null;
    spriteRenderingSystem = null;
    renderableSystem = null;
    timerSystem = null;
    motorSystem = null;
  }
}
