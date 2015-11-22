package com.gurella.engine.resource.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD })
public @interface ResourceProperty {
	Class<? extends ResourceModelProperty> model() default ReflectionResourceModelProperty.class;

	boolean nullable() default true;

	String descriptiveName() default "";

	String description() default "";

	String group() default "";
}
