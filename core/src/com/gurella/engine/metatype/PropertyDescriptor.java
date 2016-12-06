package com.gurella.engine.metatype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface PropertyDescriptor {
	@SuppressWarnings("rawtypes")
	Class<? extends Property> property() default ReflectionProperty.class;

	boolean nullable() default true;

	boolean copyable() default true;

	boolean flatSerialization() default false;
}
