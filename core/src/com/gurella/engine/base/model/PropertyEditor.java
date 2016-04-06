package com.gurella.engine.base.model;

public @interface PropertyEditor {
	boolean editorEnabled() default true;
	
	String group() default "";
}
