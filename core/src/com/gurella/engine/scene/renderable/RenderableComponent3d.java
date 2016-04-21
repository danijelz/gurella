package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graphics.GenericBatch;

public abstract class RenderableComponent3d extends RenderableComponent {
	public transient ModelInstance instance;
	private final BoundingBox temp = new BoundingBox();

	@Override
	protected void render(GenericBatch batch) {
		if (instance != null) {
			batch.render(instance);
		}
	}

	@Override
	public void getBounds(BoundingBox bounds) {
		if(instance != null) {
			instance.extendBoundingBox(bounds);
		}
	}

	@Override
	public boolean getIntersection(Ray ray, Vector3 intersection) {
		Array<Node> nodes = instance.nodes;
		for (int i = 0; i < nodes.size; i++) {
			Node node = nodes.get(i);
			node.calculateBoundingBox(temp.clr(), true);
			//
		}

		// instance.nodes.get(0).calculateBoundingBox(out, transform)
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void updateDefaultTransform() {
		if (instance != null) {
			instance.transform.idt();
		}
	}

	@Override
	protected void updateTransform() {
		if (instance != null) {
			transformComponent.getWorldTransform(instance.transform);
		}
	}

	@Override
	public void reset() {
		super.reset();
		instance = null;
	}
}
