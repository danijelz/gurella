package com.gurella.engine.graph.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gurella.engine.graph.event.EventTrigger.NopEventTrigger;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface EventCallback {
	String id() default "";
	
	boolean marker() default false;

	Class<? extends EventTrigger> trigger() default NopEventTrigger.class;
}
