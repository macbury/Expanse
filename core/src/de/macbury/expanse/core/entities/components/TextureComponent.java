package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;

/**
 * This component helps rendering textures
 */
public class TextureComponent implements Component, Pool.Poolable {
  public Texture texture;
  @Override
  public void reset() {
    texture = null;
  }
}
