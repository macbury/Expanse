package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * This component wraps {@link ModelInstance}
 */
public class RenderableComponent implements Component, Pool.Poolable, RenderableProvider {
  public ModelInstance modelInstance;

  @Override
  public void reset() {
    modelInstance = null;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (modelInstance != null) {
      modelInstance.getRenderables(renderables, pool);
    }
  }
}
