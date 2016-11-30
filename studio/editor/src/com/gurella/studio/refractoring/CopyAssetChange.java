package com.gurella.studio.refractoring;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;

public class CopyAssetChange extends ResourceChange {
	private final IResource source;
	private final IContainer target;

	private ChangeDescriptor descriptor;

	protected CopyAssetChange(IResource source, IContainer target) {
		this.source = source;
		this.target = target;
		setValidationMethod(VALIDATE_NOT_DIRTY);
	}

	@Override
	public ChangeDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(ChangeDescriptor descriptor) {
		this.descriptor = descriptor;
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

		Change deleteUndo = null;
		IResource resourceAtDestination = target.findMember(source.getName());
		if (resourceAtDestination != null && resourceAtDestination.exists()) {
			deleteUndo = performDestinationDelete(resourceAtDestination, SubMonitor.convert(progressMonitor, 1));
		} else {
			progressMonitor.worked(1);
		}

		IPath destinationPath = target.getFullPath().append(source.getName());
		int flags = IResource.KEEP_HISTORY | IResource.SHALLOW | IResource.FORCE;
		source.copy(destinationPath, flags, SubMonitor.convert(progressMonitor, 2));
		resourceAtDestination = ResourcesPlugin.getWorkspace().getRoot().findMember(destinationPath);

		progressMonitor.worked(1);
		return new UndoCopyAssetChange(source, resourceAtDestination, deleteUndo);
	}

	private static Change performDestinationDelete(IResource newResource, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Delete resource at destination", 3);
		try {
			return performDestinationDeleteSafely(newResource, monitor);
		} finally {
			monitor.done();
		}
	}

	private static Change performDestinationDeleteSafely(IResource newResource, IProgressMonitor monitor)
			throws CoreException {
		DeleteResourceChange deleteChange = new DeleteResourceChange(newResource.getFullPath(), true);
		deleteChange.initializeValidationData(SubMonitor.convert(monitor, 1));
		RefactoringStatus deleteStatus = deleteChange.isValid(SubMonitor.convert(monitor, 1));
		return deleteStatus.hasFatalError() ? null : deleteChange.perform(SubMonitor.convert(monitor, 1));
	}

	@Override
	protected IResource getModifiedResource() {
		return source;
	}

	@Override
	public String getName() {
		return String.format("copy resource '%s' to '%s'", source.getLocation().makeRelative().toString(),
				target.getName());
	}
}
