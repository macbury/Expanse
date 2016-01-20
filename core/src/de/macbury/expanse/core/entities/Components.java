package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.ComponentMapper;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.RobotComponent;
import de.macbury.expanse.core.entities.components.SpriteComponent;
import de.macbury.expanse.core.entities.components.TextureComponent;

/**
 * This class contains {@link ComponentMapper} for each component
 */
public class Components {
  public final static ComponentMapper<PositionComponent> Position                     = ComponentMapper.getFor(PositionComponent.class);
  public final static ComponentMapper<RobotComponent>    Robot                        = ComponentMapper.getFor(RobotComponent.class);
  public final static ComponentMapper<TextureComponent>  Texture                      = ComponentMapper.getFor(TextureComponent.class);
  public final static ComponentMapper<SpriteComponent>   Sprite                       = ComponentMapper.getFor(SpriteComponent.class);
}
