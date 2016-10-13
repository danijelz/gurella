package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface SceneEditorMouseListener extends EventSubscription {
	void onMouseSelection(float x, float y);
	
	void onMouseMenu(float x, float y);
}
