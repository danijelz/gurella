package com.gurella.studio.editor.utils;

import com.badlogic.gdx.input.GestureDetector;
import com.gurella.engine.utils.plugin.Plugin;

public class GestureDetectorPlugin extends GestureDetector implements Plugin {
	public GestureDetectorPlugin(GestureListener listener) {
		super(5, 0.4f, 1.1f, 0.15f, listener);
	}
}
