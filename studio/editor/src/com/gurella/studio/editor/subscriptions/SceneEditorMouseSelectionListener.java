package com.gurella.studio.editor.subscriptions;

import com.gurella.engine.event.EventSubscription;

public interface SceneEditorMouseSelectionListener extends EventSubscription {
	void onMouseSelection(float x, float y);
}
