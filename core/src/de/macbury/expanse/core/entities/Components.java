package de.macbury.expanse.core.entities;

import com.badlogic.ashley.core.ComponentMapper;
import de.macbury.expanse.core.entities.components.PositionComponent;
import de.macbury.expanse.core.entities.components.RobotComponent;

/**
 * This class contains {@link ComponentMapper} for each component
 */
public class Components {
  public final static ComponentMapper<PositionComponent> Position                     = ComponentMapper.getFor(PositionComponent.class);
  public final static ComponentMapper<RobotComponent>    Robot                        = ComponentMapper.getFor(RobotComponent.class);
}
