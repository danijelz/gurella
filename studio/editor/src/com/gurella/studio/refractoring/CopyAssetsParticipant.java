package com.gurella.studio.refractoring;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.ltk.core.refactoring.participants.CopyParticipant;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;

public class CopyAssetsParticipant extends CopyParticipant {
	private IFile file;

	@Override
	protected boolean initialize(Object element) {
		file = (IFile) element;
		return true;
	}

	@Override
	public String getName() {
		return "Gurella asset copy participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor monitor, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		final IContainer destination = (IContainer) getArguments().getDestination();
		IProject project = file.getProject();
		IPath assetsFolderPath = project.getProjectRelativePath().append("assets");
		IResource[] roots = { destination };
		IPath newResourcePath = destination.getProjectRelativePath().makeRelativeTo(assetsFolderPath)
				.append(file.getName());
		String[] fileNamePatterns = {newResourcePath.toString()};

		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
		final Map<IFile, TextFileChange> changes = new HashMap<>();
		TextSearchRequestor requestor = new TextSearchRequestorExtension(this, changes, newResourcePath.toString());
		Pattern pattern = Pattern.compile("uuid: ..............");
		TextSearchEngine.create().search(scope, requestor, pattern, monitor);

		if (changes.isEmpty()) {
			return null;
		} else {
			CompositeChange result = new CompositeChange("Gurella asset references update");
			changes.values().forEach(result::add);
			return result;
		}
	}
}
