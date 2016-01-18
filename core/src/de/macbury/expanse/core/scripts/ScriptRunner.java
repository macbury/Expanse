package de.macbury.expanse.core.scripts;

import com.badlogic.gdx.utils.Disposable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

/**
 * This class runs script in thread
 */
public class ScriptRunner extends Thread implements Disposable {
  private RobotScriptContext context;
  private ScriptableObject mainScope;

  @Override
  public void run() {
    System.out.println("Entering context in thread");
    this.context = (RobotScriptContext) Context.enter();

    try {
      this.mainScope = context.initStandardObjects();
      Script script  = context.compileString("while(true){ System.out.println('J') }", "<src>", 1, null);
      System.out.println("running script");
      script.exec(context, mainScope);
    } catch (RobotScriptContextFactory.ManualScriptStopException exception) {
      System.out.println("user stopped scrip!");
    } finally {
      System.out.println("Exiting context in thread and finishing thread!");
      Context.exit();
    }
  }

  /**
   * Triggers script stop
   */
  public void finish() {
    context.stop();
    try {
      join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void dispose() {
    finish();
  }
}
