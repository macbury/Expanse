package de.macbury.expanse.core.assets.loader;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.graphics.terrain.TerrainData;

/**
 * Loads terrain and builds its geometry!
 */
public class TerrainLoader extends AsynchronousAssetLoader<Terrain, TerrainLoader.TerrainParameter> {

  private TerrainData terrainData;

  public TerrainLoader(FileHandleResolver resolver) {
    super(resolver);
  }

  @Override
  public void loadAsync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
    Json json                       = new Json();
    TerrainData.Blueprint blueprint = json.fromJson(TerrainData.Blueprint.class, file);
    this.terrainData                = new TerrainData(blueprint);
  }

  @Override
  public Terrain loadSync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
    TerrainData currentTerrainData = terrainData;
    terrainData = null;
    return new Terrain(currentTerrainData);
  }

  @Override
  public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
    return null;
  }

  static public class TerrainParameter extends AssetLoaderParameters<Terrain> {
  }

}
