package de.macbury.expanse.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import de.macbury.expanse.core.TelegramEvents;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.entities.Messages;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.graphics.camera.Overlay;
import de.macbury.expanse.core.graphics.framebuffer.Fbo;
import de.macbury.expanse.core.graphics.framebuffer.FrameBufferManager;
import de.macbury.expanse.core.graphics.framebuffer.FullScreenFrameBufferResult;
import de.macbury.expanse.core.input.InputManager;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.math.Interpolation.*;

/**
 * This manages all stuff with in game interface
 * https://github.com/kotcrab/VisEditor/wiki/VisUI
 */
public class Hud extends Stage implements Telegraph {
  private static final String SKIN_FILE = "ui/ui.json";
  private final AnimatedImage loader;
  private FullScreenFrameBufferResult fullScreenFrameBufferResult;
  private Overlay overlay;
  private Skin skin;
  private Assets assets;
  private InputManager input;
  private Vector3 tempVec = new Vector3();
  public Hud(InputManager input, Assets assets, FrameBufferManager fb, Messages messages) {
    super(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    this.input  = input;
    this.assets = assets;

    if (!assets.isLoaded(SKIN_FILE, Skin.class)) {
      assets.load(SKIN_FILE, Skin.class);
      assets.finishLoadingAsset(SKIN_FILE);
    }

    skin = assets.get(SKIN_FILE, Skin.class);

    input.addProcessor(this);

    this.fullScreenFrameBufferResult = new FullScreenFrameBufferResult(Fbo.FinalResult, fb);
    addActor(fullScreenFrameBufferResult);

    this.overlay        = new Overlay();
    addActor(overlay);

    DebugLabel debugLabel = new DebugLabel(skin);
    debugLabel.setAlignment(Align.left);
    debugLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
    addActor(debugLabel);

    this.loader = new AnimatedImage(new Animation(0.05f, skin.getAtlas().findRegions("loader")));
    loader.setPosition( Gdx.graphics.getWidth() - 84, 20);
    addActor(loader);
    loader.setVisible(false);
  }


  public Overlay getOverlay() {
    return overlay;
  }

  @Override
  public void dispose() {
    super.dispose();

    assets.unload(SKIN_FILE);
    assets = null;
    input.removeProcessor(this);
    input = null;
    skin  = null;
  }

  public Skin getSkin() {
    return skin;
  }

  /**
   * Simple loading guage showed in corner of the screen
   * @return
   */
  public AnimatedImage getLoader() {
    return loader;
  }

  public FullScreenFrameBufferResult getFullScreenFrameBufferResult() {
    return fullScreenFrameBufferResult;
  }

  public void showLoading() {
    loader.setVisible(true);
    fullScreenFrameBufferResult.setVisible(false);
  }

  public void hideLoading() {
    loader.setVisible(false);
    fullScreenFrameBufferResult.setVisible(true);
  }

  @Override
  public boolean handleMessage(Telegram msg) {
    return false;
  }
}
