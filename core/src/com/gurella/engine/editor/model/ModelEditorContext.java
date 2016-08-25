package com.gurella.engine.editor.model;

import com.gurella.engine.base.model.Model;

public interface ModelEditorContext<T> {
	Model<T> model();

	T getModelInstance();
}
