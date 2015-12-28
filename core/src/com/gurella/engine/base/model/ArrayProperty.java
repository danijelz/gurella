package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.base.container.InitializationContext;

public class ArrayProperty<T> extends ReflectionProperty<T> {
	public ArrayProperty(Field field) {
		super(field);
	}
	
	public ArrayProperty(Field field, Method getter, Method setter) {
		super(field, getter, setter);
	}

	@Override
	public void init(InitializationContext<?> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			Object template = context.template;
			if (template != null) {
				Model<? extends Object> model = Models.getModel(template.getClass());
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
