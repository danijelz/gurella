package com.gurella.engine.base.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface PropertyEditor {
	boolean editable() default true;

	String group() default "";
}
