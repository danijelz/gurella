package com.gurella.studio.editor.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

public class JdtUtils {
	private JdtUtils() {
	}

	public static ICompilationUnit getCompilationUnit(final IResource resource) {
		IJavaElement element = JavaCore.create(resource);
		return element instanceof ICompilationUnit ? (ICompilationUnit) element : null;
	}
}
