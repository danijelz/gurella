package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.base.container.InitializationContext;

public class ArrayMetaProperty<T> extends ReflectionMetaProperty<T> {
	public ArrayMetaProperty(Field field, Method getter, Method setter) {
		super(field, getter, setter);
	}

	public ArrayMetaProperty(Field field) {
		super(field);
	}

	@Override
	public void init(InitializationContext<?> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			Object template = context.template;
			if (template != null) {
				MetaModel<? extends Object> model = ModelUtils.getModel(template.getClass());
				initValue(context.initializingObject, getValue(template));
			} else if (initByDefaultValue) {
				initValue(context.initializingObject, getDefaultValue());
			}
		} else {
			JsonValue serializedPropertyValue = serializedValue.get(name);
			if (serializedPropertyValue != null) {

			} else if (initByDefaultValue) {
				initValue(context.initializingObject, getDefaultValue());
			}
		}
	}
}
