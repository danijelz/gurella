package com.gurella.studio.refractoring;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

public class RenameAssetFolderParticipant extends RenameParticipant {
	private IFolder folder;

	@Override
	protected boolean initialize(Object element) {
		folder = (IFolder) element;
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
		System.out.println("Rename folder: " + folder.getName());
		// TODO Auto-generated method stub
		return null;
	}
}
