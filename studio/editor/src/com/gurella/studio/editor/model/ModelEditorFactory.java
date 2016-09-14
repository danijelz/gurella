package com.gurella.studio.editor.model;

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

import com.gurella.engine.editor.model.ModelEditorDescriptor;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.engine.model.CustomModelEditor;
import com.gurella.studio.editor.engine.model.CustomModelEditorContextAdapter;

public class ModelEditorFactory {
	private static final Map<Class<?>, String> customFactories = new HashMap<>();

	public static <T> MetaModelEditor<T> createEditor(Composite parent, SceneEditorContext context, T instance) {
		com.gurella.engine.editor.model.ModelEditorFactory<T> factory = getCustomFactory(instance, context);
		if (factory == null) {
			return new DefaultMetaModelEditor<T>(parent, context, instance);
		} else {
			return new CustomModelEditor<>(parent, new CustomModelEditorContextAdapter<>(context, instance, factory));
		}
	}

	public static <T> MetaModelEditor<T> createEditor(Composite parent, ModelEditorContext<?> parentContext,
			T instance) {
		SceneEditorContext sceneContext = parentContext.sceneEditorContext;
		com.gurella.engine.editor.model.ModelEditorFactory<T> factory = getCustomFactory(instance, sceneContext);
		if (factory == null) {
			return new DefaultMetaModelEditor<T>(parent, new ModelEditorContext<>(parentContext, instance));
		} else {
			return new CustomModelEditor<>(parent, new CustomModelEditorContextAdapter<>(parentContext, instance, factory));
		}
	}

	private static <T> com.gurella.engine.editor.model.ModelEditorFactory<T> getCustomFactory(T modelInstance,
			SceneEditorContext sceneContext) {
		try {
			String customFactoryClass = getCustomFactoryClass(modelInstance, sceneContext.javaProject);
			if (customFactoryClass == null) {
				return null;
			}

			Class<?> factoryClass = sceneContext.classLoader.loadClass(customFactoryClass);
			Constructor<?> constructor = factoryClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			return cast(constructor.newInstance(new Object[0]));
		} catch (Exception e) {
			customFactories.put(modelInstance.getClass(), null);
			return null;
		}
	}

	private static <T> String getCustomFactoryClass(T modelInstance, IJavaProject javaProject) throws Exception {
		Class<?> modelClass = modelInstance.getClass();
		if (customFactories.containsKey(modelClass)) {
			return customFactories.get(modelClass);
		}

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
