package com.gurella.studio.editor.render;

import com.gurella.engine.scene.debug.DebugRenderable;

//TODO unused
public class DebugRenderConfig {
	public final Class<? extends DebugRenderable> type;
	public final RenderSeverity severity;

	public DebugRenderConfig(Class<? extends DebugRenderable> type, RenderSeverity severity) {
		this.type = type;
		this.severity = severity;
	}

	public enum RenderSeverity {
		always, nodeFocus, componentFocus, never;
	}
}
