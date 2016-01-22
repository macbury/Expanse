package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.OctreeIteratingSystem;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.RenderableComponent;
import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.WorldOctree;

/**
 * This system finds all {@link Entity} with components {@link de.macbury.expanse.core.entities.components.RenderableComponent} and {@link de.macbury.expanse.core.entities.components.PositionComponent}
 * and adds it to {@link com.badlogic.gdx.graphics.g3d.ModelBatch}
 */
//TODO separate systems for rendering in color, reflection and glow batch, We can use components to make it use diffrent systems
public class RenderableSystem extends OctreeIteratingSystem implements Disposable {
  private final Environment env;
  private ModelBatch modelBatch;
  private Camera camera;
  private BoundingBox tempBox = new BoundingBox();

  public RenderableSystem(WorldOctree octree, Camera camera, ModelBatch modelBatch) {
    super(octree, Family.all(PositionComponent.class, RenderableComponent.class, BodyComponent.class).get());
    this.camera     = camera;
    this.modelBatch = modelBatch;
    this.env        = new Environment();// TODO move this to provider or something, else, this should not be initialized here
    env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
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
    RenderableComponent renderableComponent = Components.Renderable.get(entity);
    renderableComponent.modelInstance.transform.idt().rotate(Vector3.Y, positionComponent.rotationDeg).trn(
      positionComponent
    );
    modelBatch.render(renderableComponent, env);
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
    Components.Body.get(entity).getBoundingBox(tempBox);
    return camera.frustum.boundsInFrustum(tempBox);
  }

  /**
   * First we check if node is in frustrum
   * @param node
   * @return
   */
  @Override
  public boolean checkNode(OctreeNode node) {
    return camera.frustum.boundsInFrustum(node.getBounds());
  }
}
