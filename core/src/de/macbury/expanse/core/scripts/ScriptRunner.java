package de.macbury.expanse.core.scripts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.scripts.modules.ExposeAsGlobalFunction;
import org.mozilla.javascript.*;

import java.lang.reflect.Method;


/**
 * This class runs script in thread
 */
public class ScriptRunner implements Disposable {
  private enum State {
    Pending, Running, Paused, Stopped
  }
  private static final String TAG = "ScriptRunner";
  private Array<ScriptableObject> globalScopeObjects;
  private String source;
  private RobotScriptContext context;
  private ScriptableObject mainScope;
  private Script script;
  private State state;
  private ScriptThread internalThread;
  private ContinuationPending continuationPending;
  private Object result; // passed to continuation pending


  /**
   * Initialize new script runner
   * @param source in js script
   * @param globalScopeObjects list of objects to get global functions
   */
  public ScriptRunner(String source, Array<ScriptableObject> globalScopeObjects) {
    this.source             = source;
    this.globalScopeObjects = globalScopeObjects;
    internalThread          = new ScriptThread();
    state                   = ScriptRunner.State.Pending;
  }

  /**
   * Start script execution in separate thread!
   */
  public void start() {
    state = State.Running;
    internalThread.start();
  }

  public void startAndWait() {
    start();
    try {
      internalThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Resume script
   * @return true if script has been paused
   */
  public boolean resume(Object result) {
    if (state == State.Paused) {
      state = State.Running;
      this.result = result;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Use the dark force to stop this script.
   * @return true if script is stopped!
   */
  public boolean stop() {
    if (state == State.Running) {
      context.abort();
      try {
        internalThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return true;
    } else if (state == State.Paused){
      state = State.Stopped;
      return true;
    } else {
      return false;
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


  @Override
  public void dispose() {
    stop();
    globalScopeObjects.clear();
    globalScopeObjects = null;
    source = null;
    context = null;
    mainScope = null;
    internalThread = null;
    continuationPending = null;
  }

  /**
   * This just helper class for handling thread and hide all thread methods
   */
  private class ScriptThread extends Thread {
    @Override
    public void run() {
      try {
        configureContext();
        compileScript();

        state = ScriptRunner.State.Running;

        /**
         * Run forever while state is not {@link de.macbury.expanse.core.scripts.ScriptRunner.State#Stopped}
         */
        while(state != ScriptRunner.State.Stopped) {
          try {
            if (state == ScriptRunner.State.Running) {
              /**
               * If script is running and have pending continuation run it, otherwise run script from start
               */
              if (continuationPending != null) {
                context.resumeContinuation(continuationPending.getContinuation(), mainScope, result);
                result = null;
              } else {
                context.executeScriptWithContinuations(script, mainScope);
              }

              /**
               * If nothing paused the script we can now stop it
               */
              state = ScriptRunner.State.Stopped;
            } else {
              Thread.sleep(10);
            }
          } catch (RobotScriptContext.ManualScriptStopException exception) {
            /**
             * Set state to {@link de.macbury.expanse.core.scripts.ScriptRunner.State#Stopped} if user did manualy stop script
             */
            Gdx.app.debug(TAG, "Script stopped by user");
            state = ScriptRunner.State.Stopped;
          } catch (ContinuationPending continuationPending) {
            /**
             * Set state to {@link de.macbury.expanse.core.scripts.ScriptRunner.State#Paused} and store continuationPending
             */
            ScriptRunner.this.continuationPending = continuationPending;
            state = ScriptRunner.State.Paused;
          } catch (InterruptedException e) {
            /**
             * Set state to {@link de.macbury.expanse.core.scripts.ScriptRunner.State#Stopped} if antyhind did try to interput this thread
             */
            state = ScriptRunner.State.Stopped;
          }
        }
      } finally {
        continuationPending = null;
        result              = null;
        state               = ScriptRunner.State.Stopped;
        Gdx.app.debug(TAG, "Exiting script");
        Context.exit();
      }
    }
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
