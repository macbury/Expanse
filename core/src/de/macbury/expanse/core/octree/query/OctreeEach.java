package de.macbury.expanse.core.octree.query;


import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.OctreeObject;

/**
 * Created by macbury on 03.08.15.
 */
public abstract class OctreeEach<T> extends OctreeQuery {
  @Override
  public boolean checkNode(OctreeNode node) {
    return false;
  }

  @Override
  public boolean checkObject(OctreeObject object) {
    return false;
  }

  public abstract void found(T object);
}
