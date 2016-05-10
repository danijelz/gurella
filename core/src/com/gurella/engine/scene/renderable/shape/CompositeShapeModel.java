package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.base.model.PropertyDescriptor;

//TODO unused
public class CompositeShapeModel extends ShapeModel {
	private Array<ShapeModelItem> items = new Array<ShapeModelItem>();

	@Override
	protected Model createModel(ModelBuilder builder) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class ShapeModelItem implements PropertyChangeListener {
		@PropertyDescriptor(flat = true)
		private final Vector3 translation = new Vector3();

		@PropertyDescriptor(descriptiveName = "rotation", flat = true)
		private final Vector3 eulerRotation = new Vector3();
		private final Quaternion rotation = new Quaternion();

		@PropertyDescriptor(flat = true)
		private final Vector3 scale = new Vector3(1, 1, 1);

		private final Matrix4 transform = new Matrix4();
		private final Matrix4 worldTransform = new Matrix4();

		private Matrix4 parentTransform;

		public ShapeModel shape;

		@Override
		public void propertyChanged(PropertyChangeEvent event) {
			Array<Object> propertyPath = event.propertyPath;
			if (propertyPath.size == 2 && propertyPath.indexOf(this, true) == 0) {
				if (propertyPath.peek() == eulerRotation) {
					rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
				}
				update();
			}
		}

		private void update() {
			transform.set(translation, rotation, scale);
			if (parentTransform == null) {
				worldTransform.set(transform);
			} else {
				worldTransform.set(parentTransform).mul(transform);
			}
		}
	}
}
