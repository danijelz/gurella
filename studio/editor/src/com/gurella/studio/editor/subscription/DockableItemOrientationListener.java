package com.gurella.studio.editor.subscription;

import org.eclipse.swt.widgets.Control;

import com.gurella.engine.event.EventSubscription;

public interface DockableItemOrientationListener extends EventSubscription {
	void itemPositionChanged(Control control, int newOrientation);
}
