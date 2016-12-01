package com.gurella.studio.refractoring;

import java.util.HashMap;
import java.util.Map;
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
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;

import com.gurella.engine.asset.Assets;

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

		final IContainer destination = (IContainer) getArguments().getDestination();
		IResource[] roots = { assetsFolder };
		String[] fileNamePatterns = { "*.pref", "*.gscn", "*.gmat", "*.glslt", "*.grt", "*.giam" };
		IPath assetsFolderPath = assetsFolder.getProjectRelativePath();
		IPath oldResourcePath = resource.getProjectRelativePath().makeRelativeTo(assetsFolderPath);
		IPath newResourcePath = destination.getProjectRelativePath().makeRelativeTo(assetsFolderPath)
				.append(resource.getName());

		System.out.println("Move asset: " + oldResourcePath + " to " + newResourcePath);

		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
		final Map<IFile, TextFileChange> changes = new HashMap<>();
		TextSearchRequestor requestor = new RenameAssetSearchRequestor(changes, newResourcePath.toString());
		Pattern pattern = Pattern.compile(oldResourcePath.toString());
		TextSearchEngine.create().search(scope, requestor, pattern, monitor);

		if (changes.isEmpty()) {
			return null;
		}

		CompositeChange result = new CompositeChange("Gurella asset references update");
		changes.values().forEach(result::add);
		return result;
	}
}
