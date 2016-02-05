package de.macbury.expanse.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import de.macbury.expanse.core.assets.Assets;
import de.macbury.expanse.core.graphics.camera.Overlay;
import de.macbury.expanse.core.graphics.framebuffer.Fbo;
import de.macbury.expanse.core.graphics.framebuffer.FrameBufferManager;
import de.macbury.expanse.core.graphics.framebuffer.FullScreenFrameBufferResult;
import de.macbury.expanse.core.input.InputManager;

/**
 * This manages all stuff with in game interface
 * https://github.com/kotcrab/VisEditor/wiki/VisUI
 */
public class Hud extends Stage {
  private static final String SKIN_FILE = "ui/ui.json";
  private final AnimatedImage loader;
  private FullScreenFrameBufferResult fullScreenFrameBufferResult;
  private Overlay overlay;
  private Skin skin;
  private Assets assets;
  private InputManager input;
  public Hud(InputManager input, Assets assets, FrameBufferManager fb) {
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
}
