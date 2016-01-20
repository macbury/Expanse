package de.macbury.expanse.core.screens;


import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.Expanse;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;

/** <p>
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen and so on.
 * </p>
 * <p>
 * Note that {@link #dispose()} is not called automatically.
 * </p>*/
public abstract class ScreenBase implements Disposable {
  protected Expanse game;
  protected Assets assets;
  protected ScreenManager screens;
  protected Messages messages;

  /**
   * Links references to current {@link Expanse}
   * @param game
   */
  public void link(Expanse game) {
    this.unlink();
    this.game     = game;
    this.assets   = game.assets;
    this.screens  = game.screens;
    this.messages = game.messages;
  }

  /**
   * Unlink references to current {@link Expanse}
   */
  public void unlink() {
    this.game     = null;
    this.assets   = null;
    this.screens  = null;
    this.messages = null;
  }

  /**
   * Called before {@link ScreenBase#create()}. You can add assets to load here. If there are assets to load it shows loading screen
   */
  public abstract void preload();
  /**
   * Called when screen is showed
   */
  public abstract void create();

  /** Called when the screen should render itself.
   * @param delta The time in seconds since the last render. */
  public abstract void render (float delta);

  /** Called after screen show or game window resize */
  public abstract void resize (int width, int height);

  /**
   * Called when screen after screen is hiden
   */
  public abstract void unload();
}