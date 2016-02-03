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
    /*String source = Gdx.files.internal("entities/rock.entity").readString();
    JsonReader jsonReader = new JsonReader();
    Json json             = new Json();
    JsonValue root = jsonReader.parse(Gdx.files.internal("entities/rock.entity"));

    JsonValue simpleComponent = root.child();

    for (JsonValue jsonBlueprint : root) {
      String componentSimpleNamePart = jsonBlueprint.name();
      try {
        Class<ComponentBlueprint> blueprintKlassToLoad = (Class<ComponentBlueprint>)Class.forName("de.macbury.expanse.core.entities.components." + componentSimpleNamePart + "Component$Blueprint");
        ComponentBlueprint blueprint = (ComponentBlueprint) json.readValue(blueprintKlassToLoad, jsonBlueprint);

      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

    }*/

    Gdx.app.log(TAG, assets.get("entity:rock.json", EntityBlueprint.class).toString());
  }

  @Override
  public void render(float delta) {

  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void unload() {
    assets.unload("entity:rock.json");
  }

  @Override
  public void dispose() {

  }
}
