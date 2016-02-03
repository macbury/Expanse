package de.macbury.expanse;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.graphics.framebuffer.FrameBufferManager;
import de.macbury.expanse.core.input.InputManager;
import de.macbury.expanse.core.screens.ScreenManager;
import de.macbury.expanse.core.scripts.RobotScriptContextFactory;
import de.macbury.expanse.test.GameTestScreen;

public class Expanse extends ApplicationAdapter {
  public final static String VERSION = "0.0.5";
  private static final String TAG    = "Expanse";
  /**
   * This class helps with input managment
   */
  public InputManager input;
  /**
   * Message comunication class used by EntityManager
   */
  public Messages messages;
  /**
   *  Loads and stores assets like textures, bitmapfonts, tile maps, sounds, music and so on.
   */
  public Assets assets;
  /**
   * Manage in game screens
   */
  public ScreenManager screens;
  /**
   * Manage framebuffers
   */
  public FrameBufferManager fb;
  @Override
  public void create () {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    Gdx.app.log(TAG, "Init...");
    RobotScriptContextFactory.init();

    this.input      = new InputManager();
    this.assets     = new Assets();
    this.messages   = new Messages();
    this.screens    = new ScreenManager(this);
    this.fb         = new FrameBufferManager();

    screens.set(new GameTestScreen());

    Gdx.input.setInputProcessor(input);
  }

  @Override
  public void resize(int width, int height) {
    fb.resize(width, height, true);
    super.resize(width, height);
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
    fb.dispose();
  }
}
