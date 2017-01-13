package com.gurella.studio.editor.ui.bean;

import static com.gurella.engine.utils.Values.cast;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.bean.BeanEditorDescriptor;
import com.gurella.engine.test.TestEditorComponent;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.engine.bean.CustomBeanEditor;
import com.gurella.studio.editor.engine.bean.CustomBeanEditorContextAdapter;
import com.gurella.studio.editor.ui.bean.custom.TestCustomizableBeanEditor;

public class BeanEditorFactory {
	private static final Class<?>[] customBeanEditorParameterTypes = { Composite.class, BeanEditorContext.class };
	private static final Map<Class<?>, Class<?>> defaultFactories = new HashMap<>();
	private static final Map<Class<?>, String> customFactories = new HashMap<>();

	static {
		defaultFactories.put(TestEditorComponent.class, TestCustomizableBeanEditor.class);
	}

	public static <T> BeanEditor<T> createEditor(Composite parent, SceneEditorContext context, T instance) {
		Class<CustomBeanEditor<T>> beanEditorType = Values.cast(defaultFactories.get(instance.getClass()));
		if (beanEditorType != null) {
			BeanEditorContext<T> editorContext = new BeanEditorContext<>(context, instance);
			return Reflection.newInstance(beanEditorType, customBeanEditorParameterTypes, parent, editorContext);
		}

		com.gurella.engine.editor.bean.BeanEditorFactory<T> factory = getCustomFactory(instance, context);
		if (factory == null) {
			return new DefaultBeanEditor<T>(parent, context, instance);
		} else {
			return new CustomBeanEditor<>(parent, new CustomBeanEditorContextAdapter<>(context, instance, factory));
		}
	}

	public static <T> BeanEditor<T> createEditor(Composite parent, BeanEditorContext<?> parentContext, T instance) {
		SceneEditorContext sceneContext = parentContext.sceneContext;
		Class<CustomBeanEditor<T>> beanEditorType = Values.cast(defaultFactories.get(instance.getClass()));
		if (beanEditorType != null) {
			BeanEditorContext<T> editorContext = new BeanEditorContext<>(parentContext, instance);
			return Reflection.newInstance(beanEditorType, customBeanEditorParameterTypes, parent, editorContext);
		}

		com.gurella.engine.editor.bean.BeanEditorFactory<T> factory = getCustomFactory(instance, sceneContext);
		if (factory == null) {
			return new DefaultBeanEditor<T>(parent, new BeanEditorContext<>(parentContext, instance));
		} else {
			return new CustomBeanEditor<>(parent,
					new CustomBeanEditorContextAdapter<>(parentContext, instance, factory));
		}
	}

	private static <T> com.gurella.engine.editor.bean.BeanEditorFactory<T> getCustomFactory(T bean,
			SceneEditorContext sceneContext) {
		try {
			String customFactoryClass = getCustomFactoryClass(bean, sceneContext.javaProject);
			if (customFactoryClass == null) {
				return null;
			}

			Class<?> factoryClass = Reflection.forName(customFactoryClass);
			Constructor<?> constructor = factoryClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			return cast(constructor.newInstance(new Object[0]));
		} catch (Exception e) {
			customFactories.put(bean.getClass(), null);
			return null;
		}
	}

	private static <T> String getCustomFactoryClass(T bean, IJavaProject javaProject) throws Exception {
		Class<?> beanClass = bean.getClass();
		if (customFactories.containsKey(beanClass)) {
			return customFactories.get(beanClass);
		}

		IType type = javaProject.findType(beanClass.getName());
		for (IAnnotation annotation : type.getAnnotations()) {
			if (annotation.getElementName().equals(BeanEditorDescriptor.class.getSimpleName())) {
				String metaTypeFactoryClass = parseAnnotation(type, annotation);
				customFactories.put(beanClass, metaTypeFactoryClass);
				return metaTypeFactoryClass;
			}
		}

		IAnnotation annotation = type.getAnnotation(BeanEditorDescriptor.class.getName());
		String factoryClass = annotation == null ? null : parseAnnotation(type, annotation);
		customFactories.put(beanClass, factoryClass);
		return factoryClass;
	}

	private static String parseAnnotation(IType type, IAnnotation annotation) throws JavaModelException {
		IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
		for (IMemberValuePair memberValuePair : memberValuePairs) {
			if ("factory".equals(memberValuePair.getMemberName())) {
				String[][] resolveType = type.resolveType((String) memberValuePair.getValue());
				if (resolveType.length != 1) {
					return null;
				}
				String[] path = resolveType[0];
				int last = path.length - 1;
				path[last] = path[last].replaceAll("\\.", "\\$");
				StringBuilder builder = new StringBuilder();
				for (String part : path) {
					if (builder.length() > 0) {
						builder.append(".");
					}
					builder.append(part);
				}
				return builder.toString();
			}
		}

		return null;
	}
}
