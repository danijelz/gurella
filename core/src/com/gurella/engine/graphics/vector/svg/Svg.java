package com.gurella.engine.graphics.vector.svg;

import java.io.InputStream;
import java.io.Reader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.graphics.vector.svg.element.Element;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;

//TODO poolable
public class Svg {
	public ExternalFileResolver externalFileResolver;
	
	private ObjectMap<String, Element> elementsById = new ObjectMap<String, Element>();
	Element root;// TODO

	public static Svg parse(String xml) {
		SvgReader reader = Pools.obtain(SvgReader.class);
		Svg svg = reader.parse(xml);
		Pools.free(reader);
		return svg;
	}

	public static Svg parse(Reader reader) {
		SvgReader svgReader = Pools.obtain(SvgReader.class);
		Svg svg = svgReader.parse(reader);
		Pools.free(svgReader);
		return svg;
	}

	public static Svg parse(InputStream input) {
		SvgReader reader = Pools.obtain(SvgReader.class);
		Svg svg = reader.parse(input);
		Pools.free(reader);
		return svg;
	}

	public static Svg parse(FileHandle file) {
		SvgReader reader = Pools.obtain(SvgReader.class);
		Svg svg = reader.parse(file);
		Pools.free(reader);
		return svg;
	}

	void addElement(Element element) {
		String id = element.getId();
		if (id != null) {
			elementsById.put(id, element);
		}
	}

	public <T extends Element> T getElement(String reference) {
		if (reference == null) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			T casted = (T) elementsById.get(extractReferenceValue(reference));
			return casted;
		}
	}

	private static String extractReferenceValue(String reference) {
		if (reference.startsWith("#")) {
			return reference.substring(1);
		} else if (reference.startsWith("url(#")) {
			return reference.substring(5, reference.length() - 1);
		} else {
			return reference;
		}
	}
	
	private ExternalFileResolver getNonnullExternalFileResolver() {
		return externalFileResolver == null ? ExternalFileResolver.defaultInstance : externalFileResolver;
	}
	
	public Texture resolveTexture(String reference) {
		return getNonnullExternalFileResolver().resolveImage(extractReferenceValue(reference));
	}
	
	/*public Texture renderToTexture() { TODO
		FrameBuffer frameBuffer = FrameBuffer.newInstance(renderContext.canvas, (int)root.getWidth(), (int)root.getHeight(), 0);
		frameBuffer.bind();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
		render(root);
		frameBuffer.unbind();
		Texture texture = frameBuffer.getTexture();
		frameBuffer.free();
		return texture;
	}*/

	/*public void render() {
		int width = (int) root.getWidth();
		int height = (int) root.getHeight();
		render(width, height);
	}
	
	public void render(int width, int height) {
		renderContext.canvas.clear();
		renderContext.canvas.beginFrame(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f);
		root.render(renderContext, width, height);
		renderContext.canvas.endFrame();
	}

	public void render(int width, int height, PreserveAspectRatio preserveAspectRatio) {
		renderContext.canvas.clear();
		renderContext.canvas.beginFrame(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f);
		root.render(renderContext, width, height, preserveAspectRatio);
		renderContext.canvas.endFrame();
	}
	
	public void render(String id) {
		render(getElement(id));
	}
	
	public void render(String id, Rectangle viewPort, PreserveAspectRatio preserveAspectRatio, boolean overflow) {
		render(getElement(id), viewPort, preserveAspectRatio, overflow);
	}

	private void render(Element element) {
		renderContext.canvas.clear();
		element.render(renderContext);
	}
	
	public void render(Element element, float scale) {
		Rectangle bounds = Pools.obtain(Rectangle.class);
		element.getBounds(bounds);
		render(element, new Rectangle(0, 0, bounds.width * scale, bounds.height * scale), PreserveAspectRatio.STRETCH);
		Pools.free(bounds);
	}
	
	public void render(Element element, int width, int height) {
		Rectangle bounds = Pools.obtain(Rectangle.class).set(0, 0, width, height);
		render(element, bounds, PreserveAspectRatio.STRETCH);
		Pools.free(bounds);
	}
	
	public void render(Element element, Rectangle viewPort) {
		render(element, viewPort, PreserveAspectRatio.STRETCH);
	}
	
	public void render(Element element, Rectangle viewPort, PreserveAspectRatio preserveAspectRatio) {
		render(element, viewPort, preserveAspectRatio, false);
	}
	
	public void render(Element element, Rectangle viewPort, PreserveAspectRatio preserveAspectRatio, boolean overflow) {
		renderContext.canvas.clear();
		renderContext.canvas.beginFrame(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f);// TODO
																								// buffer
		renderContext.viewPort = viewPort;
		Rectangle bounds = Pools.obtain(Rectangle.class);
		updateViewBox(renderContext, element.getBounds(bounds), preserveAspectRatio, overflow);
		Pools.free(bounds);
		element.render(renderContext);
		renderContext.canvas.endFrame();
	}*/
}
