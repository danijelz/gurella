package com.gurella.engine.metatype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface ValueRange {
	IntegerRange integerRange() default @IntegerRange;

	LongRange longRange() default @LongRange;

	FloatRange floatRange() default @FloatRange;

	ShortRange shortRange() default @ShortRange;

	ByteRange byteRange() default @ByteRange;

	DoubleRange doubleRange() default @DoubleRange;

	CharRange charRange() default @CharRange;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface IntegerRange {
		int min() default Integer.MIN_VALUE;

		int max() default Integer.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface LongRange {
		long min() default Long.MIN_VALUE;

		long max() default Long.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface FloatRange {
		float min() default Float.MIN_VALUE;

		float max() default Float.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface DoubleRange {
		double min() default Double.MIN_VALUE;

		double max() default Double.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface ShortRange {
		short min() default Short.MIN_VALUE;

		short max() default Short.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface ByteRange {
		byte min() default Byte.MIN_VALUE;

		byte max() default Byte.MAX_VALUE;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface CharRange {
		char min() default Character.MIN_VALUE;

		char max() default Character.MAX_VALUE;
	}
}
