package de.macbury.expanse.core.entities.components;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.graphics.Lod;

/**
 * This component contain renderable with terrain
 */
public class TerrainRenderableComponent extends RenderableComponent {
  public ObjectMap<Lod, Renderable> lodTiles = new ObjectMap<Lod, Renderable>();

  @Override
  public void reset() {
    lodTiles.clear();
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool, Lod lod) {
    renderables.add(lodTiles.get(lod));
  }
}
