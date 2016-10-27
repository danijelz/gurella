package com.gurella.studio.editor.tool;

import com.badlogic.gdx.utils.Disposable;

public abstract class ToolHandle implements Disposable {
	int id;

	public ToolHandle(int id) {
		super();
		this.id = id;
	}
}
