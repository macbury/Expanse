package de.macbury.expanse.core.octree;

import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by macbury on 21.10.14.
 */
public interface OctreeObject {
  public BoundingBox getBoundingBox(BoundingBox outBox);
  public void setOctreeParent(OctreeNode parent);
}
