package com.gurella.studio.refractoring;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

public class GurellaAssetsMoveParticipants extends MoveParticipant {
	private IResource resource;

	@Override
	protected boolean initialize(Object element) {
		resource = (IResource) element;
		return true;
	}

	@Override
	public String getName() {
		return "Gurella asset references participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		final IResource destination = (IResource) getArguments().getDestination();
		IProject project = resource.getProject();
		IPath assetsFolderPath = project.getLocation().append("assets");
		IResource[] roots = { project };
		String[] fileNamePatterns = { "*.pref", "*.gscn", "*.gmat", "*.glslt", "*.grt", "*.giam" };
		IPath oldResourcePath = resource.getProjectRelativePath().makeRelativeTo(assetsFolderPath);
		IPath newResourcePath = destination.getProjectRelativePath().makeRelativeTo(assetsFolderPath);

		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
		final Map<IFile, TextFileChange> changes = new HashMap<>();
		TextSearchRequestor collector = new TextSearchRequestorExtension(changes, newResourcePath.toString());
		Pattern pattern = Pattern.compile(oldResourcePath.toString());
		TextSearchEngine.create().search(scope, collector, pattern, monitor);

		if (changes.isEmpty()) {
			return null;
		} else {
			CompositeChange result = new CompositeChange("Gurella asset references update");
			changes.values().forEach(result::add);
			return result;
		}
	}

	private final class TextSearchRequestorExtension extends TextSearchRequestor {
		private final Map<IFile, TextFileChange> changes;
		private final String newFileName;

		private TextSearchRequestorExtension(Map<IFile, TextFileChange> changes, String newFileName) {
			this.changes = changes;
			this.newFileName = newFileName;
		}

		@Override
		public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
			IFile file = matchAccess.getFile();
			TextFileChange change = changes.get(file);
			if (change == null) {
				TextChange textChange = getTextChange(file); // an other participant already modified that file?
				if (textChange != null) {
					return false; // don't try to merge changes
				}
				change = new TextFileChange(file.getName(), file);
				change.setEdit(new MultiTextEdit());
				changes.put(file, change);
			}
			ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), newFileName);
			change.addEdit(edit);
			change.addTextEditGroup(new TextEditGroup("Update type reference", edit)); //$NON-NLS-1$
			return true;
		}
	}
}
