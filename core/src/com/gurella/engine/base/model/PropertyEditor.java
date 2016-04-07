package com.gurella.engine.base.model;

public @interface PropertyEditor {
	boolean editable() default true;
	
	String group() default "";
}
