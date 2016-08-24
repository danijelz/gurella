package com.gurella.engine.editor.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target(value = { ElementType.TYPE })
public @interface ModelEditorDescriptor {
	Class<? extends ModelEditorFactory> factory();
}
