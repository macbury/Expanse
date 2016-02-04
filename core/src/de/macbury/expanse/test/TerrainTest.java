package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.graphics.camera.Overlay;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.framebuffer.Fbo;
import de.macbury.expanse.core.graphics.framebuffer.FullScreenFrameBufferResult;
import de.macbury.expanse.core.graphics.gbuffer.GBuffer;
import de.macbury.expanse.core.graphics.terrain.TerrainAssembler;
import de.macbury.expanse.core.graphics.terrain.TerrainData;
import de.macbury.expanse.core.screens.ScreenBase;


/**
 * https://www.reddit.com/r/gamedev/comments/1g4eae/need_help_generating_an_island_using_perlin_noise/
 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/g3d/HeightField.java
 */
public class TerrainTest extends ScreenBase {
  private MeshBuilder meshBuilder;
  private ModelBatch modelBatch;
  private Camera camera;
  private CameraInputController cameraController;
  private Renderable tileRenderable;
  private Environment env;
  private Pixmap heightmap;
  private Color tempColor;
  private Stage stage;
  private Overlay overlay;
  private RTSCameraController rtsCameraController;
  private final static BoundingBox bounds = new BoundingBox();
  private final static Pool<MeshPartBuilder.VertexInfo> VertexInfoPool = new Pool<MeshPartBuilder.VertexInfo>() {
    @Override
    protected MeshPartBuilder.VertexInfo newObject() {
      return new MeshPartBuilder.VertexInfo();
    }
  };
  private TerrainData terrainData;
  private TerrainAssembler terrainAssembler;
  private ShapeRenderer shapeRenderer;
  private GBuffer gbuffer;
  private RenderContext renderContext;

  @Override
  public void preload() {

  }

  @Override
  public void create() {
    this.gbuffer        = new GBuffer();
    this.camera         = new PerspectiveCamera(74, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    //this.camera         = new OrthographicCamera(70, 40);

    this.stage          = new Stage();
    this.overlay        = new Overlay();
    this.rtsCameraController = new RTSCameraController(input);

    this.shapeRenderer  = new ShapeRenderer();
    camera.far = 300;
    camera.near = 1;

    rtsCameraController.setCenter(terrainData.getWidth() / 2, terrainData.getHeight() / 2);
    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(overlay);

    input.addProcessor(stage);


    this.modelBatch  = new ModelBatch(renderContext);
    this.meshBuilder = new MeshBuilder();


    this.heightmap      = new Pixmap(Gdx.files.internal("textures/heightmap-mini.jpg"));
    this.tempColor      = new Color();
    this.env            = new Environment();
    env.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f,1f, 1f));
    env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    env.set(new ColorAttribute(ColorAttribute.Fog, 1f, 1f, 1f, 1f));

    this.tileRenderable = new Renderable();
    this.tileRenderable.material = new Material();
    this.tileRenderable.environment = env;
    meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked); {
      meshBuilder.part("tile1", GL20.GL_LINES, this.tileRenderable.meshPart);
      //meshBuilder.box(1,1,1,1,1,1);
      //meshBuilder.setColor(Color.BROWN);
      /*MeshPartBuilder.VertexInfo vertexInfo = new MeshPartBuilder.VertexInfo();
      for (int x = 0; x < 70; x++) {
        for (int z = 0; z < 70; z++) {

          float y1 = tempColor.set(heightmap.getPixel(x,z)).r * 10;
          float y2 = tempColor.set(heightmap.getPixel(x+1,z)).r * 10;
          float y3 = tempColor.set(heightmap.getPixel(x,z+1)).r * 10;
          float y4 = tempColor.set(heightmap.getPixel(x+1,z+1)).r * 10;

          short triangleVertex1 = meshBuilder.vertex(new Vector3(x,y1,z), Vector3.Y, Color.GREEN, Vector2.Zero);
          short triangleVertex2 = meshBuilder.vertex(new Vector3(x,y3,z+1), Vector3.Y, Color.GREEN, Vector2.Zero);
          short triangleVertex3 = meshBuilder.vertex(new Vector3(x+1,y2,z), Vector3.Y, Color.GREEN, Vector2.Zero);

          short triangleVertex4 = meshBuilder.vertex(new Vector3(x+1,y4,z+1), new Vector3(0.2f,0.2f, 0.2f), Color.GREEN, Vector2.Zero);
          short triangleVertex5 = meshBuilder.vertex(new Vector3(x,y3,z+1), new Vector3(0.2f,0.2f, 0.2f), Color.GREEN, Vector2.Zero);
          short triangleVertex6 = meshBuilder.vertex(new Vector3(x+1,y2,z), new Vector3(0.2f,0.2f, 0.2f), Color.GREEN, Vector2.Zero);

          meshBuilder.triangle(triangleVertex1, triangleVertex2, triangleVertex3);
          meshBuilder.triangle(triangleVertex5, triangleVertex4, triangleVertex6);
        }
      }*/


      for (int x = 0; x < 70; x++) {
        for (int z = 0; z < 70; z++) {


          float y1 = tempColor.set(heightmap.getPixel(x,z)).r * 10;
          float y2 = tempColor.set(heightmap.getPixel(x+1,z)).r * 10;
          float y3 = tempColor.set(heightmap.getPixel(x,z+1)).r * 10;
          float y4 = tempColor.set(heightmap.getPixel(x+1,z+1)).r * 10;

          short triangleVertex1 = meshBuilder.vertex(new Vector3(x,y1,z), Vector3.Y, Color.GREEN, Vector2.Zero);
          short triangleVertex2 = meshBuilder.vertex(new Vector3(x,y3,z+1), Vector3.Y, Color.GREEN, Vector2.Zero);
          short triangleVertex3 = meshBuilder.vertex(new Vector3(x+1,y2,z), Vector3.Y, Color.GREEN, Vector2.Zero);

          short triangleVertex4 = meshBuilder.vertex(new Vector3(x+1,y4,z+1), new Vector3(0.2f,0.2f, 0.2f), Color.GREEN, Vector2.Zero);
          short triangleVertex5 = meshBuilder.vertex(new Vector3(x,y3,z+1), new Vector3(0.2f,0.2f, 0.2f), Color.GREEN, Vector2.Zero);
          short triangleVertex6 = meshBuilder.vertex(new Vector3(x+1,y2,z), new Vector3(0.2f,0.2f, 0.2f), Color.GREEN, Vector2.Zero);

          meshBuilder.triangle(triangleVertex1, triangleVertex2, triangleVertex3);
          meshBuilder.triangle(triangleVertex5, triangleVertex4, triangleVertex6);
        }
      }


      //meshBuilder.triangle(Vector3.Zero, new Vector3(0,0,1), new Vector3(1,0,0));
      //meshBuilder.ve
    } meshBuilder.end();


    input.addProcessor(stage);

    stage.addActor(new FullScreenFrameBufferResult(Fbo.FinalResult, fb));
    stage.addActor(overlay);
  }

  @Override
  public void render(float delta) {
    camera.update();
    rtsCameraController.update(delta);

    fb.begin(Fbo.FinalResult); {
      Gdx.gl.glClearColor(1,1,1,1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

      modelBatch.begin(camera); {
        //modelBatch.render(terrainAssembler, env);
      } modelBatch.end();
    } fb.end();


    Gdx.gl.glClearColor(1,1,1,1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
      fb.saveAsPng(Fbo.FinalResult);
    }
  }

  @Override
  public void resize(int width, int height) {

  }


  @Override
  public void dispose() {
    stage.dispose();
  }
}
