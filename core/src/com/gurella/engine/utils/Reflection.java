package com.gurella.engine.utils;

import java.lang.annotation.Annotation;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.async.AsyncService;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;

//TODO caches
public class Reflection {
	// private static final ObjectMap<String, Class<?>> classesByName = new ObjectMap<String, Class<?>>();
	// private static final ObjectMap<Class<?>, Constructor> constructorsByClass = new ObjectMap<Class<?>,
	// Constructor>();

	private static final IdentityMap<Application, ClassResolver> resolvers = new IdentityMap<Application, ClassResolver>();

	private static ClassResolver singleton;
	private static ClassResolver lastSelected;
	private static Application lastApp;

	public static ClassResolver getClassResolver() {
		if (singleton != null) {
			return singleton;
		}

		synchronized (resolvers) {
			if (!AsyncService.isMultiApplicationEnvironment()) {
				singleton = DefaultClassResolver.instance;
				return singleton;
			}

			Application app = AsyncService.getCurrentApplication();
			if (lastApp == app) {
				return lastSelected;
			}

			ClassResolver resolver = resolvers.get(app, DefaultClassResolver.instance);
			lastApp = app;
			lastSelected = resolver;

			return resolver;
		}
	}

	static void setClassResolver(ClassResolver resolver) {
		ClassResolver oldResolver;

		synchronized (resolvers) {
			oldResolver = resolvers.put(AsyncService.getCurrentApplication(), resolver);

			if (lastSelected != null && lastSelected == oldResolver) {
				lastSelected = resolver == null ? DefaultClassResolver.instance : resolver;
			}
		}

		if (oldResolver == null) {
			EventService.subscribe(new Cleaner());
		}
	}

	private Reflection() {
	}

