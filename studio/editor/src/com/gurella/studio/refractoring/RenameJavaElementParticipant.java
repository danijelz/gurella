package com.gurella.studio.refractoring;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;

public class RenameJavaElementParticipant extends RenameParticipant {
	private IJavaElement element;

	@Override
	protected boolean initialize(Object element) {
		this.element = (IJavaElement) element;
		return true;
	}

	@Override
	public String getName() {
		return "Gurella java element rename participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		String oldName = element instanceof IType ? ((IType) element).getFullyQualifiedName('.')
				: element.getElementName();
		int index = oldName.lastIndexOf(element.getElementName());
		String newName = oldName.substring(0, index).concat(getArguments().getNewName());
		System.out.println("Rename java element: " + oldName + " to " + newName);

		final Map<IFile, TextFileChange> changes = new HashMap<>();
		IResource[] roots = { element.getResource().getProject().getFolder("assets") };
		String[] fileNamePatterns = { "*.pref", "*.gscn", "*.gmat", "*.glslt", "*.grt", "*.giam" };
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
		TextSearchRequestor requestor = new RenameAssetRequestor(changes, newName);
		Pattern pattern = Pattern.compile(oldName);
		TextSearchEngine.create().search(scope, requestor, pattern, monitor);

		if (changes.isEmpty()) {
			return null;
		}

		CompositeChange result = new CompositeChange("Gurella asset references update");
		changes.values().forEach(result::add);
		return result;
	}
}
