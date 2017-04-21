package com.gurella.engine.graphics;

import java.nio.IntBuffer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.gurella.engine.graphics.render.gl.GlContext;
import com.gurella.engine.utils.ImmutableArray;

public class GraphicsService {
	public final static int MAX_GLES_UNITS = 32;

	private static GL20 gl20;
	private static GL30 gl30;

	private static int defaultFramebufferHandle;
	private static int maxTextureImageUnits;
	private static int maxRenderbufferSize;
	private static int maxTextureSize;
	private static int maxCubeMapTextureSize;
	private static int maxMaxCombinedTextureImageUnits;
	private static int maxMaxVaryingVectors;
	private static int maxVertexAttribs;
	private static int maxVertexTextureImageUnits;
	private static int maxVertexUniformVectors;
	private static int maxFragmentUniformVectors;
	private static int maxViewportDimsX;
	private static int maxViewportDimsY;
	private static int aliasedLineWidthRange;
	private static int aliasedPointSizeRange;
	private static int maxColorAttachments = 1;
	private static int maxDrawBuffers = 1;

	private static final Array<String> _glExtensions = new Array<String>();
	private static final ImmutableArray<String> glExtensions = new ImmutableArray<String>(_glExtensions);
	private static final IntBuffer buffer = BufferUtils.newIntBuffer(16);
	private static final GlContext context = new GlContext();

	public static void init() {
		synchronized (context) {
			if (gl20 != null) {
				return;
			}
			gl20 = Gdx.gl20;
			gl30 = Gdx.gl30;
		}

		boolean isGl30Capable = gl30 != null;

		if (Gdx.app.getType() == ApplicationType.iOS) {
			gl20.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, buffer);
			defaultFramebufferHandle = buffer.get(0);
		} else {
			defaultFramebufferHandle = 0;
		}

		if (isGl30Capable) {
			gl30.glGetIntegerv(GL30.GL_NUM_EXTENSIONS, buffer);
			for (int i = 0, n = buffer.get(0); i < n; ++i) {
				_glExtensions.add(gl30.glGetStringi(GL20.GL_EXTENSIONS, i).trim());
			}
		} else {
			String result = gl20.glGetString(GL20.GL_EXTENSIONS);
			String[] extensions = result.split(" ");
			for (int i = 0, n = extensions.length; i < n; ++i) {
				_glExtensions.add(extensions[i].trim());
			}
		}

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
		maxTextureImageUnits = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_RENDERBUFFER_SIZE, buffer);
		maxRenderbufferSize = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buffer);
		maxTextureSize = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_CUBE_MAP_TEXTURE_SIZE, buffer);
		maxCubeMapTextureSize = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, buffer);
		maxMaxCombinedTextureImageUnits = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VARYING_VECTORS, buffer);
		maxMaxVaryingVectors = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VERTEX_ATTRIBS, buffer);
		maxVertexAttribs = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, buffer);
		maxVertexTextureImageUnits = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VERTEX_UNIFORM_VECTORS, buffer);
		maxVertexUniformVectors = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_FRAGMENT_UNIFORM_VECTORS, buffer);
		maxFragmentUniformVectors = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_VIEWPORT_DIMS, buffer);
		maxViewportDimsX = buffer.get(0);
		maxViewportDimsY = buffer.get(1);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_ALIASED_LINE_WIDTH_RANGE, buffer);
		aliasedLineWidthRange = buffer.get(0);
		buffer.clear();

		Gdx.gl.glGetIntegerv(GL20.GL_ALIASED_POINT_SIZE_RANGE, buffer);
		aliasedPointSizeRange = buffer.get(0);
		buffer.clear();

		if (isGl30Capable) {
			Gdx.gl.glGetIntegerv(GL30.GL_MAX_COLOR_ATTACHMENTS, buffer);
			maxColorAttachments = buffer.get(0);
			buffer.clear();

			Gdx.gl.glGetIntegerv(GL30.GL_MAX_DRAW_BUFFERS, buffer);
			maxDrawBuffers = buffer.get(0);
			buffer.clear();
		}
	}

	public static ImmutableArray<String> getGlExtensions() {
		return glExtensions;
	}

	public static boolean isGl30Available() {
		return gl30 != null;
	}

	public static int getDefaultFramebufferHandle() {
		return defaultFramebufferHandle;
	}

	public static int getMaxTextureImageUnits() {
		return maxTextureImageUnits;
	}

	public static int getMaxGlesTextureImageUnits() {
		return Math.min(getMaxTextureImageUnits(), MAX_GLES_UNITS);
	}

	public static int getMaxRenderbufferSize() {
		return maxRenderbufferSize;
	}

	public static int getMaxTextureSize() {
		return maxTextureSize;
	}

	public static int getMaxCubeMapTextureSize() {
		return maxCubeMapTextureSize;
	}

	public static int getMaxMaxCombinedTextureImageUnits() {
		return maxMaxCombinedTextureImageUnits;
	}

	public static int getMaxMaxVaryingVectors() {
		return maxMaxVaryingVectors;
	}

	public static int getMaxVertexAttribs() {
		return maxVertexAttribs;
	}

	public static int getMaxVertexTextureImageUnits() {
		return maxVertexTextureImageUnits;
	}

	public static int getMaxVertexUniformVectors() {
		return maxVertexUniformVectors;
	}

	public static int getMaxFragmentUniformVectors() {
		return maxFragmentUniformVectors;
	}

	public static int getAliasedLineWidthRange() {
		return aliasedLineWidthRange;
	}

	public static int getAliasedPointSizeRange() {
		return aliasedPointSizeRange;
	}

	public static int getMaxViewportDimsX() {
		return maxViewportDimsX;
	}

	public static int getMaxViewportDimsY() {
		return maxViewportDimsY;
	}

	public static GridPoint2 getMaxViewportDims(GridPoint2 out) {
		return out.set(maxViewportDimsX, maxViewportDimsY);
	}

	public static int getMaxColorAttachments() {
		return maxColorAttachments;
	}

	public static int getMaxDrawBuffers() {
		return maxDrawBuffers;
	}

	public static void render(GraphicsTask task) {
		synchronized (context) {
			context.activate();
			task.run(context);
			context.deactivate();
		}
	}

	public interface GraphicsTask {
		void run(GlContext context);
	}
}
