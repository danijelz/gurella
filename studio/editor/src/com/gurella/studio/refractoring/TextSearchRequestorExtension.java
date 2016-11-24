package com.gurella.studio.refractoring;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

final class TextSearchRequestorExtension extends TextSearchRequestor {
	private final RefactoringParticipant participant;
	private final Map<IFile, TextFileChange> changes;
	private final String newFileName;

	TextSearchRequestorExtension(RefactoringParticipant participant, Map<IFile, TextFileChange> changes,
			String newFileName) {
		this.participant = participant;
		this.changes = changes;
		this.newFileName = newFileName;
	}

	@Override
	public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
		IFile file = matchAccess.getFile();
		TextFileChange change = changes.get(file);
		if (change == null) {
			TextChange textChange = participant.getTextChange(file);
			if (textChange != null) {
				return false;
			}
			change = new TextFileChange(file.getName(), file);
			change.setEdit(new MultiTextEdit());
			changes.put(file, change);
		}
		ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), newFileName);
		change.addEdit(edit);
		change.addTextEditGroup(new TextEditGroup("Update asset reference", edit));
		return true;
	}
}