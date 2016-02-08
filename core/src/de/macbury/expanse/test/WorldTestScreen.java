package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.blueprint.EntityBlueprint;
import de.macbury.expanse.core.graphics.DebugShape;
import de.macbury.expanse.core.graphics.framebuffer.Fbo;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.ui.SelectedUnitCursor;

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
    assets.load("entity:rock.json", EntityBlueprint.class);
    assets.load("entity:tree.json", EntityBlueprint.class);
  }

  @Override
  public void create() {
    this.world = assets.get(worldToLoad);
    Group inGameUi = new Group();
    hud.addActor(inGameUi);

    SelectedUnitCursor selectedUnitCursor = new SelectedUnitCursor(messages, hud.getSkin().getPatch("selected_unit"), world.camera);
    inGameUi.addActor(selectedUnitCursor);

    EntityBlueprint rockBlueprint = assets.get("entity:rock.json");

    for (int i = 0; i < 1000; i++) {
      Entity rockEntity = rockBlueprint.create(world.entities, this.messages);

      Vector2 center = new Vector2((float)(Math.random() * world.terrain.getWidth()), (float)(Math.random() * world.terrain.getHeight()));
      Components.Position.get(rockEntity).set(
        center.x,
        0,
        center.y
      );

      Components.Position.get(rockEntity).rotationDeg = (float)Math.random() * 360;
      world.entities.addEntity(rockEntity);
    }

    EntityBlueprint treeBlueprint = assets.get("entity:tree.json");

    for (int i = 0; i < 1000; i++) {
      Entity treeEntity = treeBlueprint.create(world.entities, this.messages);

      Vector2 center = new Vector2((float)(Math.random() * world.terrain.getWidth()), (float)(Math.random() * world.terrain.getHeight()));
      Components.Position.get(treeEntity).set(
        center.x,
        0,
        center.y
      );

      Components.Position.get(treeEntity).rotationDeg = (float)Math.random() * 360;
      world.entities.addEntity(treeEntity);
    }
  }

  @Override
  public void render(float delta) {
    world.render(delta);
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
      Gdx.app.exit();
    }
/*
    fb.begin(Fbo.FinalResult); {
      shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
        shapeRenderer.setProjectionMatrix(world.camera.combined);
        shapeRenderer.setColor(Color.BLACK);
        DebugShape.octree(shapeRenderer, world.octree.getStaticOctree());

        shapeRenderer.setColor(Color.WHITE);
        DebugShape.octreeObjects(shapeRenderer, world.octree.getStaticOctree());
      } shapeRenderer.end();

    } fb.end();
*/

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
    assets.unload("entity:tree.json");
    assets.unload("entity:rock.json");
    assets.unload(worldToLoad);
  }
}
