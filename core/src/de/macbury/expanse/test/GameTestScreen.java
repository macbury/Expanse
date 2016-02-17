package de.macbury.expanse.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.macbury.expanse.core.entities.EntityManager;
import de.macbury.expanse.core.entities.components.*;
import de.macbury.expanse.core.entities.states.RobotInstructionState;
import de.macbury.expanse.core.entities.states.RobotMotorState;
import de.macbury.expanse.core.graphics.camera.GameCamera;
import de.macbury.expanse.core.graphics.camera.RTSCameraController;
import de.macbury.expanse.core.graphics.terrain.Terrain;
import de.macbury.expanse.core.octree.LevelOctree;
import de.macbury.expanse.core.screens.ScreenBase;

/**
 * Created on 29.01.16.
 */
public class GameTestScreen extends ScreenBase {
  private GameCamera camera;
  private RTSCameraController rtsCameraController;
  private EntityManager entities;
  private ShapeRenderer shapeRenderer;
  private Terrain terrain;
  private LevelOctree<PositionComponent> octree;

  @Override
  public void preload() {
    assets.load("model:rock.g3dj", Model.class);
    assets.load("model:cube.g3dj", Model.class);
    assets.load("terrain:playground.json", Terrain.class);
  }

  @Override
  public void create() {
    this.terrain              = assets.get("terrain:playground.json");
    this.shapeRenderer        = new ShapeRenderer();
    this.camera               = new GameCamera();
    this.rtsCameraController  = new RTSCameraController(input);

    this.octree               = new LevelOctree<PositionComponent>();

    //ssthis.entities             = new EntityManager(game, camera, octree, terrain);
    terrain.addToEntityManager(entities);

    rtsCameraController.setCamera(camera);
    rtsCameraController.setOverlay(hud.getOverlay());
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
        screens.set(new GameTestScreen());
        return true;
      }
    });
    hud.addActor(button);
  }

  @Override
  public void render(float delta) {
    rtsCameraController.update(delta);
    entities.update(delta);

    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
      camera.saveDebugFrustrum();
    }
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

    RobotCPUComponent robotCPUComponent = entities.createComponent(RobotCPUComponent.class);
    robotCPUComponent.init(robotEntity, messages, RobotInstructionState.Living, RobotInstructionState.WaitForInstruction);//TODO entity manager should do this

    //RobotScriptComponent robotScriptComponent = entities.createComponent(RobotScriptComponent.class);
    //robotScriptComponent.setSource(source);

    PositionComponent positionComponent = entities.createComponent(PositionComponent.class);
    positionComponent.dimension.set(2,2,2);
    positionComponent.set(position);

    robotEntity.add(entities.createComponent(TimerComponent.class));
    robotEntity.add(robotCPUComponent);
    //robotEntity.add(robotScriptComponent);
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
  public void dispose() {
    assets.unload("model:rock.g3dj");
    assets.unload("model:cube.g3dj");
    assets.unload("terrain:playground.json");
    entities.dispose();
    shapeRenderer.dispose();
    rtsCameraController.dispose();
    octree.dispose();
  }
}
