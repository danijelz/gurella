package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;

public interface EditorFocusListener extends EventSubscription {
	void focusChanged(EditorFocusData focusData);

	public static class EditorFocusData {
		public final SceneNode focusedNode;
		public final SceneNodeComponent focusedComponent;

		public EditorFocusData(SceneNode focusedNode, SceneNodeComponent focusedComponent) {
			this.focusedNode = focusedNode;
			this.focusedComponent = focusedComponent;
		}
	}
}
