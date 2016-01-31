package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.BodyComponent;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.TerrainRenderableComponent;

/**
 * This class uses {@link TerrainData} to create {@link com.badlogic.gdx.graphics.g3d.Renderable} for each terrain tile. It manages and disposes all meshes
 */
public class TerrainAssembler implements Disposable, RenderableProvider {
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
  public final Array<Renderable> renderables;
  private final static int TILE_SIZE = 20;
  private int primitiveType;
  private Array<Entity> entities;

  public TerrainAssembler(TerrainData terrainData, int primitiveType) {
    this.meshBuilder = new MeshBuilder();
    this.terrainData = terrainData;
    this.renderables = new Array<Renderable>();
    this.material    = new Material();
    this.primitiveType = primitiveType;
    this.entities      = new Array<Entity>();

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
        build(tileX, tileY);
      }
    }
  }

  private Vector3 tempNormal = new Vector3();
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
  private void build(int tileX, int tileY) {
    Renderable tileRenderable = new Renderable();
    tileRenderable.material   = material;

    int startX = tileX * TILE_SIZE;
    int startY = tileY * TILE_SIZE;
    int endX   = startX + TILE_SIZE;
    int endY   = startY + TILE_SIZE;

    meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked); {
      meshBuilder.part("tile"+tileX+"x"+tileY, primitiveType, tileRenderable.meshPart);
      for (int x = startX; x < endX; x++) {
        for (int z = startY; z < endY; z++) {
          bottomLeftVertexInfo.setPos(
            x,
            terrainData.getElevation(x,z),
            z
          ).setCol(terrainData.getColor(x,z));

          bottomRightVertexInfo.setPos(
            x+1,
            terrainData.getElevation(x+1,z),
            z
          ).setCol(terrainData.getColor(x+1,z));

          topLeftVertexInfo.setPos(
            x,
            terrainData.getElevation(x,z+1),
            z+1
          ).setCol(terrainData.getColor(x,z+1));

          topRightVertexInfo.setPos(
            x + 1,
            terrainData.getElevation(x + 1, z + 1),
            z + 1
          ).setCol(terrainData.getColor(x+1,z+1));

          calcNormal(
            bottomLeftVertexInfo,
            topLeftVertexInfo,
            bottomRightVertexInfo
          );

          meshBuilder.triangle(
            bottomLeftVertexInfo,
            topLeftVertexInfo,
            bottomRightVertexInfo
          );

          calcNormal(
            topLeftVertexInfo,
            topRightVertexInfo,
            bottomRightVertexInfo
          );

          meshBuilder.triangle(
            topLeftVertexInfo,
            topRightVertexInfo,
            bottomRightVertexInfo
          );
        }
      }
    } meshBuilder.end();

    renderables.add(tileRenderable);
  }

  @Override
  public void dispose() {
    for (Renderable renderable : renderables) {
      renderable.environment = null;
      renderable.material    = null;
      renderable.shader      = null;
      renderable.userData    = null;
      renderable.bones       = null;
      renderable.meshPart.mesh.dispose();
    }
    renderables.clear();
    terrainData = null;
    meshBuilder.clear();
    entities.clear();
    entities = null;
    meshBuilder = null;
    material    = null;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    renderables.addAll(this.renderables);
  }

  /**
   * For each tile renderable create entity and add it to {@link EntityManager}
   * @param entityManager
   */
  public void addTo(EntityManager entityManager) {
    entities.clear();
    for(Renderable tileRenderable : renderables) {
      BodyComponent bodyComponent = entityManager.createComponent(BodyComponent.class);
      tileRenderable.meshPart.mesh.calculateBoundingBox(bodyComponent);

      PositionComponent positionComponent = entityManager.createComponent(PositionComponent.class);
      bodyComponent.getMin(positionComponent);

      TerrainRenderableComponent terrainRenderableComponent = entityManager.createComponent(TerrainRenderableComponent.class);
      terrainRenderableComponent.terrainTile                = tileRenderable;

      Entity tileEntity = entityManager.createEntity();
      tileEntity.add(bodyComponent);
      tileEntity.add(terrainRenderableComponent);
      tileEntity.add(positionComponent);

      entityManager.addEntity(tileEntity);
      entities.add(tileEntity);
    }
  }

  public Array<Entity> getEntities() {
    return entities;
  }
}
