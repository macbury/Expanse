package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.*;
import de.macbury.expanse.core.entities.states.RobotInstructionState;
import de.macbury.expanse.core.entities.states.RobotMotorState;
import de.macbury.expanse.core.graphics.DebugShape;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.graphics.terrain.TerrainAssembler;
import de.macbury.expanse.core.graphics.terrain.TerrainData;
import de.macbury.expanse.core.octree.WorldOctree;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.ui.Hud;

/**
 * Created on 29.01.16.
 */
public class GameTestScreen extends ScreenBase {
  private GameCamera camera;
  private Hud hud;
  private RTSCameraController rtsCameraController;
  private WorldOctree octree;
  private EntityManager entities;
  private FPSLogger fpsLogger;
  private ShapeRenderer shapeRenderer;
  private Terrain terrain;

  @Override
  public void preload() {
    assets.load("model:cube.g3dj", Model.class);
    assets.load("terrain:playground.json", Terrain.class);
  }

  @Override
  public void create() {
    this.terrain              = assets.get("terrain:playground.json");
    this.shapeRenderer        = new ShapeRenderer();
    this.camera               = new GameCamera();
    this.hud                  = new Hud(input, assets);
    this.rtsCameraController  = new RTSCameraController(input);

    camera.far = 300;
    camera.near = 1;

    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(hud.getOverlay());
    this.octree           = new WorldOctree();

    this.entities         = new EntityManager(camera, messages, octree, terrain);
    terrain.addToEntityManager(entities);
    this.fpsLogger        = new FPSLogger();

    rtsCameraController.setListener(terrain);
    rtsCameraController.setCenter(terrain.getCenter());

    createRobot(new Vector3(terrain.getCenter().x, 1f, terrain.getCenter().y), Gdx.files.internal("scripts/terrain_test.js").readString());
    octree.setBounds(terrain.getBoundingBox(new BoundingBox()));

    Button button = new Button(hud.getSkin());
    button.setPosition(20, 20);
    button.setSize(100, 100);
    button.addListener(new ClickListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        Gdx.app.log("Button", "YESt");
        return true;
      }
    });
    hud.addActor(button);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1,1,1,1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    rtsCameraController.update(delta);
    entities.update(delta);
    fpsLogger.log();

    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
      camera.saveDebugFrustrum();
    }

    hud.act(delta);
    hud.draw();
  }

  public Entity createRobot(Vector3 position, String source) {
    Entity robotEntity                            = entities.createEntity();

    ModelComponent modelComponent                 = entities.createComponent(ModelComponent.class);
    modelComponent.modelInstance                  = new ModelInstance(assets.get("model:cube.g3dj", Model.class));

    BodyComponent bodyComponent                   = entities.createComponent(BodyComponent.class);
    //bodyComponent.dimensions.set(1, 1, 1.5f); //TODO fix this

    MotorComponent motorComponent                 = entities.createComponent(MotorComponent.class);
    motorComponent.init(robotEntity, messages, null, null);
    motorComponent.changeState(RobotMotorState.Idle);
    motorComponent.speed                          = 1;

    RobotInstructionStateComponent robotInstructionStateComponent = entities.createComponent(RobotInstructionStateComponent.class);
    robotInstructionStateComponent.init(robotEntity, messages, RobotInstructionState.Living, RobotInstructionState.WaitForInstruction);

    RobotScriptComponent robotScriptComponent = entities.createComponent(RobotScriptComponent.class);
    robotScriptComponent.setSource(source);

    PositionComponent positionComponent = entities.createComponent(PositionComponent.class);
    positionComponent.dimension.set(2,2,2);
    positionComponent.set(position);

    robotEntity.add(entities.createComponent(TimerComponent.class));
    robotEntity.add(robotInstructionStateComponent);
    robotEntity.add(robotScriptComponent);
    robotEntity.add(positionComponent);
    robotEntity.add(motorComponent);
    robotEntity.add(bodyComponent);
    robotEntity.add(modelComponent);

    entities.addEntity(robotEntity);
    return robotEntity;
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void unload() {
    assets.unload("model:cube.g3dj");
    assets.unload("terrain:playground.json");
  }

  @Override
  public void dispose() {

  }
}
