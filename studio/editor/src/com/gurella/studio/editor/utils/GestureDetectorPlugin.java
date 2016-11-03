package com.gurella.studio.editor.utils;

import com.badlogic.gdx.input.GestureDetector;
import com.gurella.engine.plugin.Plugin;

public class GestureDetectorPlugin extends GestureDetector implements Plugin {
	public GestureDetectorPlugin(GestureListener listener) {
		super(listener);
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}
}
