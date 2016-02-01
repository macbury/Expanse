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

/**
 * Loads terrain and builds its geometry!
 */
public class TerrainLoader extends AsynchronousAssetLoader<Terrain, TerrainLoader.TerrainParameter> {
  private Terrain.Blueprint blueprint;
  private AssetDescriptor<Pixmap> heightmapPixmapAssetDescriptor;

  public TerrainLoader(FileHandleResolver resolver) {
    super(resolver);
  }

  @Override
  public void loadAsync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {

  }

  @Override
  public Terrain loadSync(AssetManager manager, String fileName, FileHandle file, TerrainParameter parameter) {
    blueprint.heightMap = manager.get(heightmapPixmapAssetDescriptor);
    return new Terrain(blueprint);
  }

  @Override
  public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TerrainParameter parameter) {
    Json json                   = new Json();
    this.blueprint              = json.fromJson(Terrain.Blueprint.class, file);
    this.heightmapPixmapAssetDescriptor  = new AssetDescriptor<Pixmap>(file.parent().child(file.nameWithoutExtension()+".png"), Pixmap.class, null);

    Array<AssetDescriptor> deps = new Array();
    deps.add(heightmapPixmapAssetDescriptor);
    return deps;
  }

  static public class TerrainParameter extends AssetLoaderParameters<Terrain> {
  }

}
