package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.octree.LevelOctree;
import de.macbury.expanse.core.octree.OctreeNode;

/**
 * This class refreshes bounding boxes for each {@link BodyComponent} and additionaly
 * refresh octree
 */
public class WorldOctreeSystem extends IteratingSystem implements Disposable, EntityListener {
  private LevelOctree<PositionComponent> octree;
  private Vector3 halfDimenTemp = new Vector3();
  private Vector3 minVecTemp    = new Vector3();
  private Vector3 maxVecTemp    = new Vector3();
  /**
   * If true this will clear static octree and add all {@link Entity} with {@link de.macbury.expanse.core.entities.components.StaticComponent}
   */
  private boolean refreshStaticOctree;

  public WorldOctreeSystem(LevelOctree<PositionComponent> octree) {
    super(Family.all(PositionComponent.class).get());
    this.octree = octree;
  }

  @Override
  public void dispose() {
    octree = null;
  }

  @Override
  public void update(float deltaTime) {
    octree.getDynamicOctree().clear();
    if (refreshStaticOctree)
      octree.getStaticOctree().clear();
    super.update(deltaTime);
    refreshStaticOctree = false;
  }

  /**
   * For every {@link Entity} place centered bounding box at its position
   * @param entity
   * @param deltaTime
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    if (Components.Static.has(entity)) {
      if (refreshStaticOctree) {
        calculateBoundingBoxAndInsertIntoOctree(entity, octree.getStaticOctree());
      }
    } else {
      calculateBoundingBoxAndInsertIntoOctree(entity, octree.getDynamicOctree());
    }
  }

  private void calculateBoundingBoxAndInsertIntoOctree(Entity entity, OctreeNode<PositionComponent> targetOctree) {
    PositionComponent positionComponent = Components.Position.get(entity);

    halfDimenTemp.set(positionComponent.dimension).scl(0.5f);
    minVecTemp.set(positionComponent).sub(halfDimenTemp);
    maxVecTemp.set(positionComponent).add(halfDimenTemp);
    positionComponent.entity = entity;
    positionComponent.boundingBox.set(minVecTemp, maxVecTemp);

    targetOctree.insert(positionComponent);
  }

  @Override
  public void entityAdded(Entity entity) {
    if (Components.Static.has(entity)) {
      refreshStaticOctree = true;
    }
  }

  @Override
  public void entityRemoved(Entity entity) {
    if (Components.Static.has(entity)) {
      refreshStaticOctree = true;
    }
  }
}
