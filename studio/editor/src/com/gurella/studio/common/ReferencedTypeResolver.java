package com.gurella.studio.common;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.gurella.engine.metatype.Property;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.ui.property.PropertyEditorContext;
import com.gurella.studio.editor.utils.Try;

public class ReferencedTypeResolver {
	private ReferencedTypeResolver() {
	}

	public static Optional<String> resolveReferencedType(PropertyEditorContext<?, ?> context) {
		return Try.ignored(() -> _resolveReferencedType(context), Optional.empty());
	}

	private static Optional<String> _resolveReferencedType(PropertyEditorContext<?, ?> context)
			throws JavaModelException {
		Property<?> property = context.property;
		IJavaProject javaProject = context.javaProject;
		String typeName = context.bean.getClass().getName();
		IType type = javaProject.findType(typeName);
		final String propertyName = property.getName();
		IField field = type.getField(propertyName);
		String typeSignature = field.getTypeSignature();
		String[] typeArguments = Signature.getTypeArguments(typeSignature);
		if (typeArguments == null || typeArguments.length != 1) {
			return Optional.empty();
		}

		String typeArgument = typeArguments[0];

		switch (Signature.getTypeSignatureKind(typeArgument)) {
		case Signature.BASE_TYPE_SIGNATURE:
		case Signature.CLASS_TYPE_SIGNATURE:
			String componentTypeName = Signature.toString(Signature.getTypeErasure(typeArgument));
			return Optional.ofNullable(resolveReferencedType(type, componentTypeName));
		case Signature.ARRAY_TYPE_SIGNATURE:
			return Values.cast(Reflection.forName(typeArgument));
		default:
			return Optional.empty();
		}
	}

	public static String resolveReferencedType(IType type, String referencedType) {
		String[][] resolveType = Try.ignored(() -> type.resolveType(referencedType), new String[0][]);
		if (resolveType == null || resolveType.length != 1) {
			return null;
		}
		String[] path = resolveType[0];
		int last = path.length - 1;
		path[last] = path[last].replaceAll("\\.", "\\$");
		return Stream.of(path).collect(Collectors.joining("."));
	}
}
