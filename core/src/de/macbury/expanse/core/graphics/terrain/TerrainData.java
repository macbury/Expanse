package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.procedular.BrownianNoise;
import de.macbury.expanse.core.procedular.BrownianNoise3D;
import de.macbury.expanse.core.procedular.PerlinNoise;

/**
 * This class contains all information about terrain like height and colors
 */
public class TerrainData implements Disposable {
  private final Pixmap heightmap;
  private final Color tempColor;
  private final Color groundColor;
  private final Color snowColor;
  private final Color rockColor;
  private final int maxElevation;


  public TerrainData(Pixmap heightMap, int maxElevation) {
    this.maxElevation   = maxElevation;
    this.heightmap      = heightMap;
    this.tempColor      = new Color();

    this.rockColor      = new Color(Color.DARK_GRAY);
    this.groundColor    = new Color(Color.BROWN);
    this.snowColor      = new Color(Color.WHITE);
  }

  @Override
  public void dispose() {

  }

  public float getElevation(int x, int z) {
    return tempColor.set(heightmap.getPixel(x,z)).a * getMaxElevation();
  }

  public int getWidth() {
    return heightmap.getWidth();
  }

  public int getHeight() {
    return heightmap.getHeight();
  }

  public Color getColor(int x, int z) {
    return groundColor;
  }

  /**
   * Calculates bounding box and returns it
   * @return
   */
  public BoundingBox getBoundingBox() {
    return new BoundingBox(
      new Vector3(0,-1,0),
      new Vector3(getWidth(), getMaxElevation(), getHeight())
    );
  }

  float getMaxElevation() {
    return maxElevation;
  }

  /**
   * Creates a new vector with center of terrain
   * @return
   */
  public Vector2 getCenter() {
    return new Vector2(getWidth()/2, getHeight()/2);
  }

}
