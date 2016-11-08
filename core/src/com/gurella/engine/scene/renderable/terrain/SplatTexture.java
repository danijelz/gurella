package com.gurella.engine.scene.renderable.terrain;

import com.badlogic.gdx.graphics.Texture;

/**
 * Copied from https://github.com/mbrlabs/Mundus/blob/master/commons/src/main/com/mbrlabs/mundus/commons/terrain/SplatTexture.java
 * @author Marcus Brummer
 */
public class SplatTexture {
	public Channel channel;
	public Texture texture;

	public SplatTexture(Channel channel, Texture texture) {
		this.channel = channel;
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}

	public enum Channel {
		BASE, R, G, B, A
	}
}
