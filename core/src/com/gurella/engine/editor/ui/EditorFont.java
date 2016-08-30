package com.gurella.engine.editor.ui;

import com.badlogic.gdx.utils.Disposable;

public interface EditorFont extends Disposable {
	String getName();

	int getHeight();

	boolean isBold();

	boolean isItalic();

	boolean isDisposed();
}
