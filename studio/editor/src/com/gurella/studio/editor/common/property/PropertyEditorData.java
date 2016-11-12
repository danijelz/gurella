package com.gurella.studio.editor.common.property;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.model.ReflectionProperty;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.editor.property.PropertyEditorDescriptor.EditorType;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.common.bean.BeanEditorContext;

public class PropertyEditorData {
	private static final Map<EditorPropertyKey, PropertyEditorData> editorProperties = new HashMap<>();

	public final EditorType type;
	public final String customFactoryClass;
	public final int index;
	public final String group;
	public final String descriptiveName;
	public final String description;

	private PropertyEditorData(EditorType type, String customFactoryClass, int index, String group,
			String descriptiveName, String description) {
		this.type = type;
		this.customFactoryClass = customFactoryClass;
		this.index = index;
		this.group = group;
		this.descriptiveName = descriptiveName;
		this.description = description;
	}

	public static int compare(BeanEditorContext<?> context, Property<?> p1, Property<?> p2) {
		return Integer.compare(getIndex(context, p1), getIndex(context, p2));
	}

	public static int getIndex(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		Property<?> property = context.property;
		return PropertyEditorData.getIndex(javaProject, modelClass, property);
	}

	public static int getIndex(BeanEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		return PropertyEditorData.getIndex(javaProject, modelClass, property);
	}

	public static int getIndex(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		PropertyEditorData propertyEditorData = get(javaProject, modelClass, property);
		return propertyEditorData == null ? 0 : propertyEditorData.index;
	}

	public static String getGroup(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		Property<?> property = context.property;
		return PropertyEditorData.getGroup(javaProject, modelClass, property);
	}

	public static String getGroup(BeanEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		return PropertyEditorData.getGroup(javaProject, modelClass, property);
	}

	public static String getGroup(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		PropertyEditorData propertyEditorData = get(javaProject, modelClass, property);
		return propertyEditorData == null || propertyEditorData.group == null ? "" : propertyEditorData.group;
	}

	public static String getDescription(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		Property<?> property = context.property;
		return PropertyEditorData.getDescription(javaProject, modelClass, property);
	}

	public static String getDescription(BeanEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		return PropertyEditorData.getDescription(javaProject, modelClass, property);
	}

	public static String getDescription(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		PropertyEditorData propertyEditorData = get(javaProject, modelClass, property);
		return propertyEditorData == null ? null : propertyEditorData.description;
	}

	public static String getDescriptiveName(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		Property<?> property = context.property;
		return PropertyEditorData.getDescriptiveName(javaProject, modelClass, property);
	}

	public static String getDescriptiveName(BeanEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		return PropertyEditorData.getDescriptiveName(javaProject, modelClass, property);
	}

	public static String getDescriptiveName(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		PropertyEditorData propertyEditorData = get(javaProject, modelClass, property);
		String descriptiveName = propertyEditorData == null ? null : propertyEditorData.descriptiveName;
		return Values.isBlank(descriptiveName) ? property.getName() : propertyEditorData.descriptiveName;
	}

	public static PropertyEditorData get(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneContext.javaProject;
		Class<?> modelClass = context.bean.getClass();
		Property<?> property = context.property;
		return PropertyEditorData.get(javaProject, modelClass, property);
	}

	public static PropertyEditorData get(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		try {
			return getSafely(javaProject, modelClass, property);
		} catch (Exception e) {
			editorProperties.put(new EditorPropertyKey(modelClass, property.getName()), null);
			return null;
		}
	}

	private static PropertyEditorData getSafely(IJavaProject javaProject, Class<?> modelClass, Property<?> property)
			throws Exception {
		if (!(property instanceof ReflectionProperty)) {
			return null;
		}

		String propertyName = property.getName();
		EditorPropertyKey key = new EditorPropertyKey(modelClass, propertyName);
		PropertyEditorData data = editorProperties.get(key);
		if (data != null) {
			return data;
		}

		ReflectionProperty<?> reflectionProperty = (ReflectionProperty<?>) property;
		Class<?> declaringClass = reflectionProperty.getDeclaringClass();
		EditorPropertyKey declaredKey = new EditorPropertyKey(declaringClass, propertyName);

		if (reflectionProperty.getField() == null) {
			// TODO bean properties
			editorProperties.put(declaredKey, data);
			editorProperties.put(key, data);
			return data;
		} else {
			data = editorProperties.get(declaredKey);
			if (data != null) {
				editorProperties.put(key, data);
				return data;
			}

			IType type = javaProject.findType(declaringClass.getName());
			IField jdtField = type.getField(property.getName());
			for (IAnnotation annotation : jdtField.getAnnotations()) {
				Class<PropertyEditorDescriptor> annotationType = PropertyEditorDescriptor.class;
				String elementName = annotation.getElementName();
				if (elementName.equals(annotationType.getSimpleName())
						|| elementName.equals(annotationType.getName())) {
					data = parseAnnotation(type, annotation);
					editorProperties.put(declaredKey, data);
					editorProperties.put(key, data);
					return data;
				}
			}

			IAnnotation annotation = jdtField.getAnnotation(PropertyEditorDescriptor.class.getName());
			data = annotation == null ? null : parseAnnotation(type, annotation);
			editorProperties.put(declaredKey, data);
			editorProperties.put(key, data);
			return data;
		}
	}

	private static PropertyEditorData parseAnnotation(IType type, IAnnotation annotation) throws JavaModelException {
		IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
		String factoryName = null;
		EditorType editorType = EditorType.composite;
		int index = 0;
		String group = null;
		String description = null;
		String descriptiveName = null;

		for (IMemberValuePair memberValuePair : memberValuePairs) {
			if ("factory".equals(memberValuePair.getMemberName())) {
				String[][] resolveType = type.resolveType((String) memberValuePair.getValue());
				if (resolveType.length == 1) {
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
			} else if ("type".equals(memberValuePair.getMemberName())) {
				String editorTypeStr = memberValuePair.getValue().toString();
				if (editorTypeStr.contains(EditorType.simple.name())) {
					editorType = EditorType.simple;
				} else if (editorTypeStr.contains(EditorType.custom.name())) {
					editorType = EditorType.custom;
				}
			} else if ("index".equals(memberValuePair.getMemberName())) {
				index = Integer.parseInt(memberValuePair.getValue().toString());
			} else if ("group".equals(memberValuePair.getMemberName())) {
				group = memberValuePair.getValue().toString();
			} else if ("description".equals(memberValuePair.getMemberName())) {
				description = memberValuePair.getValue().toString();
			} else if ("descriptiveName".equals(memberValuePair.getMemberName())) {
				descriptiveName = memberValuePair.getValue().toString();
			}
		}

		return new PropertyEditorData(editorType, factoryName, index, group, descriptiveName, description);
	}

	private static class EditorPropertyKey {
		String typeName;
		String propertyName;

		public EditorPropertyKey(Class<?> type, String propertyName) {
			this.typeName = type.getName();
			this.propertyName = propertyName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + typeName.hashCode();
			result = prime * result + propertyName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			EditorPropertyKey other = (EditorPropertyKey) obj;
			return typeName.equals(other.typeName) && propertyName.equals(other.propertyName);
		}
	}

	public boolean isValidFactoryClass() {
		return customFactoryClass != null && !PropertyEditorFactory.class.getName().equals(customFactoryClass);
	}
}
