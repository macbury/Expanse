package de.macbury.expanse;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.screens.ScreenManager;
import de.macbury.expanse.core.scripts.RobotScriptContextFactory;
import de.macbury.expanse.test.TestScreen;

public class Expanse extends ApplicationAdapter {
  public final static String VERSION = "0.0.3";
  private static final String TAG    = "Expanse";

  /**
   * Message comunication class used by EntityManager
   */
  public MessageDispatcher messages;
  /**
   *  Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music and so on.
   */
  public Assets assets;
  /**
   * Manage in game screens
   */
  public ScreenManager screens;

  @Override
  public void create () {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    Gdx.app.log(TAG, "Init...");
    RobotScriptContextFactory.init();

    this.assets     = new Assets();
    this.messages   = new MessageDispatcher();
    this.screens    = new ScreenManager(this);

    screens.set(new TestScreen());
  }

  @Override
  public void render () {
    GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());
    messages.update();
    screens.update();
  }

  @Override
  public void dispose() {
    super.dispose();
    assets.dispose();
    messages.clear();
    messages.clearListeners();
  }
}
