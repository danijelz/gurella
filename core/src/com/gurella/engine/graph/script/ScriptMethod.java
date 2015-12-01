package com.gurella.engine.graph.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface ScriptMethod {
	boolean marker() default false;

	Class<? extends ScriptMethodDecorator> decorator() default ScriptMethodDecorator.class;
}
