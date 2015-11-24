package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Image;
import com.gurella.engine.graphics.vector.svg.SvgRenderContext;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;

public class ImageElement extends KnownElement implements RenderableElement {
	private Image resolvedImage;
	
	public ImageElement() {
		super(ElementType.image);
	}

	public float getX() {
		return this.<Length> getPropertyOrDefault(PropertyType.x).getPixels();
	}

	public float getY() {
		return this.<Length> getPropertyOrDefault(PropertyType.y).getPixels();
	}

	public float getWidth() {
		return this.<Length> getProperty(PropertyType.width).getPixels();
	}

	public float getHeight() {
		return this.<Length> getProperty(PropertyType.height).getPixels();
	}
	
	public PreserveAspectRatio getPreserveAspectRatio() {
		return getPropertyOrDefault(PropertyType.preserveAspectRatio);
	}
	
	public String getImageIri() {
		return getProperty(PropertyType.href);
	}
	
	@Override
	public void renderGeometry(SvgRenderContext state) {
		float width = getWidth();
		if(width == 0) {
			return;
		}
		
		float height = getHeight();
		if(height == 0) {
			return;
		}
		
		Image image = resolveImage();
		if(image == null) {
			return;
		}
		
		//TODO preserveAspectRatio
		PreserveAspectRatio preserveAspectRatio = getPropertyOrDefault(PropertyType.preserveAspectRatio);
		
		state.canvas.drawImage(image, getX(), getY(), width, height);
	}
	
	private Image resolveImage() {
		if(resolvedImage == null) {
			String imageIri = getImageIri();
			if(imageIri == null) {
				return null;
			}
			Texture texture = svg.resolveTexture(imageIri);
			resolvedImage = Image.obtain(texture, false, false, false);
		}
		return resolvedImage;
	}

	@Override
	protected Rectangle getElementBounds(Rectangle out, AffineTransform worldTransform) {
		float width = getWidth();
		if (width <= 0 || width == Float.NaN) {
			return out;
		}

		float height = getHeight();
		if (height <= 0 || height == Float.NaN) {
			return out;
		}

		float x = getX();
		float y = getY();

		Vector2 point = Pools.obtain(Vector2.class);
		point.set(x, y);
		worldTransform.transform(point);
		float xMin = point.x;
		float yMin = point.y;
		float xMax = point.x;
		float yMax = point.y;

		point.set(x, y + height);
		worldTransform.transform(point);
		xMin = Math.min(xMin, point.x);
		yMin = Math.min(yMin, point.y);
		xMax = Math.max(xMax, point.x);
		yMax = Math.max(yMax, point.y);

		point.set(x + width, y + height);
		worldTransform.transform(point);
		xMin = Math.min(xMin, point.x);
		yMin = Math.min(yMin, point.y);
		xMax = Math.max(xMax, point.x);
		yMax = Math.max(yMax, point.y);

		point.set(x + width, y);
		worldTransform.transform(point);
		xMin = Math.min(xMin, point.x);
		yMin = Math.min(yMin, point.y);
		xMax = Math.max(xMax, point.x);
		yMax = Math.max(yMax, point.y);

		out.set(xMin, yMin, xMax - xMin, yMax - yMin);
		Pools.free(point);

		return out;
	}
}
