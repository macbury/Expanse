package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.systems.SpriteRenderingSystem;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {

  private SpriteRenderingSystem spriteRenderingSystem;

  public EntityManager(Camera renderingCamera) {
    super();

    this.spriteRenderingSystem = new SpriteRenderingSystem(renderingCamera);
    addSystem(spriteRenderingSystem);
  }

  @Override
  public void dispose() {
    removeAllEntities();
    clearPools();
    spriteRenderingSystem.dispose();
    spriteRenderingSystem = null;
  }
}
