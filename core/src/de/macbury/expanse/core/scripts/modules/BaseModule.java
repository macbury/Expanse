package de.macbury.expanse.core.scripts.modules;

import org.mozilla.javascript.Context;

/**
 * This module contains all special functions used by other modules;
 */
public class BaseModule {
  /**
   * Yields control of script to main engine loop and pausing current script
   */
  @ExposeAsGlobalFunction
  public void yield() {
    throw Context.getCurrentContext().captureContinuation();
  }
}
