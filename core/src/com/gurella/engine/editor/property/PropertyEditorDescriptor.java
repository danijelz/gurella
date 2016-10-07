package com.gurella.engine.editor.property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(value = { ElementType.FIELD })
public @interface PropertyEditorDescriptor {
	Class<? extends PropertyEditorFactory<?>> factory();

	EditorType type() default EditorType.composite;

	public enum EditorType {
		simple, composite, custom
	}
}
