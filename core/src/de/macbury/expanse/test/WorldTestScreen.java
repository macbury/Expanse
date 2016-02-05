package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.screens.ScreenBase;

/**
 * Created on 05.02.16.
 */
public class WorldTestScreen extends ScreenBase {
  private final String worldToLoad;
  private World world;

  public WorldTestScreen(String worldToLoad) {
    super();
    this.worldToLoad = worldToLoad;
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
    if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
      screens.set(new WorldTestScreen("world:test1.json"));
    }

    if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
      screens.set(new WorldTestScreen("world:test2.json"));
    }

    if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
      screens.set(new WorldTestScreen("world:playground.json"));
    }
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
