package de.macbury.expanse.core.graphics;

import com.badlogic.gdx.math.Vector3;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.graphics.camera.GameCamera;

/**
 * This class describes all level of details
 */
public enum Lod {
  High(1),
  Low(2)
  ;
  private final static Vector3 tempPos = new Vector3();
  public final int resolution;
  Lod(int resolution) {
    this.resolution = resolution;
  }

  /**
   * Return lod dependent on distance from camera
   * @param camera
   * @param position
   * @return
   */
  public static Lod by(GameCamera camera, Vector3 position) {
    float distance = tempPos.set(camera.normalOrDebugPosition()).dst(position) / camera.far;

    if (distance >= 0.6f) {
      return Low;
    } else {
      return High;
    }
  }
}
