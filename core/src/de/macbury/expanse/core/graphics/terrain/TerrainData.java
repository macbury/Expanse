package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.procedular.BrownianNoise;
import de.macbury.expanse.core.procedular.BrownianNoise3D;
import de.macbury.expanse.core.procedular.PerlinNoise;
import de.macbury.expanse.core.procedular.PerlinNoise2D;

import java.util.ArrayList;

/**
 * This class contains all information about terrain like height and colors
 */
public class TerrainData implements Disposable {
  private final Color tempColor;
  private final Color groundColor;
  private final Color snowColor;
  private final Color rockColor;
  private final int maxElevation;
  private final PerlinNoise2D noise;
  private final int width;
  private final int height;
  private float[][] elevation;
  private float[][] shadeFactor;
  private Array<Vector3> islandCenters;
  public TerrainData(Blueprint blueprint) {
    this.width          = blueprint.width;
    this.height         = blueprint.height;
    this.noise          = new PerlinNoise2D(blueprint.seed);
    this.maxElevation   = blueprint.maxElevation;
    this.tempColor      = new Color();

    this.rockColor      = new Color(Color.DARK_GRAY);
    this.groundColor    = new Color(165f/255f, 121f/255f, 74f/255f, 1f);
    this.snowColor      = new Color(Color.WHITE);
    this.islandCenters  = new Array<Vector3>();
    islandCenters.add(new Vector3(width/2, height/2, 150));
    islandCenters.add(new Vector3(width/3, height/3, 55));
    islandCenters.add(new Vector3(100, 200, 40));
    buildElevation();
  }



  private void buildElevation() {
    elevation   = new float[getWidth()][getHeight()];
    shadeFactor = new float[getWidth()][getHeight()];
    Vector2 vectorCursor = new Vector2();

    for (int x = 0; x < getWidth(); x++) {
      for (int z = 0; z < getHeight(); z++) {

        float total = 0.1f;
        for (int i = 0; i < islandCenters.size; i++) {
          Vector3 islandVector   = islandCenters.get(i);
          float distanceToCenter = vectorCursor.set(x, z).dst(islandVector.x, islandVector.y);
          if (distanceToCenter <= islandVector.z) {
            total += (islandVector.z - distanceToCenter) / islandVector.z;
          }
        }
        total = Math.min(total, 1f);

        elevation[x][z]   = noise.terrainNoise(x,z, total * getMaxElevation(), 4, 1.3f);
        shadeFactor[x][z] = noise.interpolatedNoise(x,z) * 0.1f;
        //elevation[x][z] = total * getMaxElevation();
        //elevation[x][z] = 0;
      }
    }
  }

  @Override
  public void dispose() {

  }

  public float getElevation(int x, int z) {
    return elevation[x][z];
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Color getColor(int x, int z) {

    if (elevation[x][z] >= getMaxElevation() * 0.67f) {
      tempColor.set(snowColor);
    } else if (elevation[x][z] >= getMaxElevation() * 0.47f) {
      tempColor.set(rockColor);
    } else {
      tempColor.set(groundColor);
    }

    float f = shadeFactor[x][z];
    tempColor.sub(f, f, f,0);

    return tempColor;
  }

  /**
   * Calculates bounding box and returns it
   * @return
   */
  public BoundingBox getBoundingBox() {
    return new BoundingBox(
      new Vector3(0,-1,0),
      new Vector3(getWidth(), getMaxElevation()+5, getHeight())
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

  /**
   * This is simple class to load information about map from json file
   */
  public static class Blueprint {
    public int maxElevation;
    public long seed;
    public int width;
    public int height;
  }
}