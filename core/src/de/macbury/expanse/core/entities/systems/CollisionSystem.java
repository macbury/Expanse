package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.graphics.terrain.ElevationHelper;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.octree.LevelOctree;

/**
 * This system checks each {@link Entity} with {@link PositionComponent} and {@link BodyComponent}
 */
public class CollisionSystem extends IteratingSystem implements Disposable, EntityListener {
  private static final String TAG = "CollisionSystem";
  private LevelOctree<PositionComponent> octree;
  private Terrain terrain;

  public CollisionSystem(LevelOctree<PositionComponent> octree, Terrain terrain) {
    super(Family.all(PositionComponent.class).get());
    this.octree  = octree;
    this.terrain = terrain;
  }


  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    if (Components.Static.has(entity))
      return;

    snapEntityToTerrain(entity);
  }

  private void snapEntityToTerrain(Entity entity) {
    PositionComponent position = Components.Position.get(entity);
    ElevationHelper elevation  = terrain.getElevation(position.x, position.z);
    position.y                 = elevation.get();
    //Gdx.app.log(TAG, "Rotate: " + elevation.getRotation());

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
    snapEntityToTerrain(entity);
  }

  @Override
  public void entityRemoved(Entity entity) {

  }
}
