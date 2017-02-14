package com.gurella.studio.refractoring;

import static com.gurella.studio.common.AssetsFolderLocator.assetsFolderName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.corext.refactoring.tagging.IQualifiedNameUpdating;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;

@SuppressWarnings("restriction")
public class RefractoringUtils {
	private RefractoringUtils() {
	}

	static boolean qualifiedNamesHandledByProcessor(RefactoringProcessor processor) {
		if (processor instanceof IQualifiedNameUpdating) {
			IQualifiedNameUpdating qualifiedNameUpdating = (IQualifiedNameUpdating) processor;
			return qualifiedNameUpdating.getUpdateQualifiedNames()
					&& qualifiedNameUpdating.canEnableQualifiedNameUpdating();
		} else {
			return false;
		}
	}

	static IResource[] getSearchScopeRootResources(IProject root) {
		HashSet<IProject> resources = new HashSet<>();
		resources.add(root);
		appendReferencingProjects(root, resources);
		return resources.stream().map(r -> r.getFolder(assetsFolderName)).filter(f -> f != null && f.exists())
				.toArray(i -> new IResource[i]);
	}

	private static void appendReferencingProjects(IProject root, Set<IProject> resources) {
		IProject[] projects = root.getReferencingProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			if (resources.add(project)) {
				appendReferencingProjects(project, resources);
			}
		}
	}

	static String[] getFileNamePatterns() {
		// TODO extract info from AssetType
		return new String[] { "*.pref", "*.gscn", "*.gmat", "*.glslt", "*.grt", "*.giam" };
	}

	static Change createChange(boolean txtFilesHandled, IProgressMonitor monitor, IResource[] rootResources,
			String regEx, String replacement) {
		final Map<IFile, TextFileChange> changes = new HashMap<>();
		TextSearchRequestor requestor = new ReferencesSearchRequestor(txtFilesHandled, changes, replacement);
		return createChange(monitor, rootResources, regEx, requestor, changes);
	}

	private static Change createChange(IProgressMonitor monitor, IResource[] rootResources, String regEx,
			TextSearchRequestor requestor, final Map<IFile, TextFileChange> changes) {
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(rootResources, getFileNamePatterns(), false);
		Pattern pattern = Pattern.compile(regEx);
		TextSearchEngine.create().search(scope, requestor, pattern, monitor);

		if (changes.isEmpty()) {
			return null;
		}

		CompositeChange result = new CompositeChange("Gurella asset references update");
		changes.values().forEach(result::add);
		return result;
	}

	static Change createPackageMoveChange(boolean txtFilesHandled, IProgressMonitor monitor, IResource[] rootResources,
			String regEx, String replacement, IJavaElement movedElement) {
		final Map<IFile, TextFileChange> changes = new HashMap<>();
		TextSearchRequestor requestor = new PackageReferencesSearchRequestor(txtFilesHandled, movedElement, changes,
				replacement);
		return createChange(monitor, rootResources, regEx, requestor, changes);
	}
}
