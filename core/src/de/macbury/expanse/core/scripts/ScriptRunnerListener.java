package de.macbury.expanse.core.scripts;

import org.mozilla.javascript.ContinuationPending;

/**
 * Created on 20.01.16.
 */
public interface ScriptRunnerListener {
  /**
   * Triggered after script start
   *
   * @param scriptRunner
   */
  public void onScriptStart(ScriptRunner scriptRunner);

  /**
   * Triggered on any script exception
   *
   * @param scriptRunner
   * @param exception
   */
  public void onScriptException(ScriptRunner scriptRunner, Exception exception);

  /**
   * Triggered if script is aborted
   *
   * @param scriptRunner
   */
  public void onScriptAbort(ScriptRunner scriptRunner);

  /**
   * Triggered on script pause/yield
   *
   * @param scriptRunner
   */
  public void onScriptPause(ScriptRunner scriptRunner, ContinuationPending continuationPending);

  /**
   * Triggered on script finish
   *
   * @param scriptRunner
   */
  public void onScriptFinish(ScriptRunner scriptRunner);
}
