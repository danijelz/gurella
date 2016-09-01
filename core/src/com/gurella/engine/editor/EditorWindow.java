package com.gurella.engine.editor;

import com.gurella.engine.editor.ui.EditorShell;

//TODO unused
public interface EditorWindow {
	boolean close();

	void create();

	int getReturnCode();

	EditorShell getShell();

	int open();

	void setBlockOnOpen(boolean shouldBlock);
}
