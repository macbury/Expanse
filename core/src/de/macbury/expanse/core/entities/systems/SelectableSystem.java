package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.World;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.OctreeIteratingSystem;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.SelectableComponent;
import de.macbury.expanse.core.octree.LevelOctree;
import de.macbury.expanse.core.octree.OctreeNode;
import de.macbury.expanse.core.octree.query.OctreeQuery;
import de.macbury.expanse.core.ui.Hud;
import de.macbury.expanse.core.ui.HudInputListener;

/**
 * This system allows user to select entities using left button. If entity is removed from world it will deselect it.
 * System broadcast information about selection using {@link TelegramEvents#SelectedEntity} and {@link TelegramEvents#DeselectedEntity}
 * with {@link PositionComponent} as sender
 */
public class SelectableSystem extends EntitySystem implements Disposable, OctreeQuery<PositionComponent>, EntityListener {
  private static final String TAG = "SelectableSystem";
  private final Family family;
  private Messages messages;
  private PerspectiveCamera worldCamera;
  private LevelOctree<PositionComponent> octree;
  private HudInputListener inputListener;
  private Hud hud;
  private Ray mouseSelectableRay      = new Ray(new Vector3(), new Vector3());
  private Vector3 intersectionVector  = new Vector3();
  private BoundingBox tempBoundingBox = new BoundingBox();
  private Array<PositionComponent> selectedObjects;

  public SelectableSystem(LevelOctree<PositionComponent> octree, Hud hud, PerspectiveCamera worldCamera, Messages messages) {
    family   = Family.all(PositionComponent.class, SelectableComponent.class).get();
    this.hud = hud;
    this.messages = messages;
    this.worldCamera = worldCamera;
    this.octree = octree;
    this.selectedObjects = new Array<PositionComponent>(2000);

    this.inputListener = new HudInputListener(hud) {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return onTouchDown(x, y, button);
      }
    };
  }

  /**
   * If left button is presed, check if any entity with {@link SelectableComponent} is clicked
   * @param x
   * @param y
   * @param button
   * @return
   */
  private boolean onTouchDown(float x, float y, int button) {
    if (button == Input.Buttons.LEFT) {
      unselectEntities();
      mouseSelectableRay.set(worldCamera.getPickRay(Gdx.input.getX(), Gdx.input.getY()));
      octree.retrieve(selectedObjects, this);
      for (int i = 0; i < selectedObjects.size; i++) {
        messages.dispatchMessage(selectedObjects.get(i), TelegramEvents.SelectedEntity, null);
      }

      //Gdx.app.log(TAG, "Selected: " + selectedObjects.size);
      return selectedObjects.size >= 0;
    } else if (button == Input.Buttons.RIGHT) {
      //TODO implement clicking on terrain or giving action
      mouseSelectableRay.set(worldCamera.getPickRay(Gdx.input.getX(), Gdx.input.getY()));

      return false;
    } else {
      return false;
    }
  }

  private void unselectEntities() {
    for (int i = 0; i < selectedObjects.size; i++) {
      messages.dispatchMessage(selectedObjects.get(i), TelegramEvents.DeselectedEntity, null);
    }
    selectedObjects.clear();
  }

  @Override
  public void addedToEngine(Engine engine) {

  }

  @Override
  public void update(float deltaTime) {

  }

  @Override
  public void removedFromEngine(Engine engine) {

  }

  @Override
  public void dispose() {
    unselectEntities();
    messages = null;
    worldCamera = null;
    inputListener.dispose();
    hud = null;
    octree = null;
  }

  @Override
  public boolean checkNode(OctreeNode node) {
    return Intersector.intersectRayBoundsFast(mouseSelectableRay, node.getBounds());
  }

  @Override
  public boolean checkObject(PositionComponent object) {
    if (family.matches(object.entity)) {
      object.getBoundingBox(tempBoundingBox);
      return Intersector.intersectRayBoundsFast(mouseSelectableRay, tempBoundingBox);
    } else {
      return false;
    }
  }

  @Override
  public void entityAdded(Entity entity) {

  }

  /**
   * If entity is removed, remove selection from it
   * @param entity
   */
  @Override
  public void entityRemoved(Entity entity) {
    if (family.matches(entity)) {
      PositionComponent positionComponent = Components.Position.get(entity);
      selectedObjects.removeValue(positionComponent, true);
      messages.dispatchMessage(positionComponent, TelegramEvents.DeselectedEntity, null);
    }
  }
}
