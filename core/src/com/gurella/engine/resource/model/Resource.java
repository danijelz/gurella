package com.gurella.engine.resource.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface Resource {
	@SuppressWarnings("rawtypes")
	Class<? extends ResourceModel> model() default ReflectionResourceModel.class;

	String descriptiveName() default "";

	String description() default "";

	String group() default "";
}
