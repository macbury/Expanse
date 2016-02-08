package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;

/**
 * This component contains all information about how long is robot working,
 * how long is he waiting
 */
public class TimerComponent implements Pool.Poolable, Component {
  /**
   * In seconds how long is robot running
   */
  public float runTime;
  /**
   * In seconds how much robot need to wait. (Decreases over time until reaches 0)
   */
  public float waitFor;
  @Override
  public void reset() {
    runTime = 0;
    waitFor = 0;
  }

  public void setWaitFor(float waitForSeconds) {
    waitFor = waitForSeconds;
  }

  public boolean haveFinishingWaiting() {
    return waitFor <= 0;
  }

  /**
   * Just reset wait for variable to zero
   */
  public void finishWaiting() {
    waitFor = 0;
  }

  public static class Blueprint extends ComponentBlueprint<TimerComponent> {

    @Override
    public void prepareDependencies(Array<AssetDescriptor> dependencies) {

    }

    @Override
    public void assignDependencies(Assets assets) {

    }

    @Override
    public void applyTo(TimerComponent component, Entity target, Messages messages) {

    }

    @Override
    public void load(JsonValue source, Json json) {

    }

    @Override
    public void save(Json target, TimerComponent source) {

    }

    @Override
    public void dispose() {

    }
  }
}
