package com.gurella.engine.base.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface PropertyDescriptor {
	// TODO rename
	@SuppressWarnings("rawtypes")
	Class<? extends Property> property() default ReflectionProperty.class;

	boolean nullable() default true;
	
	boolean copyable() default true;
	
	boolean flat() default false;

	String descriptiveName() default "";

	String description() default "";

	String group() default "";
}
