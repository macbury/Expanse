package de.macbury.expanse.core.entities.blueprint;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.EntityManager;

/**
 * This blueprint is used to build all entities using {@link de.macbury.expanse.core.entities.EntityManager} and all information in
 * {@link ComponentBlueprint}
 */
public class EntityBlueprint implements Disposable {
  private Array<ComponentBlueprint> componentBlueprints;

  public EntityBlueprint(Array<ComponentBlueprint> componentBlueprints) {
    this.componentBlueprints = new Array<ComponentBlueprint>(componentBlueprints);
  }

  /**
   * Creates entity using pools from entity manager
   * @param entityManager
   * @return
   */
  public Entity create(EntityManager entityManager) {
    Entity entity = entityManager.createEntity();
    return entity;
  }

  /**
   * Creates new entity and adds it to {@link EntityManager}
   * @param entityManager
   * @return
   */
  public Entity createAndAdd(EntityManager entityManager) {
    Entity entity = create(entityManager);
    entityManager.addEntity(entity);
    return entity;
  }

  @Override
  public void dispose() {
    for (ComponentBlueprint blueprint : componentBlueprints) {
      blueprint.dispose();
    }
    componentBlueprints.clear();
  }
}
