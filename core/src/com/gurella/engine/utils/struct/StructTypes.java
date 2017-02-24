package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.utils.Values;

public class StructTypes {
	private static final ObjectMap<Class<? extends Struct>, StructType> types = new ObjectMap<Class<? extends Struct>, StructType>();
	
	private StructTypes() {
	}

	public static StructType get(Struct struct) {
		Class<? extends Struct> type = struct.getClass();
		return get(type);
	}

	private static StructType get(Class<? extends Struct> type) {
		StructType structType = types.get(type);
		if(structType == null) {
			structType = create(type);
			types.put(type, structType);
		}
		return structType;
	}

	private static StructType create(Class<? extends Struct> type) {
		StructType structType = new StructType();
		
		Class<?> superclass = type.getSuperclass();
		if(Struct.class.isAssignableFrom(superclass)) {
			StructType superType = get(Values.<Class<? extends Struct>>cast(superclass));
			structType._properties.addAll(superType._properties);
		}
		
		Field[] fields = ClassReflection.getDeclaredFields(type);
		for (int i = 0, n = fields.length; i < n; i++) {
			Field field = fields[i];
			if(isStructProperty(field)) {
				//TODO structType._properties.add(field.get(obj));
			}
		}
		
		// TODO Auto-generated method stub
		return structType;
	}

	private static boolean isStructProperty(Field field) {
		// TODO Auto-generated method stub
		return false;
	}
}
