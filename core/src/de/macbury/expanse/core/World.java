package de.macbury.expanse.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.Expanse;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.graphics.terrain.TerrainData;
import de.macbury.expanse.core.octree.LevelOctree;

/**
 * This class describes all game world
 */
public class World implements Disposable {
  private static final String TAG = "World";
  public EntityManager entities;
  public LevelOctree<PositionComponent> octree;
  public RTSCameraController rtsCameraController;
  private GameCamera camera;
  public Terrain terrain;

  public World(Terrain terrain, Expanse game) {
    this.camera               = new GameCamera();
    this.terrain              = terrain;
    this.rtsCameraController  = new RTSCameraController(game.input);
    this.octree               = new LevelOctree<PositionComponent>();
    octree.setBounds(terrain.getBoundingBox(new BoundingBox()));

    this.entities             = new EntityManager(game, camera, octree, terrain);
    terrain.addToEntityManager(entities);

    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(game.hud.getOverlay());
    rtsCameraController.setListener(terrain);
    rtsCameraController.setCenter(terrain.getCenter());

    Gdx.app.log(TAG, "Initialized");
  }

  public void render(float delta) {
    rtsCameraController.update(delta);
    entities.update(delta);
  }

  @Override
  public void dispose() {
    Gdx.app.log(TAG, "Disposing");
    terrain.dispose();
    octree.dispose();
    rtsCameraController.dispose();
    entities.dispose();
    terrain = null;
    camera  = null;
    rtsCameraController = null;
  }

  public void resize(int width, int height) {

  }

  public static class Blueprint {
    public TerrainData.Blueprint terrain;
    public int maxElevation;
    public long seed;
    public int width;
    public int height;
  }
}
