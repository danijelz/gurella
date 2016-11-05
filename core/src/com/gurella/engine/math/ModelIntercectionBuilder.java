package com.gurella.engine.math;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

//TODO unused
public class ModelIntercectionBuilder {
	private final ModelIntesector intesector = new ModelIntesector();
	private final Vector3 intersection = new Vector3();
	private boolean inProgress;
	private Object closestInterception;
	
	public ModelIntercectionBuilder intersection(Vector3 cameraPosition, Ray ray) {
		intesector.init(cameraPosition, ray, intersection);
		return this;
	}
}