	public static <T> Class<T> forName(String className) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> resourceType = (Class<T>) getClassResolver().forName(className);
			return resourceType;
		} catch (Exception e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> Class<T> forNameSilently(String className) {
		try {
			return forName(className);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T newInstance(Class<T> type) {
		try {
			Constructor constructor = ClassReflection.getDeclaredConstructor(type);
			constructor.setAccessible(true);
			@SuppressWarnings("unchecked")
			T instance = (T) constructor.newInstance();
			return instance;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T newInstanceSilently(Class<T> type) {
		try {
			return newInstance(type);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T newInstance(Class<T> type, Class<?>[] parameterTypes, Object... parameters) {
		try {
			Constructor constructor = ClassReflection.getDeclaredConstructor(type, parameterTypes);
			constructor.setAccessible(true);
			@SuppressWarnings("unchecked")
			T instance = (T) constructor.newInstance(parameters);
			return instance;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T newInstanceSilently(Class<T> type, Class<?>[] parameterTypes, Object... parameters) {
		try {
			return newInstance(type, parameterTypes, parameters);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T newInstance(Class<T> type, Class<?> parameterType, Object parameter) {
		try {
			Constructor constructor = ClassReflection.getDeclaredConstructor(type, parameterType);
			constructor.setAccessible(true);
			@SuppressWarnings("unchecked")
			T instance = (T) constructor.newInstance(parameter);
			return instance;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T newInstanceSilently(Class<T> type, Class<?> parameterType, Object parameter) {
		try {
			return newInstance(type, parameterType, parameter);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isInnerClass(Class<?> type) {
		return !type.isPrimitive() && ClassReflection.isMemberClass(type) && !ClassReflection.isStaticClass(type);
	}

	public static <T> T newInnerClassInstance(Class<T> type, Object enclosingInstance) {
		try {
			Constructor constructor = findInnerClassDeclaredConstructor(type, enclosingInstance);
			constructor.setAccessible(true);
			@SuppressWarnings("unchecked")
			T instance = (T) constructor.newInstance(enclosingInstance);
			return instance;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Constructor findInnerClassDeclaredConstructor(Class<?> type, Object enclosingInstance) {
		Class<?> enclosingInstanceType = enclosingInstance.getClass();
		while (enclosingInstanceType != Object.class) {
			Constructor constructor = getInnerClassDeclaredConstructorSilently(type, enclosingInstanceType);
			if (constructor != null) {
				return constructor;
			}
			enclosingInstanceType = enclosingInstanceType.getSuperclass();
		}
		return null;
	}

	public static <T> T newInstance(String className) {
		return newInstance(Reflection.<T> forName(className));
	}

	public static <T> T newInstanceSilently(String className) {
		try {
			return newInstance(Reflection.<T> forName(className));
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor findConstructor(Class<?> c, Class<?>... parameterTypes) {
		Constructor constructor = getConstructorSilently(c, parameterTypes);
		if (constructor == null) {
			constructor = getDeclaredConstructor(c, parameterTypes);
			constructor.setAccessible(true);
		}
		return constructor;
	}

	public static Constructor findConstructorSilently(Class<?> c, Class<?>... parameterTypes) {
		try {
			return findConstructor(c, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor getConstructor(Class<?> c, Class<?>... parameterTypes) {
		try {
			return ClassReflection.getConstructor(c, parameterTypes);
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Constructor getConstructorSilently(Class<?> c, Class<?>... parameterTypes) {
		try {
			return getConstructor(c, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor getDeclaredConstructor(Class<?> c, Class<?>... parameterTypes) {
		try {
			return ClassReflection.getDeclaredConstructor(c, parameterTypes);
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Constructor getDeclaredConstructorSilently(Class<?> c, Class<?>... parameterTypes) {
		try {
			return getDeclaredConstructor(c, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Constructor getInnerClassDeclaredConstructor(Class<?> c, Class<?> enclosingClass,
			Class<?>... parameterTypes) {
		try {
			if (parameterTypes == null) {
				return ClassReflection.getDeclaredConstructor(c, enclosingClass);
			} else {
				int length = parameterTypes.length;
				@SuppressWarnings("rawtypes")
				Class[] allParameterTypes = new Class[length + 1];
				allParameterTypes[0] = enclosingClass;
				System.arraycopy(parameterTypes, 0, allParameterTypes, 1, length);
				return ClassReflection.getDeclaredConstructor(c, allParameterTypes);
			}
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Constructor getInnerClassDeclaredConstructorSilently(Class<?> c, Class<?> enclosingClass,
			Class<?>... parameterTypes) {
		try {
			return getInnerClassDeclaredConstructor(c, enclosingClass, parameterTypes);
		} catch (Exception e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Method getDeclaredMethod(Class<?> c, String name, Class<?>... parameterTypes) {
		try {
			return ClassReflection.getDeclaredMethod(c, name, parameterTypes);
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Method getDeclaredMethodSilently(Class<?> c, String name, Class<?>... parameterTypes) {
		try {
			return ClassReflection.getDeclaredMethod(c, name, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getMethod(Class<?> c, String name, Class<?>... parameterTypes) {
		try {
			return ClassReflection.getMethod(c, name, parameterTypes);
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Method getMethodSilently(Class<?> c, String name, Class<?>... parameterTypes) {
		try {
			return ClassReflection.getMethod(c, name, parameterTypes);
		} catch (Exception e) {
			return null;
		}
	}

	public static Field[] getDeclaredFields(Class<?> c) {
		return ClassReflection.getDeclaredFields(c);
	}

	public static Field getDeclaredField(Class<?> c, String name) {
		try {
			return ClassReflection.getDeclaredField(c, name);
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static Field getDeclaredFieldSilently(Class<?> c, String name) {
		try {
			return ClassReflection.getDeclaredField(c, name);
		} catch (Exception e) {
			return null;
		}
	}

	public static Field getField(Class<?> c, String name) {
		try {
			return ClassReflection.getField(c, name);
		} catch (ReflectionException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Field getFieldSilently(Class<?> c, String name) {
		try {
			return ClassReflection.getField(c, name);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean hasDeclaredAnnotation(Class<?> owner, Class<? extends Annotation> annotationType) {
		com.badlogic.gdx.utils.reflect.Annotation annotation = ClassReflection.getDeclaredAnnotation(owner,
				annotationType);
		return annotation != null;
	}

	public static <T extends Annotation> T getDeclaredAnnotation(Class<?> owner, Class<T> annotationType) {
		com.badlogic.gdx.utils.reflect.Annotation annotation = ClassReflection.getDeclaredAnnotation(owner,
				annotationType);
		return annotation == null ? null : annotation.getAnnotation(annotationType);
	}

	public static boolean hasAnnotation(Class<?> owner, Class<? extends Annotation> annotationType) {
		return getAnnotation(owner, annotationType) != null;
	}

	public static <T extends Annotation> T getAnnotation(Class<?> owner, Class<T> annotationType) {
		Class<?> temp = owner;
		while (temp != null && !Object.class.equals(temp)) {
			T annotation = getDeclaredAnnotation(temp, annotationType);
			if (annotation != null) {
				return annotation;
			} else {
				temp = temp.getSuperclass();
			}
		}
		return null;
	}

	public static boolean hasDeclaredAnnotation(Field owner, Class<? extends Annotation> annotationType) {
		com.badlogic.gdx.utils.reflect.Annotation annotation = owner.getDeclaredAnnotation(annotationType);
		return annotation != null;
	}

	public static <T extends Annotation> T getDeclaredAnnotation(Field owner, Class<T> annotationType) {
		com.badlogic.gdx.utils.reflect.Annotation annotation = owner.getDeclaredAnnotation(annotationType);
		return annotation == null ? null : annotation.getAnnotation(annotationType);
	}

	public static <T extends Annotation> T getDeclaredAnnotation(Method owner, Class<T> annotationType) {
		com.badlogic.gdx.utils.reflect.Annotation annotation = owner.getDeclaredAnnotation(annotationType);
		return annotation == null ? null : annotation.getAnnotation(annotationType);
	}

	public static void setFieldValue(String fieldName, Object object, Object value) {
		setFieldValue(getField(object.getClass(), fieldName), object, value);
	}

	public static void setFieldValue(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T getFieldValue(Field field, Object object) {
		try {
			@SuppressWarnings("unchecked")
			T casted = (T) field.get(object);
			return casted;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T invokeMethod(Method method, Object object, Object... args) {
		try {
			@SuppressWarnings("unchecked")
			T casted = (T) method.invoke(object, args);
			return casted;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T invokeMethodSilently(Method method, Object object, Object... args) {
		try {
			return invokeMethod(method, object, args);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T invokeConstructor(Constructor constructor, Object... args) {
		try {
			@SuppressWarnings("unchecked")
			T casted = (T) constructor.newInstance(args);
			return casted;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T invokeConstructorSilently(Constructor constructor, Object... args) {
		try {
			return invokeConstructor(constructor, args);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T invokeConstructor(Constructor constructor) {
		try {
			@SuppressWarnings("unchecked")
			T casted = (T) constructor.newInstance((Object[]) null);
			return casted;
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	public static <T> T invokeConstructorSilently(Constructor constructor) {
		try {
			return invokeConstructor(constructor);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> Class<T> getCommonClass(Object... objects) {
		if (Values.isEmpty(objects)) {
			return null;
		}

		Class<?> currentCommon = objects[0].getClass();
		for (int i = 1, n = objects.length; i < n && currentCommon != Object.class; i++) {
			Class<?> next = objects[i].getClass();
			currentCommon = getCommonClass(currentCommon, next);
		}

		return Values.cast(currentCommon);
	}

	public static <T> Class<T> getCommonClass(final Object first, final Object second, final Object third) {
		return getCommonClass(getCommonClass(first.getClass(), second.getClass()), third.getClass());
	}

	public static <T> Class<T> getCommonClass(final Object first, final Object second) {
		return getCommonClass(first.getClass(), second.getClass());
	}

	public static <T> Class<T> getCommonClass(Class<?>... classes) {
		if (Values.isEmpty(classes)) {
			return null;
		}

		Class<?> currentCommon = classes[0];
		for (int i = 1, n = classes.length; i < n && currentCommon != Object.class; i++) {
			Class<?> next = classes[i];
			currentCommon = getCommonClass(currentCommon, next);
		}

		return Values.cast(currentCommon);
	}

	public static <T> Class<T> getCommonClass(final Class<?> first, final Class<?> second, final Class<?> third) {
		return getCommonClass(getCommonClass(first, second), third);
	}

	public static <T> Class<T> getCommonClass(final Class<?> first, final Class<?> second) {
		if (first == null || second == null) {
			return Values.cast(Object.class);
		}

		if (first == second) {
			return Values.cast(first);
		}

		Class<?> temp = first;
		while (temp != null && temp != Object.class) {
			if (temp == second) {
				return Values.cast(temp);
			}
			temp = temp.getSuperclass();
		}

		return getCommonClass(first, second.getSuperclass());
	}

	public interface ClassResolver {
		Class<?> forName(String className) throws Exception;
	}

	public static final class DefaultClassResolver implements ClassResolver {
		public static final ClassResolver instance = new DefaultClassResolver();

		private DefaultClassResolver() {
		}

		@Override
		public Class<?> forName(String className) throws Exception {
			return ClassReflection.forName(className);
		}
	}

	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void onShutdown() {
			EventService.unsubscribe(this);

			synchronized (resolvers) {
				if (resolvers.remove(AsyncService.getCurrentApplication()) == lastSelected) {
					lastSelected = null;
					lastApp = null;
				}
			}
		}
	}
}
