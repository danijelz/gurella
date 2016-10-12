package com.gurella.engine.editor.property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface PropertyEditorDescriptor {
	@SuppressWarnings("rawtypes")
	Class<? extends PropertyEditorFactory> factory() default PropertyEditorFactory.class;

	EditorType type() default EditorType.composite;

	boolean editable() default true; // TODO unused

	boolean nullable() default false; // TODO unused

	String group() default "";

	int index() default 0;

	public enum EditorType {
		simple, composite, custom
	}
}
