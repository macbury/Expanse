package de.macbury.expanse.core.scripts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.scripts.scope.ExposeAsGlobalFunction;
import org.mozilla.javascript.*;

import java.lang.reflect.Method;


/**
 * This class runs script in thread
 */
public class ScriptRunner extends Thread implements Disposable {
  private static final String TAG = "ScriptRunner";
  private Array<ScriptableObject> globalScopeObjects;
  private String source;
  private RobotScriptContext context;
  private ScriptableObject mainScope;
  private Script script;

  /**
   * Initialize new script runner
   * @param source in js script
   * @param globalScopeObjects list of objects to get global functions
   */
  public ScriptRunner(String source, Array<ScriptableObject> globalScopeObjects) {
    this.source = source;
    this.globalScopeObjects = globalScopeObjects;
  }

  @Override
  public void run() {
    try {
      configureContext();
      compileScript();

      context.execPausableScript(script, mainScope);
    } catch (RobotScriptContext.ManualScriptStopException exception) {
      Gdx.app.debug(TAG, "Script stopped by user");
    } finally {
      Gdx.app.debug(TAG, "Exiting script");
      Context.exit();
    }
  }

  /**
   * Compile script
   */
  private void compileScript() {
    this.script  = context.compileString(source, "<src>", 1, null);
  }


  /**
   * Register as global functions all methods that use {@link ExposeAsGlobalFunction}
   * @param object
   */
  private void registerFunctionsFromObject(Scriptable object) {
    for (Method method : object.getClass().getMethods()) {
      if (method.isAnnotationPresent(ExposeAsGlobalFunction.class)) {
        FunctionObject function = new FunctionObject(method.getName(), method, object);

        mainScope.put(function.getFunctionName(), mainScope, function);
      }
    }
  }

  /**
   * Starts {@link RobotScriptContext} and configure main scope
   */
  private void configureContext() {
    Gdx.app.debug(TAG, "Configuring context");
    this.context   = (RobotScriptContext) Context.enter();
    this.mainScope = context.initStandardObjects(null, true);
    for (int i = 0; i < globalScopeObjects.size; i++) {
      registerFunctionsFromObject(globalScopeObjects.get(i));
    }

    globalScopeObjects = null;
  }

  /**
   * Triggers script stop
   */
  public void abort() {
    context.stop();
    try {
      join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void pauseScript() {
    context.pause();
  }

  public void resumeScript() {
    context.resume();
  }

  @Override
  public void dispose() {
    abort();
    globalScopeObjects.clear();
    globalScopeObjects = null;
    source = null;
    context = null;
    mainScope = null;
  }

  public interface ScriptListener {
    /**
     * Triggered after script start
     * @param scriptRunner
     */
    public void onScriptStart(ScriptRunner scriptRunner);

    /**
     * Triggered on any script exception
     * @param scriptRunner
     * @param exception
     */
    public void onScriptException(ScriptRunner scriptRunner, Exception exception);

    /**
     * Triggered if script is aborted
     * @param scriptRunner
     */
    public void onScriptAbort(ScriptRunner scriptRunner);

    /**
     * Triggered on script finish
     * @param scriptRunner
     */
    public void onScriptFinish(ScriptRunner scriptRunner);
  }
}
