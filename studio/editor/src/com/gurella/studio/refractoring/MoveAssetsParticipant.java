package com.gurella.studio.refractoring;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;

import com.gurella.engine.asset.Assets;
import com.gurella.engine.utils.Values;

public class MoveAssetsParticipant extends MoveParticipant {
	private IResource resource;

	@Override
	protected boolean initialize(Object element) {
		resource = (IResource) element;
		return true;
	}

	@Override
	public String getName() {
		return "Gurella asset move participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		IFolder assetsFolder = resource.getProject().getFolder("assets");
		if (assetsFolder == null || !assetsFolder.exists()) {
			return null;
		}

		if (!"assets".equals(resource.getProjectRelativePath().segment(0))) {
			return null;
		}

		if (resource instanceof IFile && Assets.getAssetType(resource.getName()) == null) {
			return null;
		}

		IResource[] rootResources = RefractoringUtils.getSearchScopeRootResources(resource.getProject());
		if (Values.isEmpty(rootResources)) {
			return null;
		}

		final IContainer destination = (IContainer) getArguments().getDestination();
		IPath assetsFolderPath = assetsFolder.getProjectRelativePath();
		IPath oldResourcePath = resource.getProjectRelativePath().makeRelativeTo(assetsFolderPath);
		IPath newResourcePath = destination.getProjectRelativePath().makeRelativeTo(assetsFolderPath)
				.append(resource.getName());

		String regex = "(?<=[[:|\\s|\\r|\\n]{1}[\\s|\\r|\\n]{0,100}]|^)" + Pattern.quote(oldResourcePath.toString());
		return RefractoringUtils.createChange(false, monitor, rootResources, regex, newResourcePath.toString());
	}
}
