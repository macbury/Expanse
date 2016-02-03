package de.macbury.expanse.core.graphics.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import de.macbury.expanse.core.graphics.camera.frustrum.DebugFrustrum;

/**
 * This is main game camera. It contains helper methods for debugging
 */
public class GameCamera extends PerspectiveCamera implements ICamera {
  private static final float BASE_FOV = 74;
  private final Vector3 debugDirection;
  private final Vector3 debugPosition;
  private DebugFrustrum debugFrustrum;

  public GameCamera() {
    super(BASE_FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    debugDirection     = new Vector3();
    this.debugPosition = new Vector3();
    this.far           = 400;
    this.near          = 0.1f;
  }
  @Override
  public void saveDebugFrustrum() {
    this.debugFrustrum = new DebugFrustrum(frustum, invProjectionView);
    this.debugDirection.set(direction);
    debugPosition.set(position);
  }

  @Override
  public boolean haveDebugFrustrum() {
    return debugFrustrum != null;
  }
  @Override
  public DebugFrustrum getDebugFrustrum() {
    return debugFrustrum;
  }
  @Override
  public void clearDebugFrustrum() {
    this.debugFrustrum = null;
  }

  /**
   * Return debug frustum if have or return normal frustrum
   * @return
   */
  @Override
  public Frustum normalOrDebugFrustrum() {
    if (haveDebugFrustrum()) {
      return debugFrustrum;
    } else {
      return frustum;
    }
  }

  public Vector3 normalOrDebugDirection() {
    return haveDebugFrustrum() ? debugDirection : direction;
  }

  public Vector3 normalOrDebugPosition() {
    return haveDebugFrustrum() ? debugPosition : position;
  }
  @Override
  public boolean boundsInFrustum(BoundingBox testBounds) {
    return normalOrDebugFrustrum().boundsInFrustum(testBounds);
  }
}
