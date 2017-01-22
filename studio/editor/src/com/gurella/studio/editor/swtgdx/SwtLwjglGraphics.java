package com.gurella.studio.editor.swtgdx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.studio.editor.utils.Synchronized;
import com.gurella.studio.editor.utils.Try;

public class SwtLwjglGraphics implements Graphics {
	private static final int r = 8, g = 8, b = 8, a = 8;
	private static final int depth = 16, stencil = 0;
	private static final int samples = 0;

	GL20 gl20;
	GL30 gl30;
	long lastTime;

	private long frameId = -1;
	private float deltaTime = 0;
	private long frameStart = 0;
	private int frames = 0;
	private int fps;
	private final BufferFormat bufferFormat = new BufferFormat(8, 8, 8, 8, 16, 8, 0, false);
	private String extensions;
	private volatile boolean isContinuous = true;
	private volatile boolean requestRendering = false;
	private boolean softwareMode;

	private int sizeX;
	private int sizeY;

	private GLVersion glVersion;
	private GLCanvas glCanvas;

	private final Object mutex = new Object();

	SwtLwjglGraphics(Composite parentComposite) {
		GLData glData = new GLData();
		glData.redSize = r;
		glData.greenSize = g;
		glData.blueSize = b;
		glData.alphaSize = a;
		glData.depthSize = depth;
		glData.stencilSize = stencil;
		glData.samples = samples;
		glData.doubleBuffer = true;

		Point size = parentComposite.getSize();
		sizeX = size.x;
		sizeY = size.y;

		glCanvas = new GLCanvas(parentComposite, SWT.FLAT, glData);
		glCanvas.setCurrent();
		glCanvas.addListener(SWT.Resize, e -> updateSize());
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		setContext();
		initGlInstances();

		lastTime = System.nanoTime();
	}

	void setContext() {
		if (glCanvas.isDisposed()) {
			return;
		}

		glCanvas.setCurrent();
		Try.unchecked(() -> GLContext.useContext(glCanvas));
	}

	public void initGlInstances() {
		String version = GL11.glGetString(GL11.GL_VERSION);
		String vendor = GL11.glGetString(GL11.GL_VENDOR);
		String renderer = GL11.glGetString(GL11.GL_RENDERER);
		int major = Character.getNumericValue(version.charAt(0));

		if (major < 2) {
			throw new GdxRuntimeException(
					"OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + version);
		} else if (major > 2) {
			gl30 = new LwjglGL30();
			gl20 = gl30;
		} else if (supportsExtension("GL_EXT_framebuffer_object") || supportsExtension("GL_ARB_framebuffer_object")) {
			gl20 = new LwjglGL20();
		} else {
			throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: '"
					+ version + "', FBO extension: 'false', vendor: '" + vendor + "', renderer: '" + renderer + "'.");
		}

		glVersion = new GLVersion(Application.ApplicationType.Desktop, version, vendor, renderer);
	}

	private void updateSize() {
		Point size = glCanvas.getSize();
		sizeX = size.x;
		sizeY = size.y;
	}

	void setCurrent() {
		if (!glCanvas.isDisposed()) {
			glCanvas.setCurrent();
		}
	}

	void swapBuffer() {
		if (!glCanvas.isDisposed()) {
			glCanvas.swapBuffers();
		}
	}

	// TODO make package private
	public final GLCanvas getGlCanvas() {
		return glCanvas;
	}

	@Override
	public GL20 getGL20() {
		return gl20;
	}

	@Override
	public int getWidth() {
		return Math.max(1, sizeX);
	}

	@Override
	public int getHeight() {
		return Math.max(1, sizeY);
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

	void update() {
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

	@Override
	public float getPpiX() {
		return glCanvas.isDisposed() ? 0 : glCanvas.getDisplay().getDPI().x;
	}

	@Override
	public float getPpiY() {
		return glCanvas.isDisposed() ? 0 : glCanvas.getDisplay().getDPI().y;
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
		return getPpiX() / 160.0f;
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
		Synchronized.run(mutex, () -> requestRendering = true);
	}

	boolean shouldRender() {
		synchronized (mutex) {
			boolean shouldRender = requestRendering;
			requestRendering = false;
			return shouldRender || isContinuous;
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
		return new SwtLwjglCursor(glCanvas.getDisplay(), pixmap, xHotspot, yHotspot);
	}

	@Override
	public void setCursor(com.badlogic.gdx.graphics.Cursor cursor) {
		if (cursor instanceof SwtLwjglCursor) {
			SwtLwjglCursor swtLwjglCursor = (SwtLwjglCursor) cursor;
			glCanvas.setCursor(swtLwjglCursor.swtCursor);
		} else {
			glCanvas.setCursor(null);
		}
	}

	@Override
	public int getBackBufferHeight() {
		return getHeight();
	}

	@Override
	public int getBackBufferWidth() {
		return getWidth();
	}

	@Override
	public DisplayMode getDisplayMode() {
		return null;
	}

	@Override
	public DisplayMode getDisplayMode(Monitor arg0) {
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor arg0) {
		return null;
	}

	@Override
	public boolean supportsDisplayModeChange() {
		return false;
	}

	@Override
	public DisplayMode[] getDisplayModes() {
		return null;
	}

	@Override
	public Monitor getMonitor() {
		return getPrimaryMonitor();
	}

	@Override
	public Monitor[] getMonitors() {
		return new Monitor[] { getPrimaryMonitor() };
	}

	@Override
	public Monitor getPrimaryMonitor() {
		return new SwtLwjglMonitor(0, 0, "Primary Monitor");
	}

	@Override
	public boolean setFullscreenMode(DisplayMode arg0) {
		glCanvas.getShell().setFullScreen(true);
		return true;
	}

	@Override
	public void setSystemCursor(SystemCursor arg0) {
		glCanvas.setCursor(null);
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		glCanvas.getShell().setFullScreen(false);
		return true;
	}

	private class SwtLwjglMonitor extends Monitor {
		protected SwtLwjglMonitor(int virtualX, int virtualY, String name) {
			super(virtualX, virtualY, name);
		}
	}

	@Override
	public GLVersion getGLVersion() {
		return glVersion;
	}

	@Override
	public void setResizable(boolean arg0) {
	}

	@Override
	public void setUndecorated(boolean arg0) {
	}
}
