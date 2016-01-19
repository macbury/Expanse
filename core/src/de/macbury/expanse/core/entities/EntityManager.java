package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Disposable;

/**
 * This class manages all entities in game
 */
public class EntityManager extends PooledEngine implements Disposable {
  @Override
  public void dispose() {
    removeAllEntities();
    clearPools();
  }
}
