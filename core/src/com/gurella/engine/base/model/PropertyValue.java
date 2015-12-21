package com.gurella.engine.base.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface PropertyValue {
	String name();

	boolean booleanValue() default false;

	int integerValue() default 0;

	long longValue() default 0;

	float floatValue() default 0;

	short shortValue() default 0;

	byte byteValue() default 0;

	double doubleValue() default 0;

	char charValue() default 0;

	int enumOrdinal() default 0;

	String stringValue() default "";

	ValueRange range() default @ValueRange;
}