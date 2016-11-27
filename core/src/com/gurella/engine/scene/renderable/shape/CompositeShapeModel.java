package com.gurella.engine.scene.renderable.shape;

import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.metatype.PropertyDescriptor;

public class CompositeShapeModel extends ShapeModel {
	private final Quaternion rotation = new Quaternion();
	private final Matrix4 transform = new Matrix4();
	private final Matrix4 worldTransform = new Matrix4();

	@PropertyEditorDescriptor(genericTypes = { ShapeModelItem.class })
	Array<ShapeModelItem> items = new Array<ShapeModelItem>();

	public void addShape(ShapeModel shape) {
		items.add(new ShapeModelItem(shape));
		dirty = true;
	}

	public void addShape(ShapeModel shape, float x, float y, float z) {
		ShapeModelItem item = new ShapeModelItem(shape);
		item.translation.set(x, y, z);
		items.add(item);
		dirty = true;
	}

	public void removeShape(ShapeModel shape) {
		for (int i = 0, n = items.size; i < n; i++) {
			ShapeModelItem item = items.get(i);
			if (item.shape == shape) {
				items.removeIndex(i);
				dirty = true;
				return;
			}
		}
	}

	@Override
	protected void buildParts(ModelBuilder builder, Matrix4 parentTransform) {
		for (int i = 0, n = items.size; i < n; i++) {
			ShapeModelItem item = items.get(i);
			if (item != null && item.shape != null) {
				updateItemTransform(item, parentTransform);
				item.shape.buildParts(builder, worldTransform);
			}
		}
	}

	private void updateItemTransform(ShapeModelItem item, Matrix4 parentTransform) {
		rotation.setEulerAngles(item.rotation.y, item.rotation.x, item.rotation.z);
		transform.set(item.translation, rotation, item.scale);
		worldTransform.set(parentTransform).mul(transform);
	}

	public static class ShapeModelItem {
		@PropertyEditorDescriptor(group = "Transform")
		@PropertyDescriptor(flatSerialization = true)
		private final Vector3 translation = new Vector3();

		@PropertyEditorDescriptor(group = "Transform")
		@PropertyDescriptor(flatSerialization = true)
		private final Vector3 rotation = new Vector3();

		@PropertyEditorDescriptor(group = "Transform")
		@PropertyDescriptor(flatSerialization = true)
		private final Vector3 scale = new Vector3(1, 1, 1);

		ShapeModel shape;

		ShapeModelItem() {
		}

		ShapeModelItem(ShapeModel shape) {
			this.shape = shape;
		}

		public ShapeModel getShape() {
			return shape;
		}
	}
}
