package com.gurella.engine.editor.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target(value = { ElementType.FIELD })
public @interface PropertyEditor {
	Class<? extends PropertyEditorFactory> factory();
}
