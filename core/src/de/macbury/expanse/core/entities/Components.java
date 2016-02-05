package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.ComponentMapper;
import de.macbury.expanse.core.entities.components.*;

/**
 * This class contains {@link ComponentMapper} for each component
 */
public class Components {
  public final static ComponentMapper<TimerComponent> Timer                           = ComponentMapper.getFor(TimerComponent.class);
  public final static ComponentMapper<PositionComponent> Position                     = ComponentMapper.getFor(PositionComponent.class);
  public final static ComponentMapper<RobotInstructionStateComponent> RobotInstructionState = ComponentMapper.getFor(RobotInstructionStateComponent.class);
  public final static ComponentMapper<RobotScriptComponent>   RobotScript             = ComponentMapper.getFor(RobotScriptComponent.class);
  public final static ComponentMapper<TextureComponent>  Texture                      = ComponentMapper.getFor(TextureComponent.class);
  public final static ComponentMapper<SpriteComponent>   Sprite                       = ComponentMapper.getFor(SpriteComponent.class);

  public final static ComponentMapper<MotorComponent>   Motor                         = ComponentMapper.getFor(MotorComponent.class);
  public final static ComponentMapper<BodyComponent>   Body                           = ComponentMapper.getFor(BodyComponent.class);

  public final static ComponentMapper<ModelComponent>   Model                         = ComponentMapper.getFor(ModelComponent.class);
  public final static ComponentMapper<TerrainRenderableComponent>   TerrainRenderable                         = ComponentMapper.getFor(TerrainRenderableComponent.class);
  public final static ComponentMapper<StaticComponent>   Static                         = ComponentMapper.getFor(StaticComponent.class);
}
