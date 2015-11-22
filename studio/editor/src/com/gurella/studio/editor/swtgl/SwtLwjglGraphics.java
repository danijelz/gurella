package com.gurella.studio.editor.swtgl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SwtLwjglGraphics implements Graphics {
	static int major, minor;

	private final AtomicBoolean glCanvasResizeFlag = new AtomicBoolean(false);

	GL20 gl20;
	GL30 gl30;
	long frameId = -1;
	float deltaTime = 0;
	long frameStart = 0;
	int frames = 0;
	int fps;
	long lastTime = System.nanoTime();
	Composite canvas;
	boolean vsync = false;
	boolean resize = false;
	SwtLwjglApplicationConfiguration config;
	BufferFormat bufferFormat = new BufferFormat(8, 8, 8, 8, 16, 8, 0, false);
	String extensions;
	volatile boolean isContinuous = true;
	volatile boolean requestRendering = false;
	boolean softwareMode;

	private final GLCanvas glCanvas;

	SwtLwjglGraphics(Composite canvas, SwtLwjglApplicationConfiguration config) {
		this.config = config;
		this.canvas = canvas;
		GLData glData = new GLData();
		glData.redSize = config.r;
		glData.greenSize = config.g;
		glData.blueSize = config.b;
		glData.alphaSize = config.a;
		glData.depthSize = config.depth;
		glData.stencilSize = config.stencil;
		glData.samples = config.samples;
		glData.doubleBuffer = true;

		glCanvas = new GLCanvas(canvas, SWT.FLAT, glData);
		if (canvas.getLayout() instanceof GridLayout) {
			glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		}

		glCanvas.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				glCanvasResizeFlag.set(true);
			}
		});
	}

	void setCurrent() {
		if (!glCanvas.isDisposed()) {
			glCanvas.setCurrent();
		}
	}

	void swapBuffer() {
		if (!glCanvas.isDisposed())
			glCanvas.swapBuffers();
	}

	public final GLCanvas getGlCanvas() {
		return glCanvas;
	}

	boolean isResized() {
		return glCanvasResizeFlag.getAndSet(false);
	}

	@Override
	public GL20 getGL20() {
		return gl20;
	}

	@Override
	public int getHeight() {
		return Math.max(1, canvas.getSize().y);
	}

	@Override
	public int getWidth() {
		return Math.max(1, canvas.getSize().x);
	}

	public boolean isGL20Available() {
		return gl20 != null;
	}

	@Override
	public long getFrameId() {
		return frameId;
	}

	@Override
	public float getDeltaTime() {
		return deltaTime;
	}

	@Override
	public float getRawDeltaTime() {
		return deltaTime;
	}

	@Override
	public GraphicsType getType() {
		return GraphicsType.LWJGL;
	}

	@Override
	public int getFramesPerSecond() {
		return fps;
	}

	void updateTime() {
		long time = System.nanoTime();
		deltaTime = (time - lastTime) / 1000000000.0f;
		lastTime = time;

		if (time - frameStart >= 1000000000) {
			fps = frames;
			frames = 0;
			frameStart = time;
		}
		frames++;
	}

	void setupDisplay() throws LWJGLException {
		if (!glCanvas.isDisposed()) {
			glCanvas.setCurrent();
			GLContext.useContext(glCanvas);
		}
		initiateGLInstances();
	}

	public void initiateGLInstances() {
		String version = org.lwjgl.opengl.GL11.glGetString(GL11.GL_VERSION);
		major = Integer.parseInt("" + version.charAt(0));
		minor = Integer.parseInt("" + version.charAt(2));

		if (major >= 3) {
			gl30 = new LwjglGL30();
			gl20 = gl30;
		} else {
			gl20 = new LwjglGL20();
		}

		if (major <= 1)
			throw new GdxRuntimeException(
					"OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + version);
		if (major == 2 || version.contains("2.1")) {
			if (!supportsExtension("GL_EXT_framebuffer_object") && !supportsExtension("GL_ARB_framebuffer_object")) {
				String glInfo = glInfo();
				throw new GdxRuntimeException(
						"OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + version
								+ ", FBO extension: false" + (glInfo.isEmpty() ? "" : ("\n" + glInfo())));
			}
		}

		Gdx.gl = gl20;
		Gdx.gl20 = gl20;
		Gdx.gl30 = gl30;
	}

	private static String glInfo() {
		try {
			return GL11.glGetString(GL11.GL_VENDOR) + "\n" //
					+ GL11.glGetString(GL11.GL_RENDERER) + "\n" //
					+ GL11.glGetString(GL11.GL_VERSION);
		} catch (Throwable ignored) {
		}
		return "";
	}

	@Override
	public float getPpiX() {
		if (glCanvas.isDisposed()) {
			return 0;
		}
		return glCanvas.getDisplay().getDPI().x;
	}

	@Override
	public float getPpiY() {
		if (glCanvas.isDisposed()) {
			return 0;
		}
		return glCanvas.getDisplay().getDPI().x;
	}

	@Override
	public float getPpcX() {
		return getPpiX() / 2.54f;
	}

	@Override
	public float getPpcY() {
		return getPpiY() / 2.54f;
	}

	@Override
	public float getDensity() {
		if (config.overrideDensity != -1)
			return config.overrideDensity / 160f;
		return getPpiX() / 160.0f;
	}

	@Override
	public boolean supportsDisplayModeChange() {
		return false;
	}

	@Override
	public boolean setDisplayMode(DisplayMode displayMode) {
		return false;
	}

	@Override
	public boolean setDisplayMode(int width, int height, boolean fullscreen) {
		return false;
	}

	@Override
	public DisplayMode[] getDisplayModes() {
		return null;
	}

	@Override
	public DisplayMode getDesktopDisplayMode() {
		return null;
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public BufferFormat getBufferFormat() {
		return bufferFormat;
	}

	@Override
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
	}

	@Override
	public boolean supportsExtension(String extension) {
		if (extensions == null) {
			extensions = gl20.glGetString(GL20.GL_EXTENSIONS);
		}
		return extensions.contains(extension);
	}

	@Override
	public void setContinuousRendering(boolean isContinuous) {
		this.isContinuous = isContinuous;
	}

	@Override
	public boolean isContinuousRendering() {
		return isContinuous;
	}

	@Override
	public void requestRendering() {
		synchronized (this) {
			requestRendering = true;
		}
	}

	public boolean shouldRender() {
		synchronized (this) {
			boolean rq = requestRendering;
			requestRendering = false;
			return rq || isContinuous;
		}
	}

	@Override
	public boolean isFullscreen() {
		return false;
	}

	public boolean isSoftwareMode() {
		return softwareMode;
	}

	@Override
	public boolean isGL30Available() {
		return gl30 != null;
	}

	@Override
	public GL30 getGL30() {
		return gl30;
	}

	@Override
	public com.badlogic.gdx.graphics.Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		return new SwtLwjglCursor(pixmap, xHotspot, yHotspot);
	}

	@Override
	public void setCursor(com.badlogic.gdx.graphics.Cursor cursor) {
		if (cursor == null) {
			SwtLwjglCursor.resetCursor();
		} else {
			cursor.setSystemCursor();
		}
	}
}
