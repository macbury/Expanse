package de.macbury.expanse.core.scripts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.mozilla.javascript.*;


/**
 * This class runs script in thread
 */
public class ScriptRunner implements Disposable {
  private enum State {
    Pending, Running, Starting, Paused, Stopped
  }
  private static final String TAG = "ScriptRunner";
  private Array<BaseKeyword> keywords;
  private String source;
  private RobotScriptContext context;
  private ScriptableObject mainScope;
  private Script script;
  private State state;
  private Thread internalThread;
  private ContinuationPending continuationPending;
  private Object result; // passed to continuation pending
  private boolean loop = true;
  private Array<ScriptRunnerListener> listeners;
  private Object owner;

  /**
   * Initialize new script runner
   */
  public ScriptRunner(String source, Array<BaseKeyword> keywords, boolean loop) {
    this.source             = source;
    state                   = ScriptRunner.State.Pending;
    this.loop               = loop;
    this.keywords           = keywords;
    this.listeners          = new Array<ScriptRunnerListener>();

    internalThread          = new Thread() {
      @Override
      public void run() {
        ScriptRunner.this.run();
      }
    };
  }

  private void run() {
    try {
      configureContext();
      compileScript();

      state = ScriptRunner.State.Running;

      for (ScriptRunnerListener listener : listeners) {
        listener.onScriptStart(ScriptRunner.this);
      }

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

              /**
               * If result is exception throw it!
               */
              if (Exception.class.isInstance(result)) {

                throw (Exception)result;
              } else {
                /**
                 * Otherwise resume script from last state
                 */
                context.resumeContinuation(continuationPending.getContinuation(), mainScope, result);
              }

              result              = null;
              continuationPending = null;
            } else {
              context.executeScriptWithContinuations(script, mainScope);
            }

            /**
             * If nothing paused the script and is not set to loop we can now stop it
             */
            if (!loop)
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
          for (ScriptRunnerListener listener : listeners) {
            listener.onScriptAbort(ScriptRunner.this);
          }
        } catch (ContinuationPending continuationPending) {
          /**
           * Set state to {@link de.macbury.expanse.core.scripts.ScriptRunner.State#Paused} and store continuationPending
           */
          state = ScriptRunner.State.Paused;
          ScriptRunner.this.continuationPending = continuationPending;
          for (ScriptRunnerListener listener : listeners) {
            listener.onScriptPause(ScriptRunner.this, continuationPending);
          }
        } catch (InterruptedException e) {
          /**
           * Set state to {@link de.macbury.expanse.core.scripts.ScriptRunner.State#Stopped} if antyhind did try to interput this thread
           */
          state = ScriptRunner.State.Stopped;
        } catch (Exception e) {
          state = ScriptRunner.State.Stopped;
          for (ScriptRunnerListener listener : listeners) {
            listener.onScriptException(ScriptRunner.this, e);
          }
        }
      }
    } finally {
      if (listeners != null) {
        for (ScriptRunnerListener listener : listeners) {
          listener.onScriptFinish(ScriptRunner.this);
        }
      }
      internalThread      = null;
      continuationPending = null;
      result              = null;
      owner               = null;
      state               = ScriptRunner.State.Stopped;
      Gdx.app.debug(TAG, "Exiting script");
      Context.exit();

    }
  }

  /**
   * Adds listener
   * @param listener
   * @return if listener have been added
   */
  public boolean addListener(ScriptRunnerListener listener) {
    if (listeners.contains(listener, true)) {
      return false;
    } else {
      listeners.add(listener);
      return true;
    }
  }

  /**
   * Remove listener
   * @param listener
   * @return
   */
  public boolean removeListener(ScriptRunnerListener listener) {
    if (listeners.contains(listener, true)) {;
      listeners.removeValue(listener, true);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Start script execution in separate thread!
   */
  public boolean start() {
    if (state == State.Pending) {
      state = State.Starting;
      internalThread.start();
      return true;
    } else {
      return false;
    }
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
   * @param result this will be returned in function that paused script. If result is instance of Exception, then it triggers exception!
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
    if (state == State.Running || State.Starting == state) {
      state = State.Stopped;
      loop  = false;
      if (context != null)
        context.abort();
      internalThread.interrupt();
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
   * Starts {@link RobotScriptContext} and configure main scope
   */
  private void configureContext() {
    Gdx.app.debug(TAG, "Configuring context");
    this.context   = (RobotScriptContext) Context.enter();
    this.mainScope = context.initStandardObjects(null, true);

    for (BaseKeyword keyword : keywords) {
      ScriptableObject coreScope  = context.initStandardObjects();
      NativeFunction function     = keyword.register(context, coreScope);
      mainScope.put(function.getFunctionName(), mainScope, function);
    }
  }

  @Override
  public void dispose() {
    stop();

    for (BaseKeyword keyword : keywords) {
      keyword.dispose();
    }

    keywords.clear();
    keywords = null;
    source = null;
    context = null;
    mainScope = null;
    internalThread = null;
    continuationPending = null;
    listeners.clear();
    listeners = null;
    owner = null;
  }

  public Object getOwner() {
    return owner;
  }

  public void setOwner(Object owner) {
    this.owner = owner;
  }
}
