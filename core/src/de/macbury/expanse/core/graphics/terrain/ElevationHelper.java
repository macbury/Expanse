package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

/**
 * This class helps calculate terrain elevation for passed cordinates
 */
public class ElevationHelper implements Disposable {
  private static final String TAG = "ElevationHelper";
  private Vector2 tempBaryCentric = new Vector2();
  private TerrainData terrainData;

  private Vector3 topLeftVertex     = new Vector3(0,0,0);
  private Vector3 topRightVertex    = new Vector3(1,0,0);
  private Vector3 bottomLeftVertex  = new Vector3(0,0,1);
  private Vector3 bottomRightVertex = new Vector3(1,0,1);
  private Vector3 normal            = new Vector3();
  private int terrainX;
  private int terrainZ;
  private float x;
  private float z;

  public ElevationHelper(TerrainData terrainData){
    this.terrainData = terrainData;
  }

  /**
   * Triangle first
   * (0,0) ----- (1,0)
   *   | \        |
   *   |  \       |
   *   |   \      |
   *   |    \     |
   *   |     \    |
   *   |      \   |
   *   |       \  |
   *   |        \ |
   *   |         \|
   *  (0,1)----- (1,1)
   *        Triangle last
   * @param x
   * @param z
   */
  private float getElevationForTriangleB() {
    float elevation = 0.0f;
    if (tempBaryCentric.x <= (1-tempBaryCentric.y)) {
      // We are in bottom left triangle
      elevation          = barryCentric(
        topLeftVertex,
        bottomLeftVertex,
        bottomRightVertex,
        tempBaryCentric
      );
    } else {
      // We are in top right triangle
      elevation          = barryCentric(
        topLeftVertex,
        bottomRightVertex,
        topRightVertex,
        tempBaryCentric
      );
    }
    return elevation;
  }

  /**
   * Triangle first
   * (0,0) ----- (1,0)
   *   |          / |
   *   |         /  |
   *   |        /   |
   *   |   A   /    |
   *   |      /     |
   *   |     /      |
   *   |    /    B  |
   *   |   /        |
   *   |  /         |
   *  (0,1)-------(1,1)
   *        Triangle last
   * @param x
   * @param z
   */
  private float getElevationForTriangleA() {
    float elevation = 0.0f;
    if (tempBaryCentric.x <= (1-tempBaryCentric.y)) {
      // We are in top left triangle
      elevation          = barryCentric(
        topLeftVertex,
        topRightVertex,
        bottomLeftVertex,
        tempBaryCentric
      );

    } else {
      // We are in bottom right triangle
      elevation          = barryCentric(
        topRightVertex,
        bottomRightVertex,
        bottomLeftVertex,
        tempBaryCentric
      );
    }
    return elevation;
  }

  /**
   * Move cursor to new position and calculate elavation
   * @param x
   * @param z
   */
  public void set(float x, float z) {
    this.x        = x;
    this.z        = z;
    this.terrainX = (int) (MathUtils.floor(x) / TerrainAssembler.TRIANGLE_SIZE);
    this.terrainZ = (int) (MathUtils.floor(z) / TerrainAssembler.TRIANGLE_SIZE);

    tempBaryCentric.set(
      (x % TerrainAssembler.TRIANGLE_SIZE) / TerrainAssembler.TRIANGLE_SIZE,
      (z % TerrainAssembler.TRIANGLE_SIZE) / TerrainAssembler.TRIANGLE_SIZE
    );

    topLeftVertex.y      = terrainData.getElevation(terrainX, terrainZ);
    topRightVertex.y     = terrainData.getElevation(terrainX+1, terrainZ);
    bottomLeftVertex.y   = terrainData.getElevation(terrainX, terrainZ+1);
    bottomRightVertex.y  = terrainData.getElevation(terrainX+1, terrainZ+1);
  }

  /**
   * Returns elevation for this world coordinates
   * @return
   */
  public float get() {
    if (z % 2 == 0) {
      if (x % 2 == 0) {
        return getElevationForTriangleA();
      } else {
        return getElevationForTriangleB();
      }
    } else {
      if (x % 2 == 0) {
        return getElevationForTriangleB();
      } else {
        return getElevationForTriangleA();
      }
    }
  }

  /**
   * Returns normal for current triangle
   * @return
   */
  public Vector3 getNormal(Vector3 normalOut) {
    if (z % 2 == 0) {
      if (x % 2 == 0) {
        calcNormal(
          topLeftVertex,
          topRightVertex,
          bottomLeftVertex
        );
      } else {
        calcNormal(
          topLeftVertex,
          bottomLeftVertex,
          bottomRightVertex
        );
      }
    } else {
      if (x % 2 == 0) {
        calcNormal(
          topLeftVertex,
          bottomLeftVertex,
          bottomRightVertex
        );
      } else {
        calcNormal(
          topLeftVertex,
          topRightVertex,
          bottomLeftVertex
        );
      }
    }
    return normalOut.set(normal);
  }

  private void calcNormal(Vector3 p1, Vector3 p2, Vector3 p3) {
    // u = p3 - p1
    float ux = p3.x - p1.x;
    float uy = p3.y - p1.y;
    float uz = p3.z - p1.z;

    // v = p2 - p1
    float vx = p2.x - p1.x;
    float vy = p2.y - p1.y;
    float vz = p2.z - p1.z;

    // n = cross(v, u)
    float nx = ((vy * uz) - (vz * uy));
    float ny = ((vz * ux) - (vx * uz));
    float nz = ((vx * uy) - (vy * ux));

    // // normalize(n)
    float num2 = ((nx * nx) + (ny * ny)) + (nz * nz);
    float num = 1f / (float) Math.sqrt(num2);
    nx *= num;
    ny *= num;
    nz *= num;

    normal.set(nx, ny, nz);
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

  public Vector3 getRotation() {
    Vector3 vec = new Vector3();
    //float dot = vec.set(Vector3.Y).dot(elevation.getNormal(new Vector3()));
    return null;
  }
}
