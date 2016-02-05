package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.graphics.DebugShape;
import de.macbury.expanse.core.graphics.framebuffer.Fbo;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.screens.ScreenBase;

/**
 * Created on 05.02.16.
 */
public class WorldTestScreen extends ScreenBase {
  private final String worldToLoad;
  private final ShapeRenderer shapeRenderer;
  private World world;

  public WorldTestScreen(String worldToLoad) {
    super();
    this.worldToLoad = worldToLoad;
    this.shapeRenderer = new ShapeRenderer();
  }

  @Override
  public void preload() {
    assets.load(worldToLoad, World.class);
  }

  @Override
  public void create() {
    this.world = assets.get(worldToLoad);
  }

  @Override
  public void render(float delta) {
    world.render(delta);

    fb.begin(Fbo.FinalResult); {
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
        shapeRenderer.setProjectionMatrix(world.camera.combined);
        shapeRenderer.setColor(Color.BLACK);
        DebugShape.octree(shapeRenderer, world.octree.getStaticOctree());
      } shapeRenderer.end();
    } fb.end();


    /*if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
      screens.set(new WorldTestScreen("world:test1.json"));
    }

    if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
      screens.set(new WorldTestScreen("world:test2.json"));
    }

    if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
      screens.set(new WorldTestScreen("world:playground.json"));
    }*/
  }

  @Override
  public void resize(int width, int height) {
    world.resize(width, height);
  }

  @Override
  public void dispose() {
    assets.unload(worldToLoad);
  }
}
