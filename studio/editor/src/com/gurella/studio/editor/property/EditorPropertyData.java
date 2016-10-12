package com.gurella.studio.editor.property;

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
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.ModelEditorContext;

public class EditorPropertyData {
	private static final Map<EditorPropertyKey, EditorPropertyData> editorProperties = new HashMap<>();

	public final EditorType type;
	public final String factoryClass;
	public final int index;
	public final String group;
	public final String descriptiveName;
	public final String description;

	private EditorPropertyData(EditorType type, String factoryClass, int index, String group, String descriptiveName,
			String description) {
		this.type = type;
		this.factoryClass = factoryClass;
		this.index = index;
		this.group = group;
		this.descriptiveName = descriptiveName;
		this.description = description;
	}

	public static int getIndex(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		Property<?> property = context.property;
		return EditorPropertyData.getIndex(javaProject, modelClass, property);
	}

	public static int getIndex(ModelEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		return EditorPropertyData.getIndex(javaProject, modelClass, property);
	}

	public static int getIndex(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		EditorPropertyData editorPropertyData = get(javaProject, modelClass, property);
		return editorPropertyData == null ? 0 : editorPropertyData.index;
	}

	public static String getGroup(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		Property<?> property = context.property;
		return EditorPropertyData.getGroup(javaProject, modelClass, property);
	}

	public static String getGroup(ModelEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		return EditorPropertyData.getGroup(javaProject, modelClass, property);
	}

	public static String getGroup(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		EditorPropertyData editorPropertyData = get(javaProject, modelClass, property);
		return editorPropertyData == null || editorPropertyData.group == null ? "" : editorPropertyData.group;
	}

	public static String getDescription(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		Property<?> property = context.property;
		return EditorPropertyData.getDescription(javaProject, modelClass, property);
	}

	public static String getDescription(ModelEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		return EditorPropertyData.getDescription(javaProject, modelClass, property);
	}

	public static String getDescription(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		EditorPropertyData editorPropertyData = get(javaProject, modelClass, property);
		return editorPropertyData == null ? null : editorPropertyData.description;
	}

	public static String getDescriptiveName(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		Property<?> property = context.property;
		return EditorPropertyData.getDescriptiveName(javaProject, modelClass, property);
	}

	public static String getDescriptiveName(ModelEditorContext<?> context, Property<?> property) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		return EditorPropertyData.getDescriptiveName(javaProject, modelClass, property);
	}

	public static String getDescriptiveName(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		EditorPropertyData editorPropertyData = get(javaProject, modelClass, property);
		String descriptiveName = editorPropertyData == null ? null : editorPropertyData.descriptiveName;
		return Values.isBlank(descriptiveName) ? property.getName() : editorPropertyData.descriptiveName;
	}

	public static EditorPropertyData get(PropertyEditorContext<?, ?> context) {
		IJavaProject javaProject = context.sceneEditorContext.javaProject;
		Class<?> modelClass = context.modelInstance.getClass();
		Property<?> property = context.property;
		return EditorPropertyData.get(javaProject, modelClass, property);
	}

	public static EditorPropertyData get(IJavaProject javaProject, Class<?> modelClass, Property<?> property) {
		try {
			return getSafely(javaProject, modelClass, property);
		} catch (Exception e) {
			editorProperties.put(new EditorPropertyKey(modelClass, property.getName()), null);
			return null;
		}
	}

	private static EditorPropertyData getSafely(IJavaProject javaProject, Class<?> modelClass, Property<?> property)
			throws Exception {
		if (!(property instanceof ReflectionProperty)) {
			return null;
		}

		String propertyName = property.getName();
		EditorPropertyKey key = new EditorPropertyKey(modelClass, propertyName);
		EditorPropertyData data = editorProperties.get(key);
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

	private static EditorPropertyData parseAnnotation(IType type, IAnnotation annotation) throws JavaModelException {
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

		return new EditorPropertyData(editorType, factoryName, index, group, descriptiveName, description);
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
}
