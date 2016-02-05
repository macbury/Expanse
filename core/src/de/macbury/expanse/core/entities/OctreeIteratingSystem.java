package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.octree.LevelOctree;
import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.OctreeObject;
import de.macbury.expanse.core.octree.query.OctreeQuery;

/**
 * This system iterates over each {@link Entity} which is in {@link Family} and {@link OctreeIteratingSystem#checkObject(OctreeObject)} with {@link OctreeIteratingSystem#checkEntity(Entity)} return true.
 * This system is ideally for frustrum culling and collision detection
 */
public abstract class OctreeIteratingSystem extends EntitySystem implements Disposable, OctreeQuery<PositionComponent> {
  protected Family family;
  protected LevelOctree<PositionComponent> octree;
  private Array<PositionComponent> treeObjects;

  public OctreeIteratingSystem(LevelOctree<PositionComponent> octree, Family family) {
    this.family       = family;
    this.octree       = octree;
    this.treeObjects  = new Array<PositionComponent>();
  }

  @Override
  public void update(float deltaTime) {
    treeObjects.clear();
    octree.retrieve(treeObjects, this);
    for (int i = 0; i < treeObjects.size; i++) {
      processEntity(treeObjects.get(i).entity, deltaTime);
    }
  }

  protected abstract void processEntity (Entity entity, float deltaTime);

  @Override
  public void dispose() {
    octree = null;
    family = null;
  }

  /**
   * Here we check if object in octree is in family and we check its entity
   * @param object
   * @return
   */
  @Override
  public boolean checkObject(PositionComponent object) {
    return object.entity != null && family.matches(object.entity) && checkEntity(object.entity);
  }

  /**
   * Check if entity shoulf be processed
   * @param entity enity to check
   * @return
   */
  public abstract boolean checkEntity(Entity entity);
}
