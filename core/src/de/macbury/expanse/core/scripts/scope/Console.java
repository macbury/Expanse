package de.macbury.expanse.core.scripts.scope;

import com.badlogic.gdx.Gdx;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

/**
 * This class wraps {@link com.badlogic.gdx.Gdx.app} logging into console object like in browser javascript
 */
public class Console extends ScriptableObject {

  private static final String TAG = "Console";

  @ExposeAsGlobalFunction
  public void log(String message) {
    Gdx.app.log(TAG, message);
  }

  @ExposeAsGlobalFunction
  public void test() {
    Gdx.app.log(TAG, "TEST");
  }

  @ExposeAsGlobalFunction
  public void pause() {
    throw Context.getCurrentContext().captureContinuation();
  }

  @ExposeAsGlobalFunction
  public void sleep(int miliseconds) {
    try {
      Thread.sleep(miliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getClassName() {
    return getClass().getName();
  }
}
