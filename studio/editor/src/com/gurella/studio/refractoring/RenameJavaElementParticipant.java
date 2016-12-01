package com.gurella.studio.refractoring;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

import com.gurella.engine.utils.Values;

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
		if (RefractoringUtils.qualifiedNamesHandledByProcessor(getProcessor())) {
			return null;
		}

		IResource[] rootResources = RefractoringUtils.getSearchScopeRootResources(element.getResource().getProject());
		if (Values.isEmpty(rootResources)) {
			return null;
		}

		String oldName = element instanceof IType ? ((IType) element).getFullyQualifiedName('.')
				: element.getElementName();
		int index = oldName.lastIndexOf(element.getElementName());
		String newName = oldName.substring(0, index).concat(getArguments().getNewName());

		System.out.println("Rename java element: " + oldName + " to " + newName);
		String regex = "(?<=[[:|\\s|\\r|\\n]{1}[\\s|\\r|\\n]{0,100}]|^)" + Pattern.quote(oldName);
		return RefractoringUtils.createChange(monitor, rootResources, regex, newName);
	}
}
