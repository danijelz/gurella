package com.gurella.engine.graphics.vector.svg.property.value;

import com.gurella.engine.graphics.vector.svg.Svg;
import com.gurella.engine.graphics.vector.svg.element.Element;
import com.gurella.engine.graphics.vector.svg.element.PaintElement;

public class IriPaint extends Paint {
	private String iri;

	public IriPaint(String iri) {
		this.iri = iri;
	}

	@Override
	public void initCanvasPaint(Svg svg, com.gurella.engine.graphics.vector.Paint canvasPaint, float opacity) {
		doInitCanvasPaint(svg, canvasPaint, opacity);
	}

	private <T extends Element & PaintElement> void doInitCanvasPaint(Svg svg, com.gurella.engine.graphics.vector.Paint canvasPaint, float opacity) {
		T referencedPaintElement = svg.getElement(iri);
		referencedPaintElement.initCanvasPaint(canvasPaint, opacity);
	}
}
