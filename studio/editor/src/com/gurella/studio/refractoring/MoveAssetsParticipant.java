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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
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
import com.gurella.studio.editor.utils.JdtUtils;

public class MoveAssetsParticipant extends MoveParticipant {
	private IFile file;

	@Override
	protected boolean initialize(Object element) {
		file = (IFile) element;
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
		if (Assets.getAssetType(file.getName()) == null) {
			return null;
		}

		final Map<IFile, TextFileChange> changes = new HashMap<>();
		final IContainer destination = (IContainer) getArguments().getDestination();
		IProject project = file.getProject();
		IPath assetsFolderPath = project.getProjectRelativePath().append("assets");
		IResource[] roots = { project };
		String[] fileNamePatterns = { "*.pref", "*.gscn", "*.gmat", "*.glslt", "*.grt", "*.giam" };
		IPath oldResourcePath = file.getProjectRelativePath().makeRelativeTo(assetsFolderPath);
		IPath newResourcePath = destination.getProjectRelativePath().makeRelativeTo(assetsFolderPath)
				.append(file.getName());

		// TODO if java file
		if ("java".equals(file.getProjectRelativePath().getFileExtension())) {
			ICompilationUnit compilationUnit = JdtUtils.getCompilationUnit(file);
			for (IType type : compilationUnit.getAllTypes()) {
				String qualifiedName = type.getFullyQualifiedName('.');

			}
		} else {
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
			TextSearchRequestor requestor = new RenameAssetRequestor(changes, newResourcePath.toString());
			Pattern pattern = Pattern.compile(oldResourcePath.toString());
			TextSearchEngine.create().search(scope, requestor, pattern, monitor);
		}

		if (changes.isEmpty()) {
			return null;
		}

		CompositeChange result = new CompositeChange("Gurella asset references update");
		changes.values().forEach(result::add);
		return result;
	}
}
