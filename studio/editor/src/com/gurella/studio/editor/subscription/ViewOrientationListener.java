package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.control.DockableView;

public interface ViewOrientationListener extends EventSubscription {
	void orientationChanged(DockableView view, int newOrientation);
}
