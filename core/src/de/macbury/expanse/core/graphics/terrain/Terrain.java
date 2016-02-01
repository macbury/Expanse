package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.TerrainRenderableComponent;
import de.macbury.expanse.core.graphics.Lod;

/**
 * This class wraps all stuff for terrain manipulation
 */
public class Terrain implements Disposable {
  private TerrainData terrainData;
  private TerrainAssembler terrainAssembler;

  public Terrain(Blueprint blueprint) {
    this.terrainData      = new TerrainData(blueprint.heightMap, blueprint.maxElevation);
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
      positionComponent.dimension.set(TerrainAssembler.TILE_SIZE, terrainData.getMaxElevation(), TerrainAssembler.TILE_SIZE);

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
    return terrainData.getCenter();
  }

  public BoundingBox getBoundingBox() {
    return terrainData.getBoundingBox();
  }

  public static class Blueprint {
    public int maxElevation;
    public Pixmap heightMap;
  }
}
