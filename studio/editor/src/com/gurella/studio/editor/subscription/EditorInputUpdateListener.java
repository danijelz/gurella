package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorInputUpdateListener extends EventSubscription {
	void onInputUpdate();
}
