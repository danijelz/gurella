package com.gurella.engine.graphics.vector.svg.element;

import com.badlogic.gdx.math.Rectangle;
import com.gurella.engine.graphics.vector.svg.property.PropertyType;
import com.gurella.engine.graphics.vector.svg.property.value.Length;
import com.gurella.engine.graphics.vector.svg.property.value.MarkerUnits;
import com.gurella.engine.graphics.vector.svg.property.value.Overflow;
import com.gurella.engine.graphics.vector.svg.property.value.PreserveAspectRatio;

public class MarkerElement extends KnownElement{
	public MarkerElement() {
		super(ElementType.marker);
	}

	public float getOrient() {
		return this.<Float>getPropertyOrDefault(PropertyType.orient).floatValue();
	}
	
	public MarkerUnits isUserSpaceMarkerUnits() {
		return getPropertyOrDefault(PropertyType.markerUnits);
	}
	
	public Overflow getOverfow() {
		return getPropertyOrDefault(PropertyType.overflow);
	}
	
	public Rectangle getViewBox() {
		return getProperty(PropertyType.viewBox);
	}
	
	public float getRefX() {
		return this.<Length>getPropertyOrDefault(PropertyType.refX).getPixels();
	}
	
	public float getRefY() {
		return this.<Length>getPropertyOrDefault(PropertyType.refY).getPixels();
	}
	
	public float getMarkerWidth() {
		return this.<Length>getPropertyOrDefault(PropertyType.markerWidth).getPixels();
	}
	
	public float getMarkerHeight() {
		return this.<Length>getPropertyOrDefault(PropertyType.markerHeight).getPixels();
	}
	
	public PreserveAspectRatio getPreserveAspectRatio() {
		return getPropertyOrDefault(PropertyType.preserveAspectRatio);
	}
}
