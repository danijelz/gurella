package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.control.DockableView;

//TODO convert to Plugin
public interface ViewActivityListener extends EventSubscription {
	void viewOpened(DockableView view);

	void viewClosed(DockableView view);
}
