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

import com.gurella.engine.utils.Uuid;

public class GenerateUuidRequestor extends TextSearchRequestor {
	private final Map<IFile, TextFileChange> changes;
	private final IFile copy;

	GenerateUuidRequestor(Map<IFile, TextFileChange> changes, IFile copy) {
		this.changes = changes;
		this.copy = copy;
	}

	@Override
	public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
		TextFileChange change = changes.get(copy);
		if (change == null) {
			change = new TextFileChange(copy.getName(), copy);
			change.setEdit(new MultiTextEdit());
			changes.put(copy, change);
		}

		String uuid = Uuid.randomUuidString();
		ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), uuid);
		change.addEdit(edit);
		change.addTextEditGroup(new TextEditGroup("Update asset uuid", edit));
		return true;
	}
}
