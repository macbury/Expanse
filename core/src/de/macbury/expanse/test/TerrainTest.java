package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.macbury.expanse.core.graphics.camera.Overlay;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.screens.ScreenBase;

/**
 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/g3d/HeightField.java
 */
public class TerrainTest extends ScreenBase {
  private MeshBuilder meshBuilder;
  private ModelBatch modelBatch;
  private PerspectiveCamera camera;
  private CameraInputController cameraController;
  private Renderable tileRenderable;
  private Environment env;
  private Pixmap heightmap;
  private Color tempColor;
  private Stage stage;
  private Overlay overlay;
  private RTSCameraController rtsCameraController;

  @Override
  public void preload() {

  }

  @Override
  public void create() {
    this.camera         = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    this.stage          = new Stage();
    this.overlay        = new Overlay();
    this.rtsCameraController = new RTSCameraController(input);

    camera.far = 300;
    camera.near = 1;

    rtsCameraController.setCenter(0, 0);
    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(overlay);
    input.addProcessor(stage);
    stage.addActor(overlay);

    this.modelBatch  = new ModelBatch();
    this.meshBuilder = new MeshBuilder();


    this.heightmap      = new Pixmap(Gdx.files.internal("textures/heightmap-mini.jpg"));
    this.tempColor      = new Color();
    this.env            = new Environment();
    env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    this.tileRenderable = new Renderable();
    this.tileRenderable.material = new Material();
    this.tileRenderable.environment = env;
    meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked); {
      meshBuilder.part("tile1", GL20.GL_TRIANGLES, this.tileRenderable.meshPart);
      //meshBuilder.box(1,1,1,1,1,1);
      //meshBuilder.setColor(Color.BROWN);
      MeshPartBuilder.VertexInfo vertexInfo = new MeshPartBuilder.VertexInfo();
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
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    camera.update();
    rtsCameraController.update(delta);

    modelBatch.begin(camera); {
      modelBatch.render(tileRenderable);
    } modelBatch.end();

    stage.act(delta);
    stage.draw();
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
