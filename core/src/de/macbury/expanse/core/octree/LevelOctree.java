package de.macbury.expanse.core.octree;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.octree.query.OctreeQuery;

/**
 * Level octree containing static octree, and dynamic octree
 */
public class LevelOctree<C extends OctreeObject> implements Disposable {
  private final Array<OctreeNode<C>> nodes;
  private OctreeNode<C> staticOctree;
  private OctreeNode<C> dynamicOctree;
  private int i;

  public LevelOctree() {
    this.nodes         = new Array<OctreeNode<C>>();
    this.staticOctree  = new OctreeNode<C>();
    this.dynamicOctree = new OctreeNode<C>();
    nodes.add(staticOctree);
    nodes.add(dynamicOctree);
    this.i     = 0;
  }

  /**
   * This octree contains static objects. Its is only updated after insert or remove on next frame
   * @return
   */
  public OctreeNode<C> getStaticOctree() {
    return staticOctree;
  }

  /**
   * This octree contains objects that position is updated. It is updated each frame
   * @return
   */
  public OctreeNode<C> getDynamicOctree() {
    return dynamicOctree;
  }

  /**
   * Query static and dynamic octree for objects
   * @param returnObjects
   * @param query
   */
  public void retrieve(Array<C> returnObjects, OctreeQuery<C> query) {
    for (i = 0; i < nodes.size; i++) {
      nodes.get(i).retrieve(returnObjects, query);
    }
  }

  @Override
  public void dispose() {
    for (OctreeNode node : nodes) {
      node.dispose();
    }
    staticOctree = null;
    dynamicOctree = null;
    nodes.clear();
  }

  /**
   * Sets bounds for dynamic and static octree
   * @param boundingBox
   */
  public void setBounds(BoundingBox boundingBox) {
    for (i = 0; i < nodes.size; i++) {
      nodes.get(i).setBounds(boundingBox);
    }
  }
}
