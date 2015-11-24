package com.gurella.engine.graphics.vector;

import com.badlogic.gdx.graphics.GL20;

public enum VertexMode {
	triangles(GL20.GL_TRIANGLES), triangleFan(GL20.GL_TRIANGLE_FAN), triangleStrip(GL20.GL_TRIANGLE_STRIP);
	
	public final int glMode;
	
	VertexMode(int glMode) {
		this.glMode = glMode;
	}
}
