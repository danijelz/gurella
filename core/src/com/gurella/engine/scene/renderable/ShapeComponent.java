package com.gurella.engine.scene.renderable;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gurella.engine.math.geometry.shape.Shape;
import com.gurella.engine.math.geometry.shape.Sphere;

public class ShapeComponent extends RenderableComponent3d {
	private Shape shape;

	protected transient Model model;
	protected transient Material material;
	protected transient boolean modelDirty = true;

	public ShapeComponent() {
		Sphere sphere = new Sphere();
		sphere.setRadius(1.2f);
		shape = sphere;
		material = new Material(ColorAttribute.createDiffuse(0.5f, 1, 1, 0.4f), new BlendingAttribute(1));
		ModelBuilder builder = new ModelBuilder();
		float radius = sphere.getRadius();
		model = builder.createSphere(radius, radius, radius, 30, 30, material, Usage.Position | Usage.Normal);
		instance = new ModelInstance(model);
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		if (this.shape != shape) {
			this.shape = shape;
			if (model != null) {
				model.dispose();
			}
			
			if (shape == null) {
				instance = null;
			} else {
				model = null;//TODO shape.createModel();
				instance = new ModelInstance(model);
				if (transformComponent != null) {
					transformComponent.getWorldTransform(instance.transform);
				}
			}
		}
	}

	@Override
	protected void updateDefaultTransform() {
		super.updateDefaultTransform();
		if (shape != null && instance != null) {
			shape.setLocalTransform(instance.transform);
		}
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		if (shape != null && instance != null) {
			shape.setLocalTransform(instance.transform);
		}
	}

	@Override
	public void reset() {
		super.reset();
		shape = null;
	}
}
