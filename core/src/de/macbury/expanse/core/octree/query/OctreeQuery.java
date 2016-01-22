package de.macbury.expanse.core.octree.query;

import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.OctreeObject;

/**
 * Created by macbury on 29.10.14.
 */
public interface OctreeQuery<E extends OctreeObject> {
  public abstract boolean checkNode(OctreeNode node);
  public abstract boolean checkObject(E object);
}
