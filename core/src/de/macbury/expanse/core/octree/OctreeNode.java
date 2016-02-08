package de.macbury.expanse.core.octree;

import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.octree.query.OctreeQuery;

/**
 * This is octree will be used for collision detection and frustrum culling
 */
public class OctreeNode<E extends OctreeObject> implements Pool.Poolable, Disposable {
  /**
   * The maximum number of levels to break down to.
   */
  public final static int MAX_LEVELS    = 8;
  /**
   * The maximum number of objects per node.
   */
  public final static int MAX_OBJECTS   = 10;
  /**
   * The current level.
   */
  private int                 level;
  private Array<E>            objects;
  private Array<OctreeNode>   nodes;
  private BoundingBox         bounds;
  private final Vector3       tempA;
  private final Vector3       tempB;
  private final Vector3       tempC;
  private OctreeNode          parent;

  private Vector3             center  = new Vector3();
  private Vector3             min     = new Vector3();
  private Vector3             max     = new Vector3();
  private BoundingBox         tempBox = new BoundingBox();
  private static final Pool<OctreeNode> octreeNodePool = new Pool<OctreeNode>() {
    @Override
    protected OctreeNode newObject() {
      return new OctreeNode();
    }
  };

  public OctreeNode() {
    this.tempA    = new Vector3();
    this.tempB    = new Vector3();
    this.tempC    = new Vector3();
    this.level    = 0;
    this.objects  = new Array<E>();
    this.nodes    = new Array<OctreeNode>();
    this.bounds   = new BoundingBox();
    this.parent   = null;
    clear();
  }

  /**
   * Determine which node the object belongs to.
   * @param object The object in which to check.
   * @return Index of the subnode (0-7), or -1 if rect cannot completely fit within a subnode and is part of the parent node.
   */
  public int getIndex(E object) {
    object.getBoundingBox(tempBox);
    return getIndex(tempBox);
  }

  /**
   * Determine which node the vector belongs to.
   * @param point The bounds in which to check.
   * @return Index of the subnode (0-7), or -1 if rect cannot completely fit within a subnode and is part of the parent node.
   */
  private int getIndex(Vector3 point) {
    int index = -1;
    if (haveNodes()) {
      for (int i = 0; i < nodes.size; i++) {
        OctreeNode node = nodes.get(i);
        if (node.getBounds().contains(point)) {
          index = i;
          break;
        }
      }
    }
    return index;
  }

  /**
   * Determine which node the bounding box belongs to.
   * @param pRect The bounds in which to check.
   * @return Index of the subnode (0-7), or -1 if rect cannot completely fit within a subnode and is part of the parent node.
   */
  public int getIndex(BoundingBox pRect) {
    int index = -1;
    for (int i = 0; i < nodes.size; i++) {
      OctreeNode node = nodes.get(i);
      if (node.contains(pRect)) {
        index = i;
        break;
      }
    }
    return index;
  }

  /**
   * Is the rect in bounds of current node
   * @param pRect
   * @return
   */
  public boolean contains(BoundingBox pRect) {
    return bounds.contains(pRect);
  }

  public void insert(Array<E> objectsToInsert) {
    for (int i = 0; i < objectsToInsert.size; i++) {
      insert(objectsToInsert.get(i));
    }
  }

  /**
   * Insert the object into the node. If the node exceeds the capacity, it will split and build all objects to their corresponding subnodes.
   * @param objectToInsert The Body object to insert into the octree
   */
  public void insert(E objectToInsert) {
    if (haveNodes()) {
      int index = getIndex(objectToInsert);
      if (index != -1) {
        nodes.get(index).insert(objectToInsert);
        return;
      }
    }

    objects.add(objectToInsert);
    objectToInsert.setOctreeParent(this);

    if (objects.size > MAX_OBJECTS && level < MAX_LEVELS) {
      if (!haveNodes())
        split();

      int i = 0;
      while (i < objects.size) {
        E currentObject            = objects.get(i);
        int index                  = getIndex(currentObject);
        if (index != -1) {
          objects.removeValue(currentObject, false);
          nodes.get(index).insert(currentObject);
        } else {
          i++;
        }
      }
    }

  }

  public boolean remove(E object) {
    int index = getIndex(object);
    if (index == -1) {
      return objects.removeValue(object, true);
    } else {
      return nodes.get(index).remove(object);
    }
  }

  public OctreeNode getNode(OctreePart part) {
    return nodes.get(part.getIndex());
  }

  /**
   * Split the node into 8 subnodes
   */
  private void split() {
    center = bounds.getCenter(center);
    bounds.getMin(min);
    bounds.getMax(max);

    buildNode(tempA.set(max).set(max.x, max.y, min.z), center, OctreePart.FrontTopLeft);
    buildNode(tempA.set(min).set(min.x, max.y, min.z), center, OctreePart.FrontTopRight);
    buildNode(min, center, OctreePart.FrontBottomRight);
    buildNode(tempA.set(max).set(max.x, min.y, min.z), center, OctreePart.FrontTopLeft);
    buildNode(tempA.set(min).set(min.x, max.y, max.z), center, OctreePart.BackTopRight);
    buildNode(max, center, OctreePart.BackTopLeft);
    buildNode(tempA.set(max).set(min.x, min.y, max.z), center, OctreePart.BackBottomRight);
    buildNode(tempA.set(max).set(max.x, min.y, max.z), center, OctreePart.BackBottomLeft);

  }

