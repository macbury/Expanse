package de.macbury.expanse.core.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import de.macbury.expanse.core.graphics.LodRenderableProvider;

/**
 * This component wraps {@link ModelInstance}
 */
public abstract class RenderableComponent implements Component, Pool.Poolable, LodRenderableProvider {

}
