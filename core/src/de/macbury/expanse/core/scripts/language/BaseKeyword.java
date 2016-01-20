package de.macbury.expanse.core.scripts.language;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import de.macbury.expanse.core.entities.Messages;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 */
public abstract class BaseKeyword implements Disposable {
  private final static ObjectMap<String, String> sourceCache = new ObjectMap<String, String>();
  private final String namespace;
  private final String fileName;
  /**
   * For communication with engine
   */
  protected Messages messages;
  /**
   * In which entity context should be visible this keyword
   */
  protected Entity entity;

  public BaseKeyword(Messages messages, Entity entity, String namespace, String fileName) {
    this.messages  = messages;
    this.entity    = entity;
    this.namespace = namespace;
    this.fileName  = fileName;
  }

  /**
   * Loads source from file or from cache
   * @param filename
   * @return
   */
  private static String loadSource(String filename) {
    if (!sourceCache.containsKey(filename)) {
      sourceCache.put(filename, Gdx.files.classpath("core/"+filename+".js").readString());
    }

    return sourceCache.get(filename);
  }

  /**
   * Registers keyword under scope and return its body function
   * @param context
   * @param coreScope
   * @return
   */
  public NativeFunction register(Context context, ScriptableObject coreScope) {
    coreScope.put(namespace, coreScope, this);

    NativeFunction function     = (NativeFunction)context.compileFunction(
      coreScope,
      loadSource(fileName),
      fileName,
      0,
      null
    );

    return function;
  }

  /**
   * Yields control of script to main engine loop and pausing current script
   */
  public void yield() {
    throw Context.getCurrentContext().captureContinuation();
  }

  @Override
  public void dispose() {
    messages = null;
    entity   = null;
  }
}
