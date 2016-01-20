package de.macbury.expanse.core.scripts.modules;

import com.badlogic.gdx.Gdx;
import org.mozilla.javascript.Context;
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

  @Override
  public String getClassName() {
    return getClass().getName();
  }
}
