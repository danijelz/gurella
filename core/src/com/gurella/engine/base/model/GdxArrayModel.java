package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;

public class GdxArrayModel implements Model<Array<?>> {
	private static final GdxArrayModel instance = new GdxArrayModel();

	private ArrayExt<Property<?>> properties;

	public static GdxArrayModel getInstance() {
		return instance;
	}

	private GdxArrayModel() {
		properties = new ArrayExt<Property<?>>();
		properties.add(new ArrayItemsProperty(this));
	}

	@Override
	public String getName() {
		return Array.class.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Array<?>> getType() {
		Class<?> type = Array.class;
		return (Class<Array<?>>) type;
	}

	@Override
	public Array<?> newInstance(InitializationContext<Array<?>> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			Array<?> template = context.template;
			if (template == null) {
				return null;
			}

			return ReflectionUtils.newInstance(template.getClass());
		} else {
			if (serializedValue.isNull()) {
				return null;
			}

			return ReflectionUtils.newInstance(Serialization.resolveObjectType(Array.class, serializedValue));
		}
	}

	@Override
	public void initInstance(InitializationContext<Array<?>> context) {
		Array<?> initializingObject = context.initializingObject;
		if (initializingObject != null) {
			properties.get(0).init(context);
		}
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return properties.immutable();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> Property<P> getProperty(String name) {
		return (Property<P>) ("items".equals(name) ? properties.get(0) : null);
	}

	@Override
	public void serialize(Array<?> object, Class<?> knownType, Archive archive) {
		archive.writeObjectStart(object, knownType);
		((ArrayItemsProperty) properties.get(0)).serialize(object, archive);
		archive.writeObjectEnd();
	}

	private static class ArrayItemsProperty implements Property<Array<?>> {
		private Model<?> model;

		public ArrayItemsProperty(Model<?> model) {
			this.model = model;
		}

		@Override
		public String getName() {
			return "items";
		}

		@Override
		@SuppressWarnings("unchecked")
		public Class<Array<?>> getType() {
			Class<?> raw = Array.class;
			return (Class<Array<?>>) raw;
		}

		@Override
		public Model<?> getModel() {
			return model;
		}
		
		@Override
		public Property<Array<?>> copy(Model<?> model) {
			return new ArrayItemsProperty(model);
		}

		@Override
		public Range<?> getRange() {
			return null;
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public String getDescriptiveName() {
			return getName();
		}

		@Override
		public String getDescription() {
			return "";
		}

		@Override
		public String getGroup() {
			return null;
		}

		@Override
		public void init(InitializationContext<?> context) {
			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) context.initializingObject;
			if (array == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue == null ? null : context.serializedValue.get("items");

			if (serializedValue == null) {
				@SuppressWarnings("unchecked")
				Array<Object> template = (Array<Object>) context.template;
				for (int i = 0; i < template.size; i++) {
					Object value = ArrayReflection.get(template, i);
					array.add(Objects.copyValue(value, context));
				}
			} else {
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					if (serializedValue.isNull()) {
						array.add(null);
						continue;
					}

					Class<?> resolvedType = Serialization.resolveObjectType(Object.class, item);
					if (Serialization.isSimpleType(resolvedType)) {
						array.add(context.json.readValue(resolvedType, null, item));
					} else if (ClassReflection.isAssignableFrom(AssetReference.class, resolvedType)) {
						AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
						array.add(context.getAsset(assetReference));
					} else if (ClassReflection.isAssignableFrom(ObjectReference.class, resolvedType)) {
						ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
						array.add(context.getInstance(objectReference.getId()));
					} else {
						array.add(Objects.deserialize(serializedValue, resolvedType, context));
					}
				}
			}
		}

		@Override
		public Array<?> getValue(Object object) {
			return (Array<?>) object;
		}

		@Override
		public void setValue(Object object, Array<?> value) {
			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) object;
			array.clear();
			array.addAll(value);
		}

		@Override
		public void serialize(Object object, Archive archive) {
			Array<?> array = (Array<?>) object;
			archive.writeArrayStart("items");
			for (int i = 0; i < array.size; i++) {
				archive.writeValue(array.get(i), Object.class);
			}
			archive.writeArrayEnd();
		}
	}
}
