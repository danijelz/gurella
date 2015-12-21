package com.gurella.engine.base.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface Property {
	//TODO rename
	@SuppressWarnings("rawtypes")
	Class<? extends MetaProperty> model() default ReflectionMetaProperty.class;

	boolean nullable() default true;

	String descriptiveName() default "";

	String description() default "";

	String group() default "";
}

