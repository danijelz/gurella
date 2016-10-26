package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorMouseListener extends EventSubscription {
	void onMouseSelection(float x, float y);
	
	void onMouseMenu(float x, float y);
}
