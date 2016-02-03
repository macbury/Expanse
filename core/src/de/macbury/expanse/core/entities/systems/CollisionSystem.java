package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This system checks each {@link Entity} with {@link PositionComponent} and {@link BodyComponent}
 */
public class CollisionSystem extends IteratingSystem implements Disposable, EntityListener {
  private WorldOctree octree;
  private Terrain terrain;

  public CollisionSystem(WorldOctree octree, Terrain terrain) {
    super(Family.all(PositionComponent.class, BodyComponent.class).get());
    this.octree  = octree;
    this.terrain = terrain;
  }


  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    if (Components.Body.get(entity).isStatic)
      return;

    snapEntityToTerrain(entity);
  }

  private void snapEntityToTerrain(Entity entity) {
    PositionComponent position = Components.Position.get(entity);
    position.y                 = terrain.getElevation(position.x, position.z);
  }


  @Override
  public void dispose() {
    this.terrain = null;
    this.octree  = null;
  }

  /**
   * Snap {@link Entity} to terrain on start
   */
  @Override
  public void entityAdded(Entity entity) {
    PositionComponent position = Components.Position.get(entity);
    position.y = terrain.getElevation(position.x, position.z);
  }

  @Override
  public void entityRemoved(Entity entity) {

  }
}
