package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.OctreeObject;

/**
 * This component describe entity size and position in world
 */
public class BodyComponent extends BoundingBox implements Component, Pool.Poolable, OctreeObject {
  public OctreeNode parent;
  public Entity entity;
  public Vector3 dimension = new Vector3();

  @Override
  public void getBoundingBox(BoundingBox outBox) {
    outBox.set(this);
  }

  @Override
  public void setOctreeParent(OctreeNode parent) {
    this.parent = parent;
  }

  @Override
  public void reset() {
    entity = null;
    setOctreeParent(null);
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }
}
