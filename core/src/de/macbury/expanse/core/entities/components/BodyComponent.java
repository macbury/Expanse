package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
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
  public Vector3 dimensions = new Vector3();
  public OctreeNode parent;

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
    dimensions.setZero();
    setOctreeParent(null);
  }
}
