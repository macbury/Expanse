package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
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
  private TerrainData terrainData;
  private TerrainAssembler terrainAssembler;
  private ShapeRenderer shapeRenderer;

  @Override
  public void preload() {

  }

  @Override
  public void create() {
    this.shapeRenderer        = new ShapeRenderer();
    this.camera               = new GameCamera();
    this.hud                  = new Hud(input, assets);
    this.rtsCameraController  = new RTSCameraController(input);

    camera.far = 300;
    camera.near = 1;

    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(hud.getOverlay());

    this.octree      = new WorldOctree();

    this.entities    = new EntityManager(camera, messages, octree);
    this.fpsLogger   = new FPSLogger();

    this.terrainData      = new TerrainData();
    this.terrainAssembler = new TerrainAssembler(terrainData, GL20.GL_TRIANGLES);
    terrainAssembler.addTo(entities);
    rtsCameraController.setCenter(terrainData.getCenter());
    octree.setBounds(terrainData.getBoundingBox());

    Button button = new Button(hud.getSkin());
    button.setPosition(20, 20);
    button.setSize(100, 100);
    hud.addActor(button);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    rtsCameraController.update(delta);
    entities.update(delta);
    fpsLogger.log();

    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
      shapeRenderer.setColor(Color.WHITE);
      shapeRenderer.box(1,1,1,1,1,1);
      DebugShape.octree(shapeRenderer, octree);

      shapeRenderer.setColor(Color.FOREST);

      for (Entity tileEntity : terrainAssembler.getEntities()) {
        DebugShape.draw(shapeRenderer, Components.Body.get(tileEntity));
      }
    } shapeRenderer.end();

    hud.act(delta);
    hud.draw();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void unload() {

  }

  @Override
  public void dispose() {

  }
}
