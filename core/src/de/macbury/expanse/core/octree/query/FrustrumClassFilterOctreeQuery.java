package de.macbury.expanse.core.octree.query;


import de.macbury.expanse.core.octree.OctreeObject;

/**
 * Created by macbury on 29.10.14.
 */
public class FrustrumClassFilterOctreeQuery extends FrustrumOctreeQuery {
  private Class klass;

  @Override
  public boolean checkObject(OctreeObject object) {
    return super.checkObject(object) && klass.isInstance(object);
  }

  public Class getKlass() {
    return klass;
  }

  public void setKlass(Class klass) {
    this.klass = klass;
  }
}
