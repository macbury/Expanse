package de.macbury.expanse.core.assets;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Logger;
import de.macbury.expanse.Expanse;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.assets.loader.EntityBlueprintLoader;
import de.macbury.expanse.core.assets.loader.TerrainLoader;
import de.macbury.expanse.core.assets.loader.WorldLoader;
import de.macbury.expanse.core.entities.blueprint.EntityBlueprint;
import de.macbury.expanse.core.graphics.terrain.Terrain;

/**
 * This class wraps {@link AssetManager} and extends it with proper features required by game and automaticaly map asssets to path
 */
public class Assets extends AssetManager {

  public Assets(FileHandleResolver resolver, Expanse game) {
    super(resolver);
    setLogger(new Logger("AssetManager", Application.LOG_INFO));
    setLoader(Terrain.class, new TerrainLoader(resolver));
    setLoader(World.class, new WorldLoader(resolver, game));
    setLoader(EntityBlueprint.class, new EntityBlueprintLoader(resolver));
  }
}
