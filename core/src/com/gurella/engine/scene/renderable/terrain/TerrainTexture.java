package com.gurella.engine.scene.renderable.terrain;

import java.util.HashMap;
import java.util.Map;

import com.gurella.engine.scene.renderable.terrain.SplatTexture.Channel;

/**
 * Copied from
 * https://github.com/mbrlabs/Mundus/blob/master/commons/src/main/com/mbrlabs/mundus/commons/terrain/TerrainTexture.java
 * 
 * @author Marcus Brummer
 */
public class TerrainTexture {
	private Map<Channel, SplatTexture> textures;
	private SplatMap splatmap;
	private TerrainComponent terrain;

	public TerrainTexture() {
		textures = new HashMap<Channel, SplatTexture>(5, 1);
	}

	public SplatTexture getTexture(Channel channel) {
		return textures.get(channel);
	}

	public void removeTexture(Channel channel) {
		if (splatmap != null) {
			textures.remove(channel);
			splatmap.clearChannel(channel);
			splatmap.updateTexture();
		}
	}

	public void setSplatTexture(SplatTexture tex) {
		textures.put(tex.channel, tex);
	}

	public Channel getNextFreeChannel() {
		// base
		SplatTexture st = textures.get(Channel.BASE);
		if (st == null)
			return Channel.BASE;
		// r
		st = textures.get(Channel.R);
		if (st == null)
			return Channel.R;
		// g
		st = textures.get(Channel.G);
		if (st == null)
			return Channel.G;
		// b
		st = textures.get(Channel.B);
		if (st == null)
			return Channel.B;
		// a
		st = textures.get(Channel.A);
		if (st == null)
			return Channel.A;

		return null;
	}

	public boolean hasTextureChannel(Channel channel) {
		return textures.containsKey(channel);
	}

	public int countTextures() {
		return textures.size();
	}

	public SplatMap getSplatmap() {
		return splatmap;
	}

	public void setSplatmap(SplatMap splatmap) {
		this.splatmap = splatmap;
	}

	public TerrainComponent getTerrain() {
		return terrain;
	}

	public void setTerrain(TerrainComponent terrain) {
		this.terrain = terrain;
	}
}
