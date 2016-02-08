package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.TerrainRenderableComponent;
import de.macbury.expanse.core.graphics.Lod;

/**
 * This class uses {@link TerrainData} to create {@link com.badlogic.gdx.graphics.g3d.Renderable} for each terrain tile. It manages and disposes all meshes
 */
public class TerrainAssembler implements Disposable {
  private final static Pool<MeshPartBuilder.VertexInfo> VertexInfoPool = new Pool<MeshPartBuilder.VertexInfo>() {
    @Override
    protected MeshPartBuilder.VertexInfo newObject() {
      return new MeshPartBuilder.VertexInfo();
    }
  };
  private MeshPartBuilder.VertexInfo topLeftVertexInfo;
  private MeshPartBuilder.VertexInfo topRightVertexInfo;
  private MeshPartBuilder.VertexInfo bottomLeftVertexInfo;
  private MeshPartBuilder.VertexInfo bottomRightVertexInfo;
  private Material material;
  private MeshBuilder meshBuilder;
  private TerrainData terrainData;
  private ObjectMap<Lod, Array<Renderable>> lodRenderables;
  public final static int TILE_SIZE = 32;
  public final static float TRIANGLE_SIZE = 2;
  private int primitiveType;
  private BoundingBox tempBoundingBox = new BoundingBox();
  private Array<TerrainRenderableComponent> terrainRenderableComponents;

  public TerrainAssembler(TerrainData terrainData, int primitiveType) {
    this.meshBuilder = new MeshBuilder();
    this.terrainData = terrainData;
    this.lodRenderables = new ObjectMap<Lod, Array<Renderable>>();

    for (Lod lod : Lod.values()) {
      lodRenderables.put(lod, new Array<Renderable>());
    }

    this.material    = new Material();
    this.primitiveType = primitiveType;

    terrainRenderableComponents = new Array<TerrainRenderableComponent>();

    this.bottomRightVertexInfo = new MeshPartBuilder.VertexInfo();
    this.bottomLeftVertexInfo  = new MeshPartBuilder.VertexInfo();
    this.topRightVertexInfo    = new MeshPartBuilder.VertexInfo();
    this.topLeftVertexInfo     = new MeshPartBuilder.VertexInfo();
    assemble();
  }

  /**
   * Splits terrain data in tiles
   */
  private void assemble() {
    int tileCountX = terrainData.getWidth() / TILE_SIZE;
    int tileCountY = terrainData.getHeight() / TILE_SIZE;

    for (int tileX = 0; tileX < tileCountX; tileX++) {
      for (int tileY = 0; tileY < tileCountY; tileY++) {
        TerrainRenderableComponent terrainRenderableComponent = new TerrainRenderableComponent();

        for (Lod lod : Lod.values()) {
          terrainRenderableComponent.lodTiles.put(
            lod,
            build(tileX, tileY, lod)
          );
        }


        terrainRenderableComponents.add(terrainRenderableComponent);
      }
    }
  }

  private void calcNormal(MeshPartBuilder.VertexInfo p1, MeshPartBuilder.VertexInfo p2, MeshPartBuilder.VertexInfo p3) {
    // u = p3 - p1
    float ux = p3.position.x - p1.position.x;
    float uy = p3.position.y - p1.position.y;
    float uz = p3.position.z - p1.position.z;

    // v = p2 - p1
    float vx = p2.position.x - p1.position.x;
    float vy = p2.position.y - p1.position.y;
    float vz = p2.position.z - p1.position.z;

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
    p1.setNor(nx, ny, nz);
    p2.setNor(nx, ny, nz);
    p3.setNor(nx, ny, nz);
  }

  /**
   * Create renderable for current tile position
   * @param tileX
   * @param tileY
   */
  private Renderable build(int tileX, int tileY, Lod lod) {
    Renderable tileRenderable = new Renderable();
    tileRenderable.material   = material;

    int startX = tileX * TILE_SIZE;
    int startY = tileY * TILE_SIZE;
    int endX   = startX + TILE_SIZE;
    int endY   = startY + TILE_SIZE;

    meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked); {
      meshBuilder.part("tile"+tileX+"x"+tileY, primitiveType, tileRenderable.meshPart);

      int doubleLod = lod.resolution * 2;
      for (int x = startX; x < endX; x+=lod.resolution) {
        for (int z = startY; z < endY; z+=lod.resolution) {
          buildQuadVertex(x, z, lod);
          if (z % doubleLod == 0) {
            if (x % doubleLod == 0) {
              buildQuadAIndicies(x,z);
            } else {
              buildQuadBIndicies(x,z);
            }
          } else {
            if (x % doubleLod == 0) {
              buildQuadBIndicies(x,z);
            } else {
              buildQuadAIndicies(x,z);
            }
          }
        }
      }

    } meshBuilder.end();

