package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import de.macbury.expanse.core.entities.blueprint.ComponentBlueprint;
import de.macbury.expanse.core.entities.blueprint.EntityBlueprint;
import de.macbury.expanse.core.screens.ScreenBase;

/**
 * Created on 03.02.16.
 */
public class EntitiesBlueprintsScreen extends ScreenBase {
  private static final String TAG = "EntitiesBlueprintsScreen";

  @Override
  public void preload() {
    assets.load("entity:rock.json", EntityBlueprint.class);
  }

  @Override
  public void create() {
    Gdx.app.log(TAG, assets.get("entity:rock.json", EntityBlueprint.class).toString());
  }

  @Override
  public void render(float delta) {

  }

  @Override
  public void resize(int width, int height) {

  }


  @Override
  public void dispose() {
    assets.unload("entity:rock.json");
  }
}
