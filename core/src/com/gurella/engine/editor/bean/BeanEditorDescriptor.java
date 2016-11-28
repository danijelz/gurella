package com.gurella.engine.editor.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(value = { ElementType.TYPE })
public @interface BeanEditorDescriptor {
	Class<? extends BeanEditorFactory<?>> factory();
}
