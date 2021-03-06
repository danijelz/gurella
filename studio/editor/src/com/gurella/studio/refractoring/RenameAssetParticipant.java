package com.gurella.studio.refractoring;

import java.util.regex.Pattern;

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
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.utils.Values;
import com.gurella.studio.common.AssetsFolderLocator;

public class RenameAssetParticipant extends RenameParticipant {
	private IResource resource;

	@Override
	protected boolean initialize(Object element) {
		resource = (IResource) element;
		return true;
	}

	@Override
	public String getName() {
		return "Gurella asset rename participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		IFolder assetsFolder = AssetsFolderLocator.getAssetsFolder(resource);
		if (assetsFolder == null || !assetsFolder.exists()) {
			return null;
		}

		if (!AssetsFolderLocator.assetsFolderName.equals(resource.getProjectRelativePath().segment(0))) {
			return null;
		}

		if (resource instanceof IFile && AssetDescriptors.getAssetDescriptor(resource.getName()) == null) {
			return null;
		}

		IResource[] rootResources = RefractoringUtils.getSearchScopeRootResources(resource.getProject());
		if (Values.isEmpty(rootResources)) {
			return null;
		}

		IPath assetsFolderPath = assetsFolder.getProjectRelativePath();
		IPath oldResourcePath = resource.getProjectRelativePath().makeRelativeTo(assetsFolderPath);
		String newName = getArguments().getNewName();
		IPath newResourcePath = oldResourcePath.removeLastSegments(1).append(newName);

		String regex = "(?<=[[:|\\s|\\r|\\n]{1}[\\s|\\r|\\n]{0,100}]|^)" + Pattern.quote(oldResourcePath.toString())
				+ "(?=\"|/|\\s|\\r|\\n|$)";
		return RefractoringUtils.createChange(false, monitor, rootResources, regex, newResourcePath.toString());
	}
}
