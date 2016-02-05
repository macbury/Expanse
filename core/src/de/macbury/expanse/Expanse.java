package de.macbury.expanse;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.assets.EngineFileHandleResolver;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.graphics.framebuffer.FrameBufferManager;
import de.macbury.expanse.core.input.InputManager;
import de.macbury.expanse.core.screens.ScreenManager;
import de.macbury.expanse.core.scripts.RobotScriptContextFactory;
import de.macbury.expanse.core.ui.Hud;
import de.macbury.expanse.test.EntitiesBlueprintsScreen;
import de.macbury.expanse.test.GameTestScreen;
import de.macbury.expanse.test.WorldTestScreen;

public class Expanse extends ApplicationAdapter {
  public final static String VERSION = "0.0.6";
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
  /**
   * Main game ui
   */
  public Hud hud;

  @Override
  public void create () {
    //TODO handle enabling this stuff by flags...
    GLProfiler.enable();
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    Gdx.app.log(TAG, "Init...");
    RobotScriptContextFactory.init();

    this.input      = new InputManager();
    this.assets     = new Assets(new EngineFileHandleResolver(), this);
    this.messages   = new Messages();
    this.screens    = new ScreenManager(this);
    this.fb         = new FrameBufferManager();
    this.hud        = new Hud(input, assets, fb);

    screens.set(new WorldTestScreen("world:playground.json"));

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

    Gdx.gl.glClearColor(1,1,1,1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    hud.act(Gdx.graphics.getDeltaTime());
    hud.draw();
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
