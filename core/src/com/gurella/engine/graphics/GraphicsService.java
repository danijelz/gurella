package com.gurella.engine.graphics;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ObjectSet;
import com.gurella.engine.graphics.render.command.RenderComandBuffer;

//TODO unused
public class GraphicsService {
	private static GL20 gl20;
	private static GL30 gl30;

	private static int maxTextureImageUnits;
	private static ObjectSet<String> glExtensions = new ObjectSet<String>();

	private static IntBuffer buffer = BufferUtils.newIntBuffer(16);

	public static void init() {
		gl20 = Gdx.gl20;
		gl30 = Gdx.gl30;

		if (gl30 == null) {
			String result = gl20.glGetString(GL20.GL_EXTENSIONS);
			String[] extensions = result.split(" ");
			for (int i = 0, n = extensions.length; i < n; ++i) {
				glExtensions.add(extensions[i].trim());
			}
		} else {
			gl30.glGetIntegerv(GL30.GL_NUM_EXTENSIONS, buffer);
			for (int i = 0, n = buffer.get(0); i < n; ++i) {
				glExtensions.add(gl30.glGetStringi(GL20.GL_EXTENSIONS, i).trim());
			}
		}

		Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
		maxTextureImageUnits = buffer.get(0);

		// TODO Auto-generated method stub
	}

	public void render(RenderComandBuffer comandBuffer) {

	}
	
	public static int getMaxTextureImageUnits() {
		return maxTextureImageUnits;
	}
	
	public static ObjectSet<String> getGlExtensions() {
		return glExtensions;
	}
	
	public static boolean isGL30Available() {
		return gl30 != null;
	}
}
