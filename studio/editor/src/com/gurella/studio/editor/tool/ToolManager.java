package com.gurella.studio.editor.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class ToolManager extends InputAdapter {
	private final int editorId;
	private Camera toolCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	ScaleTool scaleTool = new ScaleTool();

	public ToolManager(int editorId) {
		this.editorId = editorId;
	}
}
