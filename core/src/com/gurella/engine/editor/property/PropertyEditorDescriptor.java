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

	EditorType type() default EditorType.composite; //TODO move to PropertyEditorFactory

	boolean editable() default true;

	boolean nullable() default false; // TODO unused

	int index() default 0;

	String descriptiveName() default "";

	String description() default "";

	String group() default "";

	boolean expandGroup() default false; //TODO unused

	public enum EditorType {
		simple, composite, custom
	}
}
