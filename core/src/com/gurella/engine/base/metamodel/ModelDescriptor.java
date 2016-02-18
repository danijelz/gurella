package com.gurella.engine.base.metamodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface ModelDescriptor {
	// TODO rename
	@SuppressWarnings("rawtypes")
	Class<? extends Model> model() default ReflectionModel.class;

	String descriptiveName() default "";

	String description() default "";
}
