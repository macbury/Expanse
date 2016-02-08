package de.macbury.expanse.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.graphics.camera.GameCamera;

/**
 * Renders cursor for {@link PositionComponent}
 */
public class SelectedUnitCursor extends Image implements Telegraph, Disposable {
  private static final String TAG = "SelectedUnitCursor";
  private static final float MIN_CURSOR_SIZE = 32;
  private GameCamera camera;
  private Messages messages;
  private PositionComponent targetPosition;
  private Vector3 screenVec = new Vector3();
  private Vector3 screenDim = new Vector3();
  private BoundingBox tempBoundingBox = new BoundingBox();

  public SelectedUnitCursor(Messages messages, NinePatch ninePatch, GameCamera camera) {
    super(ninePatch);
    this.messages = messages;
    this.camera   = camera;

    messages.addListener(this, TelegramEvents.SelectedEntity);
    messages.addListener(this, TelegramEvents.DeselectedEntity);
    setVisible(false);
    setTouchable(Touchable.disabled);
  }

  private Vector3 minVec = new Vector3();
  private Vector3 maxVec = new Vector3();
  @Override
  public void act(float delta) {
    if (targetPosition != null) {
      targetPosition.getBoundingBox(tempBoundingBox);
      tempBoundingBox.getMin(minVec);
      tempBoundingBox.getMax(maxVec);

      camera.project(minVec);
      camera.project(maxVec);

      screenDim.set(minVec).sub(maxVec);


      camera.project(screenVec.set(targetPosition));
      float size = Math.max(Math.max(Math.abs(screenDim.x), Math.abs(screenDim.y))*2, MIN_CURSOR_SIZE);

      setSize(
        size,
        size
      );

      setPosition(
        Math.round(screenVec.x),
        Math.round(screenVec.y),
        Align.center
      );
    }
    super.act(delta);
  }

  @Override
  public boolean handleMessage(Telegram msg) {
    switch (TelegramEvents.from(msg)) {
      case SelectedEntity:
        targetPosition = (PositionComponent)msg.sender;
        setVisible(true);
        return true;
      case DeselectedEntity:
        targetPosition = null;
        setVisible(false);
        return true;
    }
    return false;
  }

  @Override
  public void dispose() {
    remove();
    camera = null;
    messages.removeListener(this, TelegramEvents.SelectedEntity);
    messages.removeListener(this, TelegramEvents.DeselectedEntity);
    messages = null;
  }
}
