package de.macbury.expanse.core.entities.blueprint;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.assets.Assets;

/**
 * This class is used as model for blueprints
 */
public abstract class ComponentBlueprint implements Disposable {
  /**
   * Pass all dependencies that are needed to be loaded by {@link de.macbury.expanse.core.assets.Assets}
   * @return
   */
  public abstract void prepareDependencies(Array<AssetDescriptor> dependencies);

  /**
   * Assign all dependencies from assets
   * @param assets
   */
  public abstract void assignDependencies(Assets assets);
}
