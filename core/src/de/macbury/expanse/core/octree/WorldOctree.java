package de.macbury.expanse.core.octree;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;

/**
 * Base octree
 */
public class WorldOctree extends OctreeNode<PositionComponent> {

  public WorldOctree() {
    super();
  }
}