  private void buildNode(Vector3 min, Vector3 max, OctreePart part) {
    tempBox.set(min, max);
    OctreeNode nodeQuadrant = OctreeNode.node(level, tempBox);
    nodeQuadrant.setParent(this);
    nodes.add(nodeQuadrant);
  }

  public void getDimension(Vector3 out) {
    bounds.getDimensions(out);
  }

  public float getWidth() {
    bounds.getDimensions(tempA);
    return tempA.x;
  }

  public float getHeight() {
    bounds.getDimensions(tempA);
    return tempA.y;
  }

  public float getDepth() {
    bounds.getDimensions(tempA);
    return tempA.z;
  }

  public OctreeNode getParent() {
    return parent;
  }

  public void setParent(OctreeNode parent) {
    this.parent = parent;
  }

  public boolean haveNodes() {
    return nodes.size != 0;
  }

  public int getLevel() {
    return level;
  }

  public BoundingBox getBounds() {
    return bounds;
  }

  public void setBounds(BoundingBox box) {
    bounds.set(box);
    clear();
  }

  /**
   * Clears octree objects and nodes
   */
  public void clear() {
    for (E o : objects) {
      o.setOctreeParent(null);
    }
    objects.clear();
    for (OctreeNode node : nodes) {
      octreeNodePool.free(node);
    }
    nodes.clear();
  }

  @Override
  public void reset() {
    this.level = 0;
    this.bounds.set(Vector3.Zero, Vector3.Zero);
    parent = null;
    clear();
  }

  public static OctreeNode root() {
    OctreeNode node = octreeNodePool.obtain();
    return node;
  }

  public static OctreeNode node(int parentLevel, BoundingBox box) {
    OctreeNode node = octreeNodePool.obtain();
    node.setLevel(parentLevel + 1);
    node.setBounds(box);
    return node;
  }

  @Override
  public void dispose() {
    clear();
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void bottomNodes(Array<OctreeNode> out) {
    if (haveNodes()) {
      for(OctreeNode node : nodes) {
        node.bottomNodes(out);
      }
    } else {
      out.add(this);
    }
  }

  public void retriveNodes(Array<OctreeNode> outNodes, Frustum frustum) {
    if (haveNodes()) {
      for(OctreeNode node : nodes) {
        node.retriveNodes(outNodes, frustum);
      }
    } else {
      if (frustum.boundsInFrustum(this.getBounds())) {
        outNodes.add(this);
      }
    }
  }

  public void retrieve(Array<E> returnObjects, Frustum frustum, boolean checkObjectsToo) {
    if (haveNodes()) {
      for(OctreeNode node : nodes) {
        if (frustum.boundsInFrustum(node.getBounds())) {
          node.retrieve(returnObjects, frustum, checkObjectsToo);
        }
      }
    }

    if (checkObjectsToo) {
      for (E object : objects) {
        object.getBoundingBox(tempBox);
        if (frustum.boundsInFrustum(tempBox)) {
          returnObjects.add(object);
        }
      }
    } else {
      returnObjects.addAll(objects);
    }

  }

  public void retrieve(Array<E> returnObjects, BoundingBox object) {
    int index = getIndex(object);
    if (index != -1 && haveNodes()) {
      nodes.get(index).retrieve(returnObjects, object);
    }
    returnObjects.addAll(objects);
  }

  /**
   * Return objects that have bounding box containg point
   * @param returnObjects
   * @param point
   */
  public void retrieve(Array<E> returnObjects, Vector3 point) {
    int index = getIndex(point);
    if (index != -1 && haveNodes()) {
      nodes.get(index).retrieve(returnObjects, point);
    }

    for (int i = 0; i < objects.size; i++) {
      E octreeObject = objects.get(i);
      octreeObject.getBoundingBox(tempBox);
      if (tempBox.contains(point)) {
        returnObjects.add(octreeObject);
      }
    }
  }

  public void retrieve(Array<E> returnObjects, OctreeObject object) {
    object.getBoundingBox(tempBox);
    retrieve(returnObjects, tempBox);
  }


  public void retrieve(Array<E> returnObjects, OctreeQuery query) {
    if (haveNodes()) {
      for(OctreeNode node : nodes) {
        if (query.checkNode(node)) {
          node.retrieve(returnObjects, query);
        }
      }
    }

    for (E object : objects) {
      if (query.checkObject(object)) {
        returnObjects.add(object);
      }
    }
  }

  public Array<E> getObjects() {
    return objects;
  }

  /**
   * Returns all objects
   * @param returnObjects
   */
  public void retrieve(Array<E> returnObjects) {
    if (haveNodes()) {
      for(OctreeNode node : nodes) {
        node.retrieve(returnObjects);
      }
    }

    returnObjects.addAll(this.objects);
  }
}
