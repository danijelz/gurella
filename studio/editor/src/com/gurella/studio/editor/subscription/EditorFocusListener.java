package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;

public interface EditorFocusListener extends EventSubscription {
	void focusChanged(EditorFocusData focus);

	public static class EditorFocusData {
		public final SceneNode2 focusedNode;
		public final SceneNodeComponent2 focusedComponent;

		public EditorFocusData(SceneNode2 focusedNode, SceneNodeComponent2 focusedComponent) {
			this.focusedNode = focusedNode;
			this.focusedComponent = focusedComponent;
		}
	}
}
