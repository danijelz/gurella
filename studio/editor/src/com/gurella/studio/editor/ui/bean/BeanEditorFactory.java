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
import com.gurella.studio.common.ReferencedTypeResolver;
import com.gurella.studio.editor.engine.bean.CustomBeanEditor;
import com.gurella.studio.editor.engine.bean.CustomBeanEditorContextAdapter;
import com.gurella.studio.editor.ui.bean.custom.TestCustomizableBeanEditor;
import com.gurella.studio.gdx.GdxContext;

public class BeanEditorFactory {
	private static final Class<?>[] customBeanEditorParameterTypes = { Composite.class, BeanEditorContext.class };
	private static final Map<Class<?>, Class<?>> defaultFactories = new HashMap<>();
	private static final Map<Class<?>, String> customFactories = new HashMap<>();

	static {
		defaultFactories.put(TestEditorComponent.class, TestCustomizableBeanEditor.class);
	}

	public static <T> BeanEditor<T> createEditor(Composite parent, int gdxContextId, T bean) {
		Class<CustomBeanEditor<T>> beanEditorType = Values.cast(defaultFactories.get(bean.getClass()));
		if (beanEditorType != null) {
			BeanEditorContext<T> editorContext = new BeanEditorContext<>(gdxContextId, bean);
			return Reflection.newInstance(beanEditorType, customBeanEditorParameterTypes, parent, editorContext);
		}

		IJavaProject javaProject = GdxContext.getJavaProject(gdxContextId);
		com.gurella.engine.editor.bean.BeanEditorFactory<T> factory = getCustomFactory(bean, javaProject);
		if (factory == null) {
			return new DefaultBeanEditor<T>(parent, gdxContextId, bean);
		} else {
			return new CustomBeanEditor<>(parent, new CustomBeanEditorContextAdapter<>(gdxContextId, bean, factory));
		}
	}

	public static <T> BeanEditor<T> createEditor(Composite parent, BeanEditorContext<?> parentContext, T bean) {
		Class<CustomBeanEditor<T>> beanEditorType = Values.cast(defaultFactories.get(bean.getClass()));
		if (beanEditorType != null) {
			BeanEditorContext<T> editorContext = new BeanEditorContext<>(parentContext, bean);
			return Reflection.newInstance(beanEditorType, customBeanEditorParameterTypes, parent, editorContext);
		}

		com.gurella.engine.editor.bean.BeanEditorFactory<T> factory = getCustomFactory(bean, parentContext.javaProject);
		if (factory == null) {
			return new DefaultBeanEditor<T>(parent, new BeanEditorContext<>(parentContext, bean));
		} else {
			return new CustomBeanEditor<>(parent, new CustomBeanEditorContextAdapter<>(parentContext, bean, factory));
		}
	}

	private static <T> com.gurella.engine.editor.bean.BeanEditorFactory<T> getCustomFactory(T bean,
			IJavaProject javaProject) {
		try {
			String customFactoryClass = getCustomFactoryClass(bean, javaProject);
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
				return ReferencedTypeResolver.resolveReferencedType(type, (String) memberValuePair.getValue());
			}
		}

		return null;
	}
}
