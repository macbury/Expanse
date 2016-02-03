package de.macbury.expanse.core.entities.blueprint;

/**
 * This class is basic stuff for blueprint consumer. Blueprint consumer is interface that need to be appended to component that want to apply {@link ComponentBlueprint}
 */
public interface BlueprintConsumer<T extends ComponentBlueprint> {
  public void consume(T blueprint);
}
