package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.vector.AffineTransform;
import com.gurella.engine.graphics.vector.Gradient;
import com.gurella.engine.graphics.vector.GradientSpread;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;

public abstract class GradientElement extends KnownElement implements PaintElement {
	private final Array<StopElement> stopElements = new Array<StopElement>();
	private final Gradient gradient = Gradient.obtain();
	private boolean gradientDirty = true;

	public GradientElement(ElementType elementType) {
		super(elementType);
	}

	public AffineTransform getGradientTransform() {
		return getPropertyOrDefault(PropertyType.gradientTransform);
	}

	public GradientSpread getGradientSpread() {
		return getPropertyOrDefault(PropertyType.spreadMethod);
	}

	protected Gradient getGradient() {
		if (gradientDirty) {
			initGradient();
		}
		return gradient;
	}

	protected Array<StopElement> getStopElements() {
		if (gradientDirty) {
			initGradient();
		}

		return stopElements;
	}

	private void initGradient() {
		// TODO remove animation listeners from stop elements
		stopElements.clear();
		gradient.reset();
		initStopElements();
		initCanvasGradient();
		gradientDirty = false;
	}

	private void initStopElements() {
		String reference = getProperty(PropertyType.href);
		if (reference == null) {
			Array<Element> children = getChildren();
			for (int i = 0; i < children.size; i++) {
				Element child = children.get(i);
				if (child instanceof StopElement) {
					stopElements.add((StopElement) child);
				}
			}
		} else {
			GradientElement referencedGradient = getSvg().getElement(reference);
			stopElements.addAll(referencedGradient.getStopElements());
		}
	}

	private void initCanvasGradient() {
		gradient.reset();

		for (int i = 0; i < stopElements.size; i++) {
			StopElement stopElement = stopElements.get(i);
			int stopColor = stopElement.getStopColor().getColor();
			float r = ((stopColor & 0x00ff0000) >>> 16) / 255f;
			float g = ((stopColor & 0x0000ff00) >>> 8) / 255f;
			float b = ((stopColor & 0x000000ff)) / 255f;
			float a = stopElement.getStopOpacity();
			float offset = stopElement.getOffset();
			gradient.add(offset, r, g, b, a);
		}
	}
}
