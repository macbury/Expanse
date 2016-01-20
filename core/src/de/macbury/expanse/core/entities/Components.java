package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.ComponentMapper;
import de.macbury.expanse.core.entities.components.*;

/**
 * This class contains {@link ComponentMapper} for each component
 */
public class Components {
  public final static ComponentMapper<TimerComponent> Timer                           = ComponentMapper.getFor(TimerComponent.class);
  public final static ComponentMapper<PositionComponent> Position                     = ComponentMapper.getFor(PositionComponent.class);
  public final static ComponentMapper<RobotStateComponent> RobotState                 = ComponentMapper.getFor(RobotStateComponent.class);
  public final static ComponentMapper<RobotScriptComponent>   RobotScript             = ComponentMapper.getFor(RobotScriptComponent.class);
  public final static ComponentMapper<TextureComponent>  Texture                      = ComponentMapper.getFor(TextureComponent.class);
  public final static ComponentMapper<SpriteComponent>   Sprite                       = ComponentMapper.getFor(SpriteComponent.class);

}
