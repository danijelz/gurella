package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorResizeListener extends EventSubscription {
	void resize(int width, int height);
}
