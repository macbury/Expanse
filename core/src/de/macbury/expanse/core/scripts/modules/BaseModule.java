package de.macbury.expanse.core.scripts.modules;

import com.badlogic.gdx.utils.Disposable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * This module contains all special functions used by other modules;
 */
public abstract class BaseModule extends ScriptableObject implements Disposable {
  /**
   * Yields control of script to main engine loop and pausing current script
   */
  @ExposeAsGlobalFunction
  public void yield() {
    throw Context.getCurrentContext().captureContinuation();
  }
}
