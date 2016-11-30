package com.gurella.studio.refractoring;

import java.util.Arrays;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CopyRefactoring;

public class CopyAssetsDescriptor extends RefactoringDescriptor {
	public static final String ID = "com.gurella.studio.refractoring.copy.assets"; //$NON-NLS-1$

	private IPath destinationPath;
	private IPath[] resourcePathsToCopy;

	public CopyAssetsDescriptor() {
		super(ID, null, "N/A", null, STRUCTURAL_CHANGE | MULTI_CHANGE);
		resourcePathsToCopy = null;
		destinationPath = null;
	}

	protected CopyAssetsDescriptor(String id, String project, String description, String comment, int flags) {
		super(id, project, description, comment, flags);
	}

	public void setDestination(IContainer container) {
		Assert.isNotNull(container);
		destinationPath = container.getFullPath();
	}

	public void setDestinationPath(IPath path) {
		Assert.isNotNull(path);
		destinationPath = path;
	}

	public IPath getDestinationPath() {
		return destinationPath;
	}

	public void setResourcePathsToCopy(IPath[] resourcePaths) {
		if (resourcePaths == null || resourcePaths.length == 0) {
			throw new IllegalArgumentException("Only arrays with size > 0 are allowed");
		}
		resourcePathsToCopy = resourcePaths;
	}

	public void setResourcesToCopy(IResource[] resources) {
		if (resources == null || resources.length == 0) {
			throw new IllegalArgumentException("Only arrays with size > 0 are allowed");
		}
		resourcePathsToCopy = Arrays.stream(resources).map(r -> r.getFullPath()).toArray(i -> new IPath[i]);
	}

	public IPath[] getResourcePathsToCopy() {
		return resourcePathsToCopy;
	}

	@Override
	public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IPath destinationPath = getDestinationPath();
		if (destinationPath == null) {
			status.addFatalError("Destination path is not set");
			return null;
		}

		IResource destination = root.findMember(destinationPath);
		if (!(destination instanceof IFolder || destination instanceof IProject) || !destination.exists()) {
			status.addFatalError(String.format("The destination container '%s' does not exist",
					destinationPath.makeRelative().toString()));
			return null;
		}

		IPath[] paths = getResourcePathsToCopy();
		if (paths == null) {
			status.addFatalError("Paths to copy are not set");
			return null;
		}

		IResource[] resources = new IResource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			IPath path = paths[i];
			if (path == null) {
				status.addFatalError("Path to copy is null");
				return null;
			}
			IResource resource = root.findMember(path);
			if (resource == null || !resource.exists()) {
				status.addFatalError(
						String.format("The copied resource '%s' does not exist", path.makeRelative().toString()));
				return null;
			}
			if (!(resource instanceof IFile || resource instanceof IFolder)) {
				status.addFatalError(String.format("The copied resource ''%s'' is not a file or folder",
						path.makeRelative().toString()));
				return null;
			}
			resources[i] = resource;
		}

		CopyAssetsProcessor processor = new CopyAssetsProcessor(resources);
		processor.setDestination((IContainer) destination);
		return new CopyRefactoring(processor);
	}
}
