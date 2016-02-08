package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * This class helps calculate terrain elevation for passed cordinates
 */
class ElevationHelper implements Disposable {
  private static final String TAG = "ElevationHelper";
  private Vector2 tempBaryCentric = new Vector2();
  private TerrainData terrainData;

  private Vector3 topLeftVertex     = new Vector3(0,0,0);
  private Vector3 topRightVertex    = new Vector3(1,0,0);
  private Vector3 bottomLeftVertex  = new Vector3(0,0,1);
  private Vector3 bottomRightVertex = new Vector3(1,0,1);

  public ElevationHelper(TerrainData terrainData){
    this.terrainData = terrainData;
  }

  /**
   * Returns elevation for this world coordinates
   * @param x
   * @param z
   * @return
   */
  public float get(float x, float z) {
    int terrainX = MathUtils.floor(x / TerrainAssembler.TRIANGLE_SIZE);
    int terrainZ = MathUtils.floor(z / TerrainAssembler.TRIANGLE_SIZE);

    tempBaryCentric.set(
      (x % TerrainAssembler.TRIANGLE_SIZE) / TerrainAssembler.TRIANGLE_SIZE,
      (z % TerrainAssembler.TRIANGLE_SIZE) / TerrainAssembler.TRIANGLE_SIZE
    );

    float elevation = 0.0f;
    //TODO fix triangles for current implementation
    if (tempBaryCentric.x <= (1-tempBaryCentric.y)) {
      // We are in top left triangle
      topLeftVertex.y    = terrainData.getElevation(terrainX, terrainZ);
      topRightVertex.y   = terrainData.getElevation(terrainX+1, terrainZ);
      bottomLeftVertex.y = terrainData.getElevation(terrainX, terrainZ+1);
      elevation          = barryCentric(
        topLeftVertex,
        topRightVertex,
        bottomLeftVertex,
        tempBaryCentric
      );
    } else {
      // We are in bottom right triangle
      topRightVertex.y    = terrainData.getElevation(terrainX+1, terrainZ);
      bottomRightVertex.y = terrainData.getElevation(terrainX+1, terrainZ+1);
      bottomLeftVertex.y  = terrainData.getElevation(terrainX, terrainZ+1);

      elevation          = barryCentric(
        topRightVertex,
        bottomRightVertex,
        bottomLeftVertex,
        tempBaryCentric
      );
    }

    //Gdx.app.log(TAG, "Elevation: " + elevation + " for " + tempBaryCentric.toString());

    return elevation;
  }

  private float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
    float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
    float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
    float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
    float l3 = 1.0f - l1 - l2;
    return l1 * p1.y + l2 * p2.y + l3 * p3.y;
  }

  @Override
  public void dispose() {
    terrainData = null;
  }
}
