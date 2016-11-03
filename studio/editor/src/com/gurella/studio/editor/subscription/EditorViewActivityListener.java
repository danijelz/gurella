package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.control.DockableView;

//TODO replace with plugins
public interface EditorViewActivityListener extends EventSubscription {
	void viewClosed(DockableView view);

	void viewOpened(DockableView view);
}
