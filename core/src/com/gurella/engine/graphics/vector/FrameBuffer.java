package com.gurella.engine.graphics.vector;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_ATTACHMENT0;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_BINDING;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_COMPLETE;
import static com.badlogic.gdx.graphics.GL20.GL_RENDERBUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_RENDERBUFFER_BINDING;
import static com.badlogic.gdx.graphics.GL20.GL_STENCIL_ATTACHMENT;
import static com.badlogic.gdx.graphics.GL20.GL_STENCIL_INDEX8;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;

import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

class FrameBuffer implements Poolable {
	private static int defaultFrameBufferObject = -1;

	int fbo;
	int rbo;
	Texture texture;
	private boolean ownsTexture; 

	private IntBuffer handle = BufferUtils.newIntBuffer(16);

	private FrameBuffer() {
	}
	
	static FrameBuffer obtain(int width, int height, Format format) {
		FrameBuffer fb = Pools.obtain(FrameBuffer.class);
		if (fb.init(width, height, format)) {
			fb.ownsTexture = true;
			return fb;
		} else {
			fb.free();
			throw new IllegalStateException("frame buffer couldn't be constructed.");
		}
	}
	
	static FrameBuffer obtain(Texture texture) {
		FrameBuffer fb = Pools.obtain(FrameBuffer.class);
		if (fb.init(texture)) {
			return fb;
		} else {
			fb.free();
			throw new IllegalStateException("frame buffer couldn't be constructed.");
		}
	}

	private boolean init(int width, int height, Format format) {
		Texture texture = new Texture(width, height, format);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
		ownsTexture = true;
		return init(texture);
	}

	private boolean init(Texture texture) {
		this.texture = texture;
		
		handle.clear();
		gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, handle);
		int defaultFBO = handle.get(0);
		
		handle.clear();
		gl.glGetIntegerv(GL_RENDERBUFFER_BINDING, handle);
		int defaultRBO = handle.get(0);

		// frame buffer object
		handle.clear();
		gl.glGenFramebuffers(1, handle);
		fbo = handle.get(0);
		gl.glBindFramebuffer(GL_FRAMEBUFFER, fbo);

		// render buffer object
		handle.clear();
		gl.glGenRenderbuffers(1, handle);
		rbo = handle.get(0);
		gl.glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		gl.glRenderbufferStorage(GL_RENDERBUFFER, GL_STENCIL_INDEX8, texture.getWidth(), texture.getHeight());

		// combine all
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureObjectHandle(), 0);
		gl.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
		
		gl.glBindFramebuffer(GL_FRAMEBUFFER, defaultFBO);
		gl.glBindRenderbuffer(GL_RENDERBUFFER, defaultRBO);
		
		return gl.glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
	}

	public void bind() {
		if (defaultFrameBufferObject == -1) {
			handle.clear();
			gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, handle);
			defaultFrameBufferObject = handle.get(0);
		}

		gl.glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}
	
	public void unbind () {
		gl.glBindFramebuffer(GL_FRAMEBUFFER, defaultFrameBufferObject);
	}
	
	public Texture getTexture() {
		return texture;
	}

	@Override
	public void reset() {
		if (fbo != 0) {
			handle.clear();
			handle.put(fbo);
			handle.flip();
			gl.glDeleteFramebuffers(1, handle);
			fbo = 0;
		}
		
		if (rbo != 0) {
			handle.clear();
			handle.put(rbo);
			handle.flip();
			gl.glDeleteRenderbuffers(1, handle);
			rbo = 0;
		}
		
		if (texture != null) {
			if(ownsTexture) {
				texture.dispose();
			}
			texture = null;
		}
		
		ownsTexture = false;
	}

	public void free() {
		Pools.free(this);
	}
}
