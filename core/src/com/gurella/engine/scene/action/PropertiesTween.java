package com.gurella.engine.scene.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Date;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;

public class PropertiesTween<T> implements Tween, Poolable {
	private static final ObjectSet<Class<?>> tweenableTypes = new ObjectSet<Class<?>>();
	private static final IdentityMap<Class<?>, Accessor<?>> accessorsByType = new IdentityMap<Class<?>, Accessor<?>>();

	static {
		registerAccessor(byte.class, ByteAccessor.instance);
		registerAccessor(Byte.class, ByteAccessor.instance);
		registerAccessor(char.class, CharAccessor.instance);
		registerAccessor(Character.class, CharAccessor.instance);
		registerAccessor(short.class, ShortAccessor.instance);
		registerAccessor(Short.class, ShortAccessor.instance);
		registerAccessor(int.class, IntAccessor.instance);
		registerAccessor(Integer.class, IntAccessor.instance);
		registerAccessor(long.class, LongAccessor.instance);
		registerAccessor(Long.class, LongAccessor.instance);
		registerAccessor(float.class, FloatAccessor.instance);
		registerAccessor(Float.class, FloatAccessor.instance);
		registerAccessor(double.class, DoubleAccessor.instance);
		registerAccessor(Double.class, DoubleAccessor.instance);
		registerAccessor(Date.class, DateAccessor.instance);
		registerAccessor(boolean.class, BooleanAccessor.instance);
		registerAccessor(Boolean.class, BooleanAccessor.instance);
		registerAccessor(BigInteger.class, BigIntegerAccessor.instance);
		registerAccessor(BigDecimal.class, BigDecimalAccessor.instance);
	}

	private static <T> void registerAccessor(Class<T> type, Accessor<T> accessor) {
		tweenableTypes.add(type);
		accessorsByType.put(type, accessor);
	}

	private T target;

	// TODO cache data by class
	private final ArrayExt<Property<?>> properties = new ArrayExt<Property<?>>();
	private final ArrayExt<Accessor<?>> accessors = new ArrayExt<Accessor<?>>();
	private final ArrayExt<PropertiesTween<?>> children = new ArrayExt<PropertiesTween<?>>();

	private final ArrayExt<Object> startValues = new ArrayExt<Object>();
	private final ArrayExt<Object> endValues = new ArrayExt<Object>();

	public PropertiesTween(T target, T end) {
		this(target, target, end, Models.<T> getCommonModel(target, end));
	}

	public PropertiesTween(T target, T start, T end) {
		this(target, start, end, Models.<T> getCommonModel(target, start, end));
	}

	private PropertiesTween(T target, T start, T end, Model<T> model) {
		this.target = target;
		ImmutableArray<Property<?>> allProperties = model.getProperties();
		for (int i = 0, n = allProperties.size(); i < n; i++) {
			Property<?> property = allProperties.get(i);
			Object startValue = property.getValue(start);
			Object endValue = property.getValue(end);
			if (startValue != null && endValue != null) {
				appendProperty(target, property, startValue, endValue);
			}
		}
	}

	protected void appendProperty(T target, Property<?> property, Object startValue, Object endValue) {
		Class<?> type = property.getType();
		if (tweenableTypes.contains(type)) {
			if (Values.isNotEqual(startValue, endValue)) {
				properties.add(property);
				accessors.add(accessorsByType.get(type));
				startValues.add(startValue);
				endValues.add(endValue);
			}
		} else {
			initChild(property.getValue(target), startValue, endValue);
		}
	}

	private <P> void initChild(P target, P start, P end) {
		if (target == null || start == null || end == null) {
			return;
		}

		Model<P> model = Models.<P> getCommonModel(target, end);
		if (model.getProperties().size() == 0) {
			return;
		}

		children.add(new PropertiesTween<P>(target, start, end));
	}

