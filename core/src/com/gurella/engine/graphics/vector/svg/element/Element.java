package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Canvas;
import com.gurella.engine.graphics.vector.svg.Svg;
import com.gurella.engine.graphics.vector.svg.SvgRenderContext;
import com.gurella.engine.graphics.vector.svg.property.Properties;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Display;
import com.gurella.engine.graphics.vector.svg.property.value.Visibility;

//TODO poolable
public abstract class Element {
	Svg svg;
	private Element parent;
	private Properties properties = new Properties();
	Array<Element> children = new Array<Element>();

	public abstract String getName();

	public String getId() {
		return properties.getProperty(PropertyType.id);
	}

	public String getCssClass() {
		return properties.getProperty(PropertyType.cssClass);
	}

	public Svg getSvg() {
		return svg;
	}

	public Element getParent() {
		return parent;
	}

	public Array<Element> getChildren() {
		return children;
	}

	public void addChild(Element child) {
		child.parent = this;
		child.addParentProperties(properties);
		children.add(child);
	}

	private void addParentProperties(Properties parentProperties) {
		properties.addParentProperties(parentProperties);
	}

	public <T> T getProperty(PropertyType property) {
		return properties.getProperty(property);
	}

	public <T> T getPropertyOrDefault(PropertyType property) {
		return properties.getPropertyOrDefault(property);
	}

	public void setProperty(String propertyName, String value) {
		properties.setProperty(propertyName, value);
	}

	public boolean isDisplayed() {
		return Display.none != getDisplay();
	}

	public Display getDisplay() {
		return getPropertyOrDefault(PropertyType.display);
	}

	public boolean isVisible() {
		return Visibility.visible == getPropertyOrDefault(PropertyType.visibility);
	}

	public Visibility getVisibility() {
		return getPropertyOrDefault(PropertyType.visibility);
	}

	public final void render(SvgRenderContext state) {
		if (isDisplayed()) {
			renderHierarchy(state);
		}
	}

	protected void renderHierarchy(SvgRenderContext state) {
		saveRenderState(state);
		
		if (isVisible() && this instanceof RenderableElement) {
			((RenderableElement) this).renderGeometry(state);
		}

		for (Element child : children) {
			if (child instanceof RenderableElement) {
				child.render(state);
			}
		}

		restoreRenderState(state);
	}

	private void saveRenderState(SvgRenderContext state) {
		Canvas canvas = state.canvas;
		canvas.saveState(true);
		AffineTransform transform = getPropertyOrDefault(PropertyType.transform);
		canvas.preMultiplyTransform(transform);
		// TODO clips
	}

	private void restoreRenderState(SvgRenderContext state) {
		state.canvas.restoreState();
	}

	public Rectangle getBounds(Rectangle out) {
		AffineTransform transform = AffineTransform.obtain();
		getBounds(out.set(Float.NaN, Float.NaN, Float.NaN, Float.NaN), transform);
		Pools.free(transform);

		if (out.x == Float.NaN) {
			out.set(0, 0, 0, 0);
		}

		return out;
	}

	private Rectangle getBounds(Rectangle out, AffineTransform parentTransform) {
		if (!isDisplayed()) {
			return out;
		}

		AffineTransform worldTransform = getWorldTransform(parentTransform);
		getElementBounds(out, worldTransform);
		getChildrenBounds(out, worldTransform);
		Pools.free(worldTransform);
		return out;
	}

	private void getChildrenBounds(Rectangle out, AffineTransform worldTransform) {
		Rectangle tempBounds = Pools.obtain(Rectangle.class);
		for (int i = 0; i < children.size; i++) {
			Element child = children.get(i);
			tempBounds.set(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
			child.getBounds(tempBounds, worldTransform);

			if (tempBounds.x != Float.NaN) {
				if (out.x == Float.NaN) {
					out.set(tempBounds);
				} else {
					out.merge(tempBounds);
				}
			}
		}
		Pools.free(tempBounds);
	}

	private AffineTransform getWorldTransform(AffineTransform parentTransform) {
		AffineTransform worldTransform = AffineTransform.obtain().set(parentTransform);
		return worldTransform.mulLeft(this.<AffineTransform> getPropertyOrDefault(PropertyType.transform));
	}

	protected Rectangle getElementBounds(Rectangle out, AffineTransform worldTransform) {
		return out;
	}
}
