package de.macbury.expanse.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import de.macbury.expanse.core.screens.ScreenBase;
import de.macbury.expanse.core.ui.Hud;

/**
 * Created on 27.01.16.
 */
public class HudTest extends ScreenBase {

  @Override
  public void preload() {

  }

  @Override
  public void create() {
    Button button = new Button(hud.getSkin());
    button.setPosition(20, 20);
    button.setSize(100, 100);
    hud.addActor(button);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1,1,1,1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    hud.act(delta);
    hud.draw();
  }

  @Override
  public void resize(int width, int height) {

  }


  @Override
  public void dispose() {
    hud.dispose();
  }
}
