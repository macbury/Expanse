package de.macbury.expanse.core.entities.components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.graphics.Lod;

/**
 * This component wraps {@link ModelInstance}
 */
public class ModelComponent extends RenderableComponent {
  public ModelInstance modelInstance;

  @Override
  public void reset() {
    modelInstance = null;
  }


  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool, Lod lod) {
    if (modelInstance != null) {
      modelInstance.getRenderables(renderables, pool);
    }
  }
}
