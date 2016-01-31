package de.macbury.expanse.core.entities.components;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * This component contain renderable with terrain
 */
public class TerrainRenderableComponent extends RenderableComponent {
  public Renderable terrainTile;
  @Override
  public void reset() {
    terrainTile = null;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (terrainTile != null) {
      renderables.add(terrainTile);
    }
  }
}
