package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.scripts.ScriptRunner;
import de.macbury.expanse.core.scripts.modules.Console;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created on 18.01.16.
 */
public class TestScreen extends ScreenBase {
  private static final String TAG = "TestScreen";
  private Texture texture;
  private SpriteBatch spriteBatch;
  private OrthographicCamera camera;
  private ScriptRunner scriptRunner;
  private FPSLogger fpsLogger;
  private int frameNo;
  private EntityManager entities;

  @Override
  public void preload() {
    this.assets.load("textures:bgspeedship.png", Texture.class);
  }

  @Override
  public void create() {
    this.camera      = new OrthographicCamera();
    this.texture     = this.assets.get("textures:bgspeedship.png", Texture.class);
    this.spriteBatch = new SpriteBatch();
    this.entities    = new EntityManager();
    this.fpsLogger   = new FPSLogger();

    Array<ScriptableObject> globalObjectFunctions = new Array<ScriptableObject>();
    globalObjectFunctions.add(new Console());

    this.scriptRunner = new ScriptRunner(Gdx.files.internal("scripts/for.js").readString(), globalObjectFunctions);
    //long startAt      = System.currentTimeMillis();
    scriptRunner.start();
    //Gdx.app.log(TAG, "Speed of script: " + (System.currentTimeMillis() - startAt) + " miliseconds!");

  }

  @Override
  public void render(float delta) {
    camera.update();
    entities.update(delta);
    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin(); {
      spriteBatch.draw(texture, 60,60);
    } spriteBatch.end();

    fpsLogger.log();

    frameNo += 1;
    scriptRunner.resume("random result");

  }

  @Override
  public void resize(int width, int height) {
    camera.setToOrtho(false, width, height);
  }

  @Override
  public void unload() {
    this.assets.unload("textures:bgspeedship.png");
  }

  @Override
  public void dispose() {
    spriteBatch.dispose();
    scriptRunner.dispose();
    entities.dispose();
  }
}
