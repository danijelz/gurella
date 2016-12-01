package com.gurella.studio.refractoring;

import org.eclipse.jdt.internal.corext.refactoring.tagging.IQualifiedNameUpdating;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;

@SuppressWarnings("restriction")
public class RefractoringUtils {
	private RefractoringUtils() {
	}

	static boolean qualifiedNamesHandledByProcessor(RefactoringProcessor processor) {
		if (processor instanceof IQualifiedNameUpdating) {
			IQualifiedNameUpdating qualifiedNameUpdating = (IQualifiedNameUpdating) processor;
			return qualifiedNameUpdating.getUpdateQualifiedNames()
					&& qualifiedNameUpdating.canEnableQualifiedNameUpdating();
		} else {
			return false;
		}
	}
}
