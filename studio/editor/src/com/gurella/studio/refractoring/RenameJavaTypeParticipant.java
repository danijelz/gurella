package com.gurella.studio.refractoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

public class RenameJavaTypeParticipant extends RenameParticipant {
	private IType type;

	@Override
	protected boolean initialize(Object element) {
		type = (IType) element;
		return true;
	}

	@Override
	public String getName() {
		return "Gurella java type rename participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		System.out.println("Rename type: " + type.getFullyQualifiedName());
		// TODO Auto-generated method stub
		return null;
	}
}
