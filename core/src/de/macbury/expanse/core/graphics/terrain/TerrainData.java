package de.macbury.expanse.core.graphics.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
  private final PerlinNoise perlinNoise;
  private final BrownianNoise3D brownianNoise;
  private final Color groundColor;
  private final Color snowColor;
  private final Color rockColor;

  public TerrainData() {
    this.perlinNoise    = new PerlinNoise(System.currentTimeMillis());
    this.brownianNoise  = new BrownianNoise3D(perlinNoise, 4);
    this.heightmap      = new Pixmap(Gdx.files.internal("textures/Heightmap-mini.PNG"));
    this.tempColor      = new Color();

    this.rockColor      = new Color(Color.DARK_GRAY);
    this.groundColor    = new Color(Color.BROWN);
    this.snowColor      = new Color(Color.WHITE);
  }

  @Override
  public void dispose() {
    heightmap.dispose();
  }

  public float getElevation(int x, int z) {
    //return (float)brownianNoise.noise(x/4, 0, z/4) * 20f;
    //perlinNoise.noise();
    return tempColor.set(heightmap.getPixel(x,z)).r * 20;
  }

  public int getWidth() {
    return heightmap.getWidth();
  }

  public int getHeight() {
    return heightmap.getHeight();
  }

  public Color getColor(int x, int z) {
    if (getElevation(x,z) >= 19) {
      return snowColor;
    } else if (getElevation(x,z) >= 4) {
      return rockColor;
    } else if (getElevation(x,z) >= 3) {
      return Color.FOREST;
    } else {
      return groundColor;
    }
  }
}
