package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.*;
import de.macbury.expanse.core.entities.states.RobotInstructionState;
import de.macbury.expanse.core.entities.states.RobotMotorState;
import de.macbury.expanse.core.graphics.DebugShape;
import de.macbury.expanse.core.graphics.camera.Overlay;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.octree.WorldOctree;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.scripts.ScriptRunner;

/**
 * Created on 18.01.16.
 */
public class TestScreen extends ScreenBase {
  private static final String TAG = "TestScreen";
  private Texture texture;
  private SpriteBatch spriteBatch;
  private PerspectiveCamera camera;
  private ScriptRunner scriptRunner;
  private FPSLogger fpsLogger;
  private int frameNo;
  private EntityManager entities;
  private boolean entityPause;
  private Entity myRobotEntity;
  private WorldOctree octree;
  private ShapeRenderer shapeRenderer;
  private Array<Entity> tempEntities = new Array<Entity>();
  private CameraInputController cameraController;
  private Model robotModel;
  private Overlay overlay;
  private RTSCameraController rtsCameraController;
  private Stage stage;

  @Override
  public void preload() {
    this.assets.load("textures:bot.png", Texture.class);
  }

  @Override
  public void create() {

    this.shapeRenderer  = new ShapeRenderer();
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

    this.texture     = this.assets.get("textures:bot.png", Texture.class);
    this.spriteBatch = new SpriteBatch();
    this.octree      = new WorldOctree();

    octree.setBounds(new BoundingBox(new Vector3(-500, -2, -500), new Vector3(500, 100, 500))); // in pixels
    this.entities    = new EntityManager(camera, messages, octree);
    this.fpsLogger   = new FPSLogger();

    this.cameraController = new CameraInputController(camera);

    assets.load("models/cube.g3dj", Model.class);
    assets.finishLoading();
    this.robotModel = assets.get("models/cube.g3dj");

    //createRobot(new Vector3(250, 0, 250), Gdx.files.internal("scripts/robot1.js").readString());

    //createRobot(new Vector3(550, 0, 350), Gdx.files.internal("scripts/robot2.js").readString());

    for (int i = 0; i < 800; i++) {
      createRobot(new Vector3(
        (float) (Math.random() * 800)-400,
        0.5f,
        (float) (Math.random() * 800)-400
      ), Gdx.files.internal("scripts/random_robot_mov_test.js").readString());
    }

    /*for (int i = 0; i < 30; i++) {
      createRobot(new Vector3(500, 0, 500), Gdx.files.internal("scripts/my_robot.js").readString());
    }*/


    this.myRobotEntity = createRobot(new Vector3(0, 0.5f, 0), Gdx.files.internal("scripts/my_robot.js").readString());

  }

  public Entity createRobot(Vector3 position, String source) {
    Entity robotEntity                            = entities.createEntity();

    RenderableComponent renderableComponent       = entities.createComponent(RenderableComponent.class);
    renderableComponent.modelInstance             = new ModelInstance(robotModel);

    BodyComponent bodyComponent                   = entities.createComponent(BodyComponent.class);
    bodyComponent.dimensions.set(1, 1, 1.5f);

    MotorComponent motorComponent                 = entities.createComponent(MotorComponent.class);
    motorComponent.init(robotEntity, messages, null, null);
    motorComponent.changeState(RobotMotorState.Idle);
    motorComponent.speed                          = 1;

    RobotInstructionStateComponent robotInstructionStateComponent = entities.createComponent(RobotInstructionStateComponent.class);
    robotInstructionStateComponent.init(robotEntity, messages, RobotInstructionState.Living, RobotInstructionState.WaitForInstruction);

    RobotScriptComponent robotScriptComponent = entities.createComponent(RobotScriptComponent.class);
    robotScriptComponent.setSource(source);

    PositionComponent positionComponent = entities.createComponent(PositionComponent.class);
    positionComponent.set(position);

    SpriteComponent spriteComponent     = entities.createComponent(SpriteComponent.class);
    spriteComponent.setSize(texture.getWidth(), texture.getHeight());
    spriteComponent.setOrigin(texture.getWidth()/2, texture.getHeight()/2);
    spriteComponent.setRotation(0);
    spriteComponent.setRegion(texture);

    robotEntity.add(entities.createComponent(TimerComponent.class));
    robotEntity.add(robotInstructionStateComponent);
    robotEntity.add(robotScriptComponent);
    robotEntity.add(positionComponent);
    //robotEntity.add(spriteComponent);
    robotEntity.add(motorComponent);
    robotEntity.add(bodyComponent);
    robotEntity.add(renderableComponent);

    entities.addEntity(robotEntity);
    tempEntities.add(robotEntity);
    return robotEntity;
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    camera.update();


    rtsCameraController.update(delta);
    if (!entityPause) {
      entities.update(delta);
    }

    frameNo++;

    fpsLogger.log();
    //scriptRunner.resume("random result");
    if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
      this.entityPause = !entityPause;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
      messages.dispatchStopRobot(myRobotEntity);
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {

      messages.dispatchStartRobot(myRobotEntity);
    }

    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
      shapeRenderer.setColor(Color.WHITE);
      DebugShape.octree(shapeRenderer, octree);
    } shapeRenderer.end();
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line); {
      shapeRenderer.setColor(Color.RED);
      for (int i = 0; i < tempEntities.size; i++) {
        //BodyComponent bodyComponent = Components.Body.get(tempEntities.get(i));
        //PositionComponent positionComponent = Components.Position.get(tempEntities.get(i));
        DebugShape.draw(shapeRenderer, Components.Body.get(tempEntities.get(i)));
      }
    } shapeRenderer.end();

    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    //camera.setToOrtho(false, width, height);
    overlay.invalidate();
  }

  @Override
  public void unload() {
    this.assets.unload("textures:bot.png");
  }

  @Override
  public void dispose() {
    spriteBatch.dispose();
    scriptRunner.dispose();
    entities.dispose();
    stage.dispose();
    input.removeProcessor(stage);
  }
}
