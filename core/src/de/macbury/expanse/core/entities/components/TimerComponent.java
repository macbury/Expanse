package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

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
}
