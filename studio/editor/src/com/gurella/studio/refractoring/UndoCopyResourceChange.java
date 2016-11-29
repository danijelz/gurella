package com.gurella.studio.refractoring;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;

public class UndoCopyResourceChange extends ResourceChange {
	private final IResource original;
	private final IResource copy;
	private final Change restoreSourceChange;

	protected UndoCopyResourceChange(IResource original, IResource copy, Change restoreSourceChange) {
		this.original = original;
		this.copy = copy;
		this.restoreSourceChange = restoreSourceChange;

		// We already present a dialog to the user if he
		// moves read-only resources. Since moving a resource
		// doesn't do a validate edit (it actually doesn't
		// change the content we can't check for READ only
		// here.
		setValidationMethod(VALIDATE_NOT_DIRTY);
	}

	@Override
	public final Change perform(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		IProgressMonitor progressMonitor = monitor == null ? new NullProgressMonitor() : monitor;
		try {
			return performSafely(progressMonitor);
		} finally {
			progressMonitor.done();
		}
	}

	private Change performSafely(IProgressMonitor progressMonitor) throws CoreException {
		progressMonitor.beginTask(getName(), 4);

		deleteCopy(SubMonitor.convert(progressMonitor, 1));
		IContainer parent = copy.getParent();

		copy.delete(true, SubMonitor.convert(progressMonitor, 2));

		// restore file at source
		if (restoreSourceChange != null) {
			performSourceRestore(SubMonitor.convert(progressMonitor, 1));
		} else {
			progressMonitor.worked(1);
		}

		return new CopyResourceChange(original, parent);
	}

	private Change deleteCopy(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Delete resource at destination", 3);
		try {
			return deleteCopySafely(monitor);
		} finally {
			monitor.done();
		}
	}

	private Change deleteCopySafely(IProgressMonitor monitor) throws CoreException {
		DeleteResourceChange deleteChange = new DeleteResourceChange(copy.getFullPath(), true);
		deleteChange.initializeValidationData(SubMonitor.convert(monitor, 1));
		RefactoringStatus deleteStatus = deleteChange.isValid(SubMonitor.convert(monitor, 1));
		return deleteStatus.hasFatalError() ? null : deleteChange.perform(SubMonitor.convert(monitor, 1));
	}

	private void performSourceRestore(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Restore resource at source", 3);
		try {
			performSourceRestoreSafely(monitor);
		} finally {
			monitor.done();
		}
	}

	private void performSourceRestoreSafely(IProgressMonitor monitor) throws CoreException {
		restoreSourceChange.initializeValidationData(SubMonitor.convert(monitor, 1));
		RefactoringStatus restoreStatus = restoreSourceChange.isValid(SubMonitor.convert(monitor, 1));
		if (!restoreStatus.hasFatalError()) {
			restoreSourceChange.perform(SubMonitor.convert(monitor, 1));
		}
	}

	@Override
	protected IResource getModifiedResource() {
		return copy;
	}

	@Override
	public String getName() {
		String resourcePath = copy.getLocation().makeRelative().toString();
		return String.format("undo copy resource '%s' to '%s'", resourcePath, copy.getParent().getName());
	}
}
