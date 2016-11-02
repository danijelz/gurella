package com.gurella.studio.editor.subscription;

import com.badlogic.gdx.InputProcessor;
import com.gurella.engine.event.EventSubscription;

public interface InputProcessorActivationListener extends EventSubscription {
	void activate(InputProcessor processor);

	void deactivate(InputProcessor processor);
}
