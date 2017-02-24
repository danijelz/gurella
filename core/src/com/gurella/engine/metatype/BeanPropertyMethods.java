package com.gurella.engine.metatype;

import static com.gurella.engine.metatype.MetaTypes.getPrefix;
import static com.gurella.engine.metatype.MetaTypes.isPrefix;
import static com.gurella.engine.metatype.MetaTypes.setPrefix;

import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.utils.Reflection;

class BeanPropertyMethods {
	Method getter;
	Method setter;

	private BeanPropertyMethods(Method getter, Method setter) {
		this.getter = getter;
		this.setter = setter;
	}

	static BeanPropertyMethods getInstance(Class<?> owner, String name) {
		String upperCaseName = name.substring(0, 1).toUpperCase() + name.substring(1);
		String setterName = setPrefix + upperCaseName;

		Method boolGetter = getBeanGetter(owner, isPrefix + upperCaseName, boolean.class);
		if (boolGetter != null) {
			Method boolSetter = getBeanSetter(owner, setterName, boolean.class);
			if (boolSetter != null) {
				return new BeanPropertyMethods(boolGetter, boolSetter);
			}
		}

		Method getter = getBeanGetter(owner, getPrefix + upperCaseName, null);
		if (getter == null) {
			return null;
		}

		Method setter = getBeanSetter(owner, setterName, getter.getReturnType());
		if (setter == null) {
			return null;
		}

		return new BeanPropertyMethods(getter, setter);
	}

	private static Method getBeanGetter(Class<?> owner, String name, Class<?> returnType) {
		Method getter = Reflection.getDeclaredMethodSilently(owner, name);
		return isValidBeanGetter(getter, returnType) ? getter : null;
	}

	private static boolean isValidBeanGetter(Method method, Class<?> returnType) {
		return method != null && (returnType == null || method.getReturnType() == returnType)
				&& isValidBeanMethod(method);
	}

	private static Method getBeanSetter(Class<?> owner, String name, Class<?> propertyType) {
		Method setter = Reflection.getDeclaredMethodSilently(owner, name, propertyType);
		return isValidBeanSetter(setter, propertyType) ? setter : null;
	}

	private static boolean isValidBeanSetter(Method method, Class<?> propertyType) {
		if (method == null) {
			return false;
		}

		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			return false;
		}

		return parameterTypes[0] == propertyType && isValidBeanMethod(method);
	}

	private static boolean isValidBeanMethod(Method method) {
		return !method.isPrivate() || method.getDeclaredAnnotation(PropertyDescriptor.class) != null;
	}

	@Override
	public String toString() {
		return "getter: '" + getter.getName() + "', setter: '" + setter.getName() + "', type: "
				+ getter.getReturnType().getName();
	}
}
