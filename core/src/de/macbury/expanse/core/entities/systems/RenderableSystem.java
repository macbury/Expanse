package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.OctreeIteratingSystem;
import de.macbury.expanse.core.entities.components.*;
import de.macbury.expanse.core.graphics.Lod;
import de.macbury.expanse.core.graphics.LodModelBatch;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This system finds all {@link Entity} with components {@link de.macbury.expanse.core.entities.components.RenderableComponent} and {@link de.macbury.expanse.core.entities.components.PositionComponent}
 * and adds it to {@link com.badlogic.gdx.graphics.g3d.ModelBatch}
 */
//TODO separate systems for rendering in color, reflection and glow batch, We can use components to make it use diffrent systems
public class RenderableSystem extends OctreeIteratingSystem implements Disposable {
  private final Environment env;
  private LodModelBatch modelBatch;
  private GameCamera camera;
  private BoundingBox tempBox = new BoundingBox();
  private Vector3 tempVec     = new Vector3();
  public RenderableSystem(WorldOctree octree, GameCamera camera, LodModelBatch modelBatch) {
    super(octree, Family.all(
      PositionComponent.class
    ).one(
      ModelComponent.class,
      TerrainRenderableComponent.class
    ).get());
    this.camera     = camera;
    this.modelBatch = modelBatch;
    this.env        = new Environment();// TODO move this to provider or something, else, this should not be initialized here
    env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.5f, 1f));
    env.add(new DirectionalLight().set(0.6f, 0.6f, 0.5f, -1f, -0.8f, -0.2f));
    env.set(new ColorAttribute(ColorAttribute.Fog,1f,1f,1f,1f));
  }

  @Override
  public void update(float deltaTime) {
    modelBatch.begin(camera); {
      super.update(deltaTime);
    } modelBatch.end();
  }

  /**
   * Update position and rotation from {@link PositionComponent} and render with current {@link Environment}
   * @param entity
   * @param deltaTime
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent positionComponent     = Components.Position.get(entity);

    if (Components.Model.has(entity)) {
      ModelComponent modelComponent = Components.Model.get(entity);
      modelComponent.modelInstance.transform.idt().rotate(Vector3.Y, positionComponent.rotationDeg).trn(
        positionComponent
      );

      //throw new GdxRuntimeException("Fix this!");
      modelBatch.render(modelComponent.modelInstance, env);
    }

    if (Components.TerrainRenderable.has(entity)) {
      modelBatch.render(
        Components.TerrainRenderable.get(entity),
        env,
        Lod.High
      );
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    modelBatch = null;
    camera     = null;
  }

  /**
   * Now we check if {@link Entity} {@link BodyComponent} is in frustrum
   * @param entity enity to check
   * @return
   */
  @Override
  public boolean checkEntity(Entity entity) {
    Components.Position.get(entity).getBoundingBox(tempBox);
    return camera.normalOrDebugFrustrum().boundsInFrustum(tempBox);
  }

  /**
   * First we check if node is in frustrum
   * @param node
   * @return
   */
  @Override
  public boolean checkNode(OctreeNode node) {
    return camera.normalOrDebugFrustrum().boundsInFrustum(node.getBounds());
  }
}
