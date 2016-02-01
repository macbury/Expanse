package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.graphics.DebugShape;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.graphics.terrain.TerrainAssembler;
import de.macbury.expanse.core.graphics.terrain.TerrainData;
import de.macbury.expanse.core.octree.WorldOctree;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.ui.Hud;

/**
 * Created on 29.01.16.
 */
public class TerrainEntitiesTest extends ScreenBase {
  private GameCamera camera;
  private Hud hud;
  private RTSCameraController rtsCameraController;
  private WorldOctree octree;
  private EntityManager entities;
  private FPSLogger fpsLogger;
  private ShapeRenderer shapeRenderer;
  private Terrain terrain;

  @Override
  public void preload() {
    assets.load("terrain:playground.json", Terrain.class);
  }

  @Override
  public void create() {
    this.terrain              = assets.get("terrain:playground.json");
    this.shapeRenderer        = new ShapeRenderer();
    this.camera               = new GameCamera();
    this.hud                  = new Hud(input, assets);
    this.rtsCameraController  = new RTSCameraController(input);

    camera.far = 300;
    camera.near = 1;

    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(hud.getOverlay());

    this.octree           = new WorldOctree();

    this.entities         = new EntityManager(camera, messages, octree);
    terrain.addToEntityManager(entities);
    this.fpsLogger        = new FPSLogger();


    rtsCameraController.setCenter(terrain.getCenter());
    octree.setBounds(terrain.getBoundingBox());

    Button button = new Button(hud.getSkin());
    button.setPosition(20, 20);
    button.setSize(100, 100);
    hud.addActor(button);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1,1,1,1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    rtsCameraController.update(delta);
    entities.update(delta);
    fpsLogger.log();
/*
    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
      shapeRenderer.setColor(Color.WHITE);
      //shapeRenderer.box(1,1,1,1,1,1);
      DebugShape.octree(shapeRenderer, octree);

      shapeRenderer.setColor(Color.FOREST);

      for (Entity tileEntity : terrainAssembler.getEntities()) {
        DebugShape.draw(shapeRenderer, Components.Position.get(tileEntity).boundingBox);
      }
    } shapeRenderer.end();*/

    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
      camera.saveDebugFrustrum();
    }

    hud.act(delta);
    hud.draw();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void unload() {
    assets.unload("terrain:playground.json");
  }

  @Override
  public void dispose() {

  }
}
