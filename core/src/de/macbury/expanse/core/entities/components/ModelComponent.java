package de.macbury.expanse.core.entities.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.blueprint.BlueprintConsumer;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
import de.macbury.expanse.core.graphics.Lod;

/**
 * This component wraps {@link ModelInstance}
 */
public class ModelComponent extends RenderableComponent implements BlueprintConsumer<ModelComponent.Blueprint> {
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

  @Override
  public void consume(Blueprint blueprint) {
    reset();
    this.modelInstance = new ModelInstance(blueprint.model);
  }

  public static class Blueprint extends ComponentBlueprint {
    public String name;
    public Model model;

    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {
      AssetDescriptor modelAssetDescriptor = new AssetDescriptor(name, Model.class);
      dependencies.add(modelAssetDescriptor);
    }

    @Override
    public void assignDependencies(Assets assets) {
      model = assets.get(name);
    }

    @Override
    public void dispose() {
      model = null;
    }
  }
}
