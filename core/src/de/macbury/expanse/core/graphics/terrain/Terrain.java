package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.TerrainRenderableComponent;
import de.macbury.expanse.core.graphics.Lod;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.camera.RTSCameraListener;

/**
 * This class wraps all stuff for terrain manipulation
 */
public class Terrain implements Disposable, RTSCameraListener {
  private Vector3 tempVecA = new Vector3();
  private Vector3 tempVecB = new Vector3();
  private TerrainData terrainData;
  private TerrainAssembler terrainAssembler;

  public Terrain(TerrainData terrainData) {
    this.terrainData      = terrainData;
    this.terrainAssembler = new TerrainAssembler(terrainData, GL20.GL_TRIANGLES);
  }

  /**
   * Creates all tile entities
   */
  public void addToEntityManager(EntityManager entityManager) {
    BoundingBox tempBoundingBox = new BoundingBox();
    for (TerrainRenderableComponent terrainRenderableComponent : terrainAssembler.getComponents()) {
      terrainRenderableComponent.lodTiles.get(Lod.High).meshPart.mesh.calculateBoundingBox(tempBoundingBox);

      PositionComponent positionComponent = entityManager.createComponent(PositionComponent.class);
      tempBoundingBox.getCenter(positionComponent);
      positionComponent.dimension.set(TerrainAssembler.TILE_SIZE * TerrainAssembler.TRIANGLE_SIZE, terrainData.getMaxElevation(), TerrainAssembler.TILE_SIZE * TerrainAssembler.TRIANGLE_SIZE);

      Entity tileEntity = entityManager.createEntity();
      tileEntity.add(terrainRenderableComponent);
      tileEntity.add(positionComponent);

      entityManager.addEntity(tileEntity);
    }
  }


  @Override
  public void dispose() {
    terrainData.dispose();
    terrainAssembler.dispose();
  }

  public Vector2 getCenter() {
    return terrainData.getCenter().scl(TerrainAssembler.TRIANGLE_SIZE);
  }

  /**
   * Calculates bounding box and returns it
   * @return
   * @param out
   */
  public BoundingBox getBoundingBox(BoundingBox out) {
    return out.set(
      tempVecA.set(0, -1, 0),
      tempVecB.set(terrainData.getWidth() * TerrainAssembler.TRIANGLE_SIZE, terrainData.getMaxElevation() + 5, terrainData.getHeight() * TerrainAssembler.TRIANGLE_SIZE)
    );
  }

  /**
   * Returns elevation for this world cordinates
   * @param x
   * @param z
   * @return
   */
  public float getElevation(float x, float z) {
    int tx =  MathUtils.floor(x / TerrainAssembler.TRIANGLE_SIZE);
    int tz =  MathUtils.floor(z / TerrainAssembler.TRIANGLE_SIZE);
    Gdx.app.log("DATA", "x=" + (x / TerrainAssembler.TRIANGLE_SIZE) + " z=" + (z / TerrainAssembler.TRIANGLE_SIZE));
    return terrainData.getElevation(tx, tz);
  }

  @Override
  public BoundingBox getCameraBounds(BoundingBox out) {
    return getBoundingBox(out);
  }

  @Override
  public float getCameraElevation(RTSCameraController cameraController, Camera camera) {
    return getElevation(camera.position.x, camera.position.z);
  }
}
