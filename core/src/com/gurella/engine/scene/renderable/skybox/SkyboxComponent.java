package com.gurella.engine.scene.renderable.skybox;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.graphics.render.GenericBatch;
import com.gurella.engine.scene.renderable.RenderableComponent;

@ModelDescriptor(descriptiveName = "Skybox")
public class SkyboxComponent extends RenderableComponent {

	@Override
	protected void updateGeometry() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doRender(GenericBatch batch) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doGetBounds(BoundingBox bounds) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean doGetIntersection(Ray ray, Vector3 intersection) {
		return false;
	}
}
