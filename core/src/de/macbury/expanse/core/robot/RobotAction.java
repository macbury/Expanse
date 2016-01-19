package de.macbury.expanse.core.robot;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * This is base class for actions that robot can perform. It should be reversa
 */
public class RobotAction implements Pool.Poolable {
  protected ScriptRunner scriptRunner;
  @Override
  public void reset() {
    scriptRunner = null;
  }
}
