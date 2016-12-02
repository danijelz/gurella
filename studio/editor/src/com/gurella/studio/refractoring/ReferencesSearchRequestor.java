package com.gurella.studio.refractoring;

import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

final class ReferencesSearchRequestor extends TextSearchRequestor {
	private final boolean txtFilesHandled;
	private final Map<IFile, TextFileChange> changes;
	private final String replacement;

	ReferencesSearchRequestor(boolean txtFilesHandled, Map<IFile, TextFileChange> changes, String replacement) {
		this.txtFilesHandled = txtFilesHandled;
		this.changes = changes;
		this.replacement = replacement;
	}

	@Override
	public boolean canRunInParallel() {
		return true;
	}

	@Override
	public boolean acceptFile(IFile file) throws CoreException {
		return !(txtFilesHandled
				&& FileBuffers.getTextFileBufferManager().isTextFileLocation(file.getFullPath(), false));
	}

	@Override
	public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
		IFile file = matchAccess.getFile();
		TextFileChange change;
		synchronized (changes) {
			change = changes.get(file);
			if (change == null) {
				change = new TextFileChange(file.getName(), file);
				change.setEdit(new MultiTextEdit());
				changes.put(file, change);
			}
		}

		ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), replacement);
		change.addEdit(edit);
		// TODO convert to single change and insert in one group
		change.addTextEditGroup(new TextEditGroup("Update asset reference", edit));
		return true;
	}
}