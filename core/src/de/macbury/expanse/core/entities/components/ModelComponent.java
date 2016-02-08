package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
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
    modelInstance.getRenderables(renderables, pool);
  }

  public static class Blueprint extends ComponentBlueprint<ModelComponent> {
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
    public void applyTo(ModelComponent component, Entity owner, Messages messages) {
      component.modelInstance = new ModelInstance(model);
    }

    @Override
    public void load(JsonValue source, Json json) {
      name = source.getString("name");
    }

    @Override
    public void save(Json target, ModelComponent source) {
      target.writeValue("name", name);
    }

    @Override
    public void dispose() {
      model = null;
    }
  }
}