	@Override
	public void update(float percent) {
		for (int i = 0, n = properties.size; i < n; i++) {
			update(percent, i);
		}
		for (int i = 0, n = children.size; i < n; i++) {
			children.get(i).update(percent);
		}
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
		return getDiagnostics(0);
	}

	public String getDiagnostics(int level) {
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < level; j++) {
			builder.append(" ");
		}

		for (int i = 0, n = properties.size; i < n; i++) {
			Property<?> property = properties.get(i);
			builder.append(property.getName());
			builder.append(": ");
			builder.append(property.getValue(target));
			if (i < n - 1) {
				builder.append(", ");
			}
		}

		for (int i = 0, n = children.size; i < n; i++) {
			builder.append(children.get(i).getDiagnostics(level + 1));
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
			property.setValue(target, Byte.valueOf((byte) Math.round(start + (end - start) * percent)));
		}
	}

	private static class CharAccessor implements Accessor<Character> {
		private static final CharAccessor instance = new CharAccessor();

		@Override
		public void update(Object target, Property<Character> property, Character startValue, Character endValue,
				float percent) {
			char start = startValue.charValue();
			char end = endValue.charValue();
			property.setValue(target, Character.valueOf((char) Math.round(start + (end - start) * percent)));
		}
	}

	private static class ShortAccessor implements Accessor<Short> {
		private static final ShortAccessor instance = new ShortAccessor();

		@Override
		public void update(Object target, Property<Short> property, Short startValue, Short endValue, float percent) {
			short start = startValue.shortValue();
			short end = endValue.shortValue();
			property.setValue(target, Short.valueOf((short) Math.round(start + (end - start) * percent)));
		}
	}

	private static class IntAccessor implements Accessor<Integer> {
		private static final IntAccessor instance = new IntAccessor();

		@Override
		public void update(Object target, Property<Integer> property, Integer startValue, Integer endValue,
				float percent) {
			int start = startValue.intValue();
			int end = endValue.intValue();
			property.setValue(target, Integer.valueOf(Math.round(start + (end - start) * percent)));
		}
	}

	private static class LongAccessor implements Accessor<Long> {
		private static final LongAccessor instance = new LongAccessor();

		@Override
		public void update(Object target, Property<Long> property, Long startValue, Long endValue, float percent) {
			long start = startValue.longValue();
			long end = endValue.longValue();
			property.setValue(target, Long.valueOf(Math.round(start + (end - start) * percent)));
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
			// TODO updates start value
			Date currentValue = property.getValue(target);
			currentValue.setTime(Math.round(start + (end - start) * percent));
			property.setValue(target, currentValue);
		}
	}

	private static class BooleanAccessor implements Accessor<Boolean> {
		private static final BooleanAccessor instance = new BooleanAccessor();

		@Override
		public void update(Object target, Property<Boolean> property, Boolean startValue, Boolean endValue,
				float percent) {
			property.setValue(target, percent > 0.5f ? endValue : startValue);
		}
	}

	private static class BigIntegerAccessor implements Accessor<BigInteger> {
		private static final BigIntegerAccessor instance = new BigIntegerAccessor();

		@Override
		public void update(Object target, Property<BigInteger> property, BigInteger startValue, BigInteger endValue,
				float percent) {
			BigInteger result = new BigDecimal(startValue)
					.add(new BigDecimal(endValue.subtract(startValue)).multiply(new BigDecimal(percent)))
					.round(MathContext.UNLIMITED).toBigInteger();
			property.setValue(target, result);
		}
	}

	private static class BigDecimalAccessor implements Accessor<BigDecimal> {
		private static final BigDecimalAccessor instance = new BigDecimalAccessor();

		@Override
		public void update(Object target, Property<BigDecimal> property, BigDecimal startValue, BigDecimal endValue,
				float percent) {
			BigDecimal result = endValue.subtract(startValue).multiply(new BigDecimal(percent)).add(startValue);
			property.setValue(target, result);
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
