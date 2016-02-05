package de.macbury.expanse.core.assets.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import de.macbury.expanse.Expanse;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.graphics.terrain.TerrainData;

/**
 * Loads world
 */
public class WorldLoader extends AsynchronousAssetLoader<World, WorldLoader.WorldParameter> {
  private final Expanse game;
  private World.Blueprint blueprint;
  private TerrainData terrainData;

  public WorldLoader(FileHandleResolver resolver, Expanse game) {
    super(resolver);
    this.game = game;
  }

  @Override
  public void loadAsync(AssetManager manager, String fileName, FileHandle file, WorldParameter parameter) {
    this.terrainData = new TerrainData(blueprint.terrain);
  }

  @Override
  public World loadSync(AssetManager manager, String fileName, FileHandle file, WorldParameter parameter) {
    Terrain terrain = new Terrain(terrainData);
    terrainData = null;
    System.gc();
    return new World(terrain, game);
  }

  @Override
  public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, WorldParameter parameter) {
    Json json                       = new Json();
    this.blueprint                  = json.fromJson(World.Blueprint.class, file);
    return null;
  }

  static public class WorldParameter extends AssetLoaderParameters<World> {

  }

}
