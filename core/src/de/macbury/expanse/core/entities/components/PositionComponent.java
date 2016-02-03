package de.macbury.expanse.core.entities.components;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.blueprint.BlueprintConsumer;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.OctreeObject;

/**
 * This class contains information about entity position on Screen, and if this entity is visible on current screen;
 */
public class PositionComponent extends Vector3 implements Component, Pool.Poolable, OctreeObject, BlueprintConsumer<PositionComponent.Blueprint> {
  public float rotationDeg;
  public OctreeNode parent;
  public Entity entity;
  public Vector3 dimension = new Vector3();
  /**
   * For octree checking, Computed using current position and dimensions, calculated in {@link de.macbury.expanse.core.entities.systems.WorldOctreeSystem}
   */
  public BoundingBox boundingBox = new BoundingBox();

  @Override
  public void reset() {
    setZero();
    rotationDeg = 0;
    parent = null;
    entity = null;
    dimension.set(1,1,1);
  }

  @Override
  public void getBoundingBox(BoundingBox outBox) {
    outBox.set(boundingBox);
  }

  @Override
  public void setOctreeParent(OctreeNode parent) {
    this.parent = parent;
  }

  @Override
  public void consume(Blueprint blueprint) {
    reset();
    set(blueprint.x, blueprint.y, blueprint.z);
    rotationDeg = blueprint.rotation;
    dimension.set(blueprint.dimension);
  }

  public static class Blueprint extends ComponentBlueprint {
    public float x;
    public float y;
    public float z;
    public float rotation;
    public Vector3 dimension;

    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {

    }

    @Override
    public void assignDependencies(Assets assets) {

    }

    @Override
    public void dispose() {

    }
  }
}