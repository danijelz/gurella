package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.part.SceneEditorView;

public interface SceneEditorViewClosedListener extends EventSubscription {
	void viewClosed(SceneEditorView view);
}
