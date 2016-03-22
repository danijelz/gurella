package com.gurella.engine.scene.action;

import java.util.Date;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class PropertiesTween<T> implements Tween, Poolable {
	private static final ArrayExt<Class<?>> tweenableTypes = ArrayExt.<Class<?>> with(byte.class, Byte.class,
			char.class, Character.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class,
			float.class, Float.class, double.class, Double.class, Date.class);
	private static final IdentityMap<Class<?>, Accessor<?>> accessorsByType = new IdentityMap<Class<?>, Accessor<?>>();

	static {
		accessorsByType.put(byte.class, ByteAccessor.instance);
		accessorsByType.put(Byte.class, ByteAccessor.instance);
		accessorsByType.put(char.class, CharAccessor.instance);
		accessorsByType.put(Character.class, CharAccessor.instance);
		accessorsByType.put(short.class, ShortAccessor.instance);
		accessorsByType.put(Short.class, ShortAccessor.instance);
		accessorsByType.put(int.class, IntAccessor.instance);
		accessorsByType.put(Integer.class, IntAccessor.instance);
		accessorsByType.put(long.class, LongAccessor.instance);
		accessorsByType.put(Long.class, LongAccessor.instance);
		accessorsByType.put(float.class, FloatAccessor.instance);
		accessorsByType.put(Float.class, FloatAccessor.instance);
		accessorsByType.put(double.class, DoubleAccessor.instance);
		accessorsByType.put(Double.class, DoubleAccessor.instance);
		accessorsByType.put(Date.class, DateAccessor.instance);
	}

	private T target;

	private final ArrayExt<Property<?>> properties = new ArrayExt<Property<?>>();
	private final ArrayExt<Accessor<?>> accessors = new ArrayExt<Accessor<?>>();
	private final ArrayExt<Object> startValues = new ArrayExt<Object>();
	private final ArrayExt<Object> endValues = new ArrayExt<Object>();

	public PropertiesTween(T target, T end) {
		this(target, target, end, Models.<T>getCommonModel(target, end));
	}

	public PropertiesTween(T target, T start, T end) {
		this(target, start, end, Models.<T>getCommonModel(target, start, end));
	}

	private PropertiesTween(T target, T start, T end, Model<T> model) {
		this.target = target;
		ImmutableArray<Property<?>> allProperties = model.getProperties();
		for (int i = 0, n = allProperties.size(); i < n; i++) {
			Property<?> property = allProperties.get(i);
			Class<?> type = property.getType();
			if (tweenableTypes.contains(type, true)) {
				Object startValue = property.getValue(start);
				Object endValue = property.getValue(end);
				if (Values.isNotEqual(startValue, endValue)) {
					properties.add(property);
					accessors.add(accessorsByType.get(type));
					startValues.add(startValue);
					endValues.add(endValue);
				}
			}
		}
	}

	@Override
	public void update(float percent) {
		for (int i = 0, n = properties.size; i < n; i++) {
			update(percent, i);
		}
		System.out.println(getDiagnostics());
	}

	@SuppressWarnings("unchecked")
	private <V> void update(float percent, int index) {
		Property<V> property = (Property<V>) properties.get(index);
		Accessor<V> accessor = (Accessor<V>) accessors.get(index);
		V startValue = (V) startValues.get(index);
		V endValue = (V) endValues.get(index);
		accessor.update(target, property, startValue, endValue, percent);
	}

	public String getDiagnostics() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0, n = properties.size; i < n; i++) {
			Property<?> property = properties.get(i);
			builder.append(property.getName());
			builder.append(": ");
			builder.append(property.getValue(target));
			if (i < n - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	private interface Accessor<T> {
		void update(Object target, Property<T> property, T startValue, T endValue, float percent);
	}

	private static class ByteAccessor implements Accessor<Byte> {
		private static final ByteAccessor instance = new ByteAccessor();

		@Override
		public void update(Object target, Property<Byte> property, Byte startValue, Byte endValue, float percent) {
			byte start = startValue.byteValue();
			byte end = endValue.byteValue();
			property.setValue(target, Byte.valueOf((byte) (start + (end - start) * percent)));
		}
	}

	private static class CharAccessor implements Accessor<Character> {
		private static final CharAccessor instance = new CharAccessor();

		@Override
		public void update(Object target, Property<Character> property, Character startValue, Character endValue,
				float percent) {
			char start = startValue.charValue();
			char end = endValue.charValue();
			property.setValue(target, Character.valueOf((char) (start + (end - start) * percent)));
		}
	}

	private static class ShortAccessor implements Accessor<Short> {
		private static final ShortAccessor instance = new ShortAccessor();

		@Override
		public void update(Object target, Property<Short> property, Short startValue, Short endValue, float percent) {
			short start = startValue.shortValue();
			short end = endValue.shortValue();
			property.setValue(target, Short.valueOf((short) (start + (end - start) * percent)));
		}
	}

	private static class IntAccessor implements Accessor<Integer> {
		private static final IntAccessor instance = new IntAccessor();

		@Override
		public void update(Object target, Property<Integer> property, Integer startValue, Integer endValue,
				float percent) {
			int start = startValue.intValue();
			int end = endValue.intValue();
			property.setValue(target, Integer.valueOf((int) (start + (end - start) * percent)));
		}
	}

	private static class LongAccessor implements Accessor<Long> {
		private static final LongAccessor instance = new LongAccessor();

		@Override
		public void update(Object target, Property<Long> property, Long startValue, Long endValue, float percent) {
			long start = startValue.longValue();
			long end = endValue.longValue();
			property.setValue(target, Long.valueOf((long) (start + (end - start) * percent)));
		}
	}

	private static class FloatAccessor implements Accessor<Float> {
		private static final FloatAccessor instance = new FloatAccessor();

		@Override
		public void update(Object target, Property<Float> property, Float startValue, Float endValue, float percent) {
			float start = startValue.floatValue();
			float end = endValue.floatValue();
			property.setValue(target, Float.valueOf((start + (end - start) * percent)));
		}
	}

	private static class DoubleAccessor implements Accessor<Double> {
		private static final DoubleAccessor instance = new DoubleAccessor();

		@Override
		public void update(Object target, Property<Double> property, Double startValue, Double endValue,
				float percent) {
			double start = startValue.doubleValue();
			double end = endValue.doubleValue();
			property.setValue(target, Double.valueOf((start + (end - start) * percent)));
		}
	}

	private static class DateAccessor implements Accessor<Date> {
		private static final DateAccessor instance = new DateAccessor();

		@Override
		public void update(Object target, Property<Date> property, Date startValue, Date endValue, float percent) {
			long start = startValue.getTime();
			long end = endValue.getTime();
			property.getValue(target).setTime((long) (start + (end - start) * percent));
		}
	}

	@Override
	public void reset() {
		target = null;
		properties.reset();
		accessors.reset();
		startValues.reset();
		endValues.reset();
	}
}
