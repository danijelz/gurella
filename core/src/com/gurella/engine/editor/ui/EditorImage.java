package com.gurella.engine.editor.ui;

import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.math.GridRectangle;

public interface EditorImage extends Disposable {
	GridRectangle getBounds();

	boolean isDisposed();
}
