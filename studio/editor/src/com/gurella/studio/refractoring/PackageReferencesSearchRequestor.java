package com.gurella.studio.refractoring;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

public class PackageReferencesSearchRequestor extends TextSearchRequestor {
	private final Map<IFile, TextFileChange> changes;
	private final String replacement;

	PackageReferencesSearchRequestor(Map<IFile, TextFileChange> changes, String replacement) {
		this.changes = changes;
		this.replacement = replacement;
	}

	@Override
	public boolean canRunInParallel() {
		return true;
	}

	@Override
	public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
		int start = matchAccess.getMatchOffset();
		int length = matchAccess.getMatchLength();
		int fileContentLength = matchAccess.getFileContentLength();
		int end = start + length;
		if (end < fileContentLength) {
			char after = matchAccess.getFileContentChar(end);
			if (Character.isJavaIdentifierPart(after)) {
				return true;
			}
		}

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
		change.addTextEditGroup(new TextEditGroup("Update asset reference", edit));
		return true;
	}
}