    lodRenderables.get(lod).add(tileRenderable);
    return tileRenderable;
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
  private void buildQuadAIndicies(int x, int z) {
    Color colorA = terrainData.getColor(x, z);
    topLeftVertexInfo.setCol(colorA);
    topRightVertexInfo.setCol(colorA);
    bottomLeftVertexInfo.setCol(colorA);

    calcNormal(
      bottomLeftVertexInfo,
      topRightVertexInfo,
      topLeftVertexInfo
    );

    meshBuilder.triangle(
      bottomLeftVertexInfo,
      topRightVertexInfo,
      topLeftVertexInfo
    );

    Color colorB = terrainData.getColor(x+1, z+1);

    calcNormal(
      bottomLeftVertexInfo,
      bottomRightVertexInfo,
      topRightVertexInfo
    );

    topRightVertexInfo.setCol(colorB);
    bottomRightVertexInfo.setCol(colorB);
    bottomLeftVertexInfo.setCol(colorB);

    meshBuilder.triangle(
      bottomLeftVertexInfo,
      bottomRightVertexInfo,
      topRightVertexInfo
    );
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

  private void buildQuadBIndicies(int x, int z) {
    Color colorA = terrainData.getColor(x+1, z+1);
    topLeftVertexInfo.setCol(colorA);
    bottomLeftVertexInfo.setCol(colorA);
    bottomRightVertexInfo.setCol(colorA);

    calcNormal(
      topLeftVertexInfo,
      bottomLeftVertexInfo,
      bottomRightVertexInfo
    );

    meshBuilder.triangle(
      topLeftVertexInfo,
      bottomLeftVertexInfo,
      bottomRightVertexInfo
    );

    Color colorB = terrainData.getColor(x, z);

    calcNormal(
      topLeftVertexInfo,
      bottomRightVertexInfo,
      topRightVertexInfo
    );

    topLeftVertexInfo.setCol(colorB);
    bottomRightVertexInfo.setCol(colorB);
    topRightVertexInfo.setCol(colorB);

    meshBuilder.triangle(
      topLeftVertexInfo,
      bottomRightVertexInfo,
      topRightVertexInfo
    );
  }


  private void buildQuadVertex(int x, int z, Lod lod) {
    topLeftVertexInfo.setPos(
      x * TRIANGLE_SIZE,
      terrainData.getElevation(x, z),
      z * TRIANGLE_SIZE
    );

    topRightVertexInfo.setPos(
      (x + lod.resolution) * TRIANGLE_SIZE,
      terrainData.getElevation(x + lod.resolution, z),
      z * TRIANGLE_SIZE
    );

    bottomLeftVertexInfo.setPos(
      x * TRIANGLE_SIZE,
      terrainData.getElevation(x, z + lod.resolution),
      (z + lod.resolution) * TRIANGLE_SIZE
    );

    bottomRightVertexInfo.setPos(
      (x + lod.resolution) * TRIANGLE_SIZE,
      terrainData.getElevation(x + lod.resolution, z + lod.resolution),
      (z + lod.resolution) * TRIANGLE_SIZE
    );
  }

  @Override
  public void dispose() {
    for (Lod lod : Lod.values()) {
      for (Renderable renderable : lodRenderables.get(lod)) {
        renderable.environment = null;
        renderable.material    = null;
        renderable.shader      = null;
        renderable.userData    = null;
        renderable.bones       = null;
        renderable.meshPart.mesh.dispose();
      }
    }

    lodRenderables.clear();
    terrainData = null;
    meshBuilder.clear();
    meshBuilder = null;
    material    = null;
    terrainRenderableComponents.clear();
  }


  public Array<TerrainRenderableComponent> getComponents() {
    return terrainRenderableComponents;
  }
}
