package de.macbury.expanse.core.entities.systems;

import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import de.macbury.expanse.core.entities.Components;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.SpriteComponent;
import de.macbury.expanse.core.entities.components.TextureComponent;

/**
 * This system renders all {@link Entity} with {@link SpriteBatch}. Entity must have:
 * {@link PositionComponent}
 * {@link de.macbury.expanse.core.entities.components.SpriteComponent}
 */
public class SpriteRenderingSystem extends IteratingSystem implements Disposable {
  private SpriteBatch spriteBatch;
  private Camera camera;
  private Matrix4 tempMat;

  public SpriteRenderingSystem(Camera camera) {
    super(Family.all(PositionComponent.class, SpriteComponent.class).get());
    this.spriteBatch = new SpriteBatch();
    this.camera      = camera;
    this.tempMat     = new Matrix4();
  }

  @Override
  public void dispose() {
    spriteBatch.dispose();
    spriteBatch = null;
    camera      = null;
  }

  @Override
  public void update(float deltaTime) {
    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin(); {
      super.update(deltaTime);
    } spriteBatch.end();
  }

  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    PositionComponent positionComponent = Components.Position.get(entity);
    SpriteComponent  spriteComponent    = Components.Sprite.get(entity);

    spriteComponent.setCenter(positionComponent.x, positionComponent.z);
    spriteComponent.setRotation(-positionComponent.rotationDeg);//TODO: Rotate in clockwise direction the sprite :/
    spriteComponent.draw(spriteBatch);
  }
}
