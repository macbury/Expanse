package de.macbury.expanse.core.scripts.scope;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Target(METHOD)
@Retention(RUNTIME)
public @interface ExposeAsGlobalFunction {
}
