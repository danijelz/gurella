package com.gurella.studio.editor.model;

import java.lang.reflect.Constructor;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.model.ModelEditorDescriptor;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.engine.model.CustomModelEditor;

public class ModelEditorFactory {
	private static final Map<Class<?>, String> customFactories = new HashMap<>();

	public static <T> MetaModelEditor<T> createEditor(Composite parent, SceneEditorContext sceneContext,
			T modelInstance) {
		return createEditor(parent, new ModelEditorContext<>(sceneContext, modelInstance));
	}

	public static <T> MetaModelEditor<T> createEditor(Composite parent, ModelEditorContext<T> context) {
		MetaModelEditor<T> customEditor = createCustomEditor(parent, context);
		if (customEditor == null) {
			return new DefaultMetaModelEditor<T>(parent, context);
		} else {
			return customEditor;
		}
	}

	private static <T> MetaModelEditor<T> createCustomEditor(Composite parent, ModelEditorContext<T> context) {
		try {
			String customFactoryClass = getCustomFactoryClass(context);
			if (customFactoryClass == null) {
				return null;
			}

			URLClassLoader classLoader = context.sceneEditorContext.classLoader;
			Class<?> factoryClass = classLoader.loadClass(customFactoryClass);
			Constructor<?> constructor = factoryClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			Object factory = constructor.newInstance(new Object[0]);
			return new CustomModelEditor<>(parent, context);
		} catch (Exception e) {
			customFactories.put(context.modelInstance.getClass(), null);
			return null;
		}
	}

	private static <T> String getCustomFactoryClass(ModelEditorContext<T> context) throws Exception {
		T modelInstance = context.modelInstance;

		Class<?> modelClass = modelInstance.getClass();
		if (customFactories.containsKey(modelClass)) {
			return customFactories.get(modelClass);
		}

		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		IType type = javaProject.findType(modelClass.getName());
		for (IAnnotation annotation : type.getAnnotations()) {
			if (annotation.getElementName().equals(ModelEditorDescriptor.class.getSimpleName())) {
				String modelFactoryClass = parseAnnotation(type, annotation);
				customFactories.put(modelClass, modelFactoryClass);
				return modelFactoryClass;
			}
		}

		IAnnotation annotation = type.getAnnotation(ModelEditorDescriptor.class.getName());
		String modelFactoryClass = annotation == null ? null : parseAnnotation(type, annotation);
		customFactories.put(modelClass, modelFactoryClass);
		return modelFactoryClass;
	}

	private static String parseAnnotation(IType type, IAnnotation annotation) throws JavaModelException {
		IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
		String factoryName = null;
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
				factoryName = builder.toString();
			}
		}

		return factoryName;
	}
}
