package com.gurella.engine.scene.movement;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.subscriptions.base.object.ObjectParentChangeListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.movement.NodeTransformChangedListener;

@ModelDescriptor(descriptiveName = "Transform")
public class TransformComponent extends SceneNodeComponent2 implements PropertyChangeListener, Poolable {
	private static final Array<NodeTransformChangedListener> listeners = new Array<NodeTransformChangedListener>();
	private static final Object mutex = new Object();

	private transient int nodeId;

	@PropertyDescriptor(flat = true)
	private final Vector3 translation = new Vector3();

	@PropertyDescriptor(descriptiveName = "rotation", flat = true)
	private final Vector3 eulerRotation = new Vector3();
	private final Quaternion rotation = new Quaternion();

	@PropertyDescriptor(flat = true)
	private final Vector3 scale = new Vector3(1, 1, 1);

	private final Matrix4 transform = new Matrix4();
	private final Matrix4 worldTransform = new Matrix4();
	private boolean transformDirty = true;

	private final Matrix4 worldTransformInverse = new Matrix4();
	private boolean transformInvDirty = true;

	private TransformComponent parentTransform;
	private final Array<TransformComponent> childTransforms = new Array<TransformComponent>();

	private final Vector3 tempVector = new Vector3();
	private final Quaternion rotator = new Quaternion();

	private final NodeParentChangedListener nodeParentChangedListener = new NodeParentChangedListener();
	private final ParentComponentActivityListener parentComponentActivityListener = new ParentComponentActivityListener();
	private final ParentNodeTransformChangedListener parentNodeTransformChangedListener = new ParentNodeTransformChangedListener();

	@Override
	protected void onActivate() {
		SceneNode2 node = getNode();
		nodeId = node.getInstanceId();
		subscribeTo(node, nodeParentChangedListener);

		SceneNode2 parentNode = node.getParentNode();
		if (parentNode != null) {
			parentTransform = parentNode.getActiveComponent(TransformComponent.class);
			subscribeTo(parentNode, parentComponentActivityListener);
			subscribeTo(parentNode, parentNodeTransformChangedListener);
		}
	}

	@Override
	protected void onDeactivate() {
		nodeId = -1;
	}

	private void notifyChanged() {
		if (!transformDirty) {
			transformDirty = true;
			transformInvDirty = true;
			notifyChanged(this);
		}
	}

	private static void notifyChanged(TransformComponent component) {
		synchronized (mutex) {
			EventService.getSubscribers(component.nodeId, NodeTransformChangedListener.class, listeners);
			for (int i = 0; i < listeners.size; i++) {
				listeners.get(i).onNodeTransformChanged();
			}
			listeners.clear();
		}
	}

	// //////////translate

	public TransformComponent translate(Vector3 additionalTranslation) {
		translation.add(additionalTranslation);
		notifyChanged();
		return this;
	}

	public TransformComponent translate(float x, float y, float z) {
		translation.add(x, y, z);
		notifyChanged();
		return this;
	}

	public TransformComponent translateX(float x) {
		translation.x += x;
		notifyChanged();
		return this;
	}

	public TransformComponent translateY(float y) {
		translation.y += y;
		notifyChanged();
		return this;
	}

	public TransformComponent translateZ(float z) {
		translation.z += z;
		notifyChanged();
		return this;
	}

	public TransformComponent setTranslation(float x, float y, float z) {
		translation.set(x, y, z);
		notifyChanged();
		return this;
	}

	public TransformComponent setTranslation(Vector3 translation) {
		translation.set(translation);
		notifyChanged();
		return this;
	}

	public TransformComponent setTranslationX(float x) {
		translation.x = x;
		notifyChanged();
		return this;
	}

	public TransformComponent setTranslationY(float y) {
		translation.y = y;
		notifyChanged();
		return this;
	}

	public TransformComponent setTranslationZ(float z) {
		translation.z = z;
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldTranslation(Vector3 translation) {
		return setWorldTranslation(translation.x, translation.y, translation.z);
	}

	public TransformComponent setWorldTranslation(float x, float y, float z) {
		translation.set(x, y, z);
		if (parentTransform == null) {
			translation.sub(parentTransform.getWorldTranslation());
		}
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldTranslationX(float x) {
		if (parentTransform == null) {
			translation.x = x;
		} else {
			translation.x = x - parentTransform.getWorldTranslationX();
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setWorldTranslationY(float y) {
		if (parentTransform == null) {
			translation.y = y;
		} else {
			translation.y = y - parentTransform.getWorldTranslationY();
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setWorldTranslationZ(float z) {
		if (parentTransform == null) {
			translation.z = z;
		} else {
			translation.z = z - parentTransform.getWorldTranslationZ();
		}

		notifyChanged();
		return this;
	}

	public float getTranslationX() {
		return translation.x;
	}

	public float getTranslationY() {
		return translation.y;
	}

	public float getTranslationZ() {
		return translation.z;
	}

	public Vector3 getTranslation(Vector3 outTranslate) {
		return outTranslate.set(translation);
	}

	public float getWorldTranslationX() {
		return getWorldTranslation().x;
	}

	public float getWorldTranslationY() {
		return getWorldTranslation().y;
	}

	public float getWorldTranslationZ() {
		return getWorldTranslation().z;
	}

	public Vector3 getWorldTranslation(Vector3 outTranslate) {
		return outTranslate.set(getWorldTranslation());
	}

	private Vector3 getWorldTranslation() {
		if (parentTransform == null) {
			return translation;
		} else {
			update();
			return worldTransform.getTranslation(tempVector);
		}
	}

	// ////////////scale

	public TransformComponent scale(Vector3 additionalScale) {
		scale.add(additionalScale);
		notifyChanged();
		return this;
	}

	public TransformComponent scale(float x, float y, float z) {
		scale.add(x, y, z);
		notifyChanged();
		return this;
	}

	public TransformComponent scaleX(float x) {
		scale.x += x;
		notifyChanged();
		return this;
	}

	public TransformComponent scaleY(float y) {
		scale.y += y;
		notifyChanged();
		return this;
	}

	public TransformComponent scaleZ(float z) {
		scale.z += z;
		notifyChanged();
		return this;
	}

	public TransformComponent setScale(Vector3 scale) {
		scale.set(scale);
		notifyChanged();
		return this;
	}

	public TransformComponent setScale(float x, float y, float z) {
		scale.set(x, y, z);
		notifyChanged();
		return this;
	}

	public TransformComponent setScaleX(float x) {
		scale.x = x;
		notifyChanged();
		return this;
	}

	public TransformComponent setScaleY(float y) {
		scale.y = y;
		notifyChanged();
		return this;
	}

	public TransformComponent setScaleZ(float z) {
		scale.z = z;
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldScale(Vector3 newScale) {
		return setWorldScale(newScale.x, newScale.y, newScale.z);
	}

	public TransformComponent setWorldScale(float x, float y, float z) {
		if (parentTransform == null) {
			scale.set(x, y, z);
		} else {
			parentTransform.getWorldScale(tempVector);

			if (tempVector.x == 0) {
				scale.x = x;
			} else {
				scale.x /= tempVector.x;
			}

			if (tempVector.y == 0) {
				scale.y = y;
			} else {
				scale.y /= tempVector.y;
			}

			if (tempVector.z == 0) {
				scale.z = z;
			} else {
				scale.z /= tempVector.z;
			}
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setWorldScaleX(float x) {
		float parentScale;
		if (parentTransform == null || (parentScale = parentTransform.getWorldScaleX()) == 0) {
			scale.x = x;
		} else {
			scale.x /= parentScale;
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setWorldScaleY(float y) {
		float parentScale;
		if (parentTransform == null || (parentScale = parentTransform.getWorldScaleY()) == 0) {
			scale.y = y;
		} else {
			scale.y /= parentScale;
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setWorldScaleZ(float z) {
		float parentScale;
		if (parentTransform == null || (parentScale = parentTransform.getWorldScaleZ()) == 0) {
			scale.z = z;
		} else {
			scale.z /= parentScale;
		}

		notifyChanged();
		return this;
	}

	public float getScaleX() {
		return scale.x;
	}

	public float getScaleY() {
		return scale.y;
	}

	public float getScaleZ() {
		return scale.z;
	}

	public Vector3 getScale(Vector3 outScale) {
		return outScale.set(scale);
	}

	public float getWorldScaleX() {
		return getWorldScale().x;
	}

	public float getWorldScaleY() {
		return getWorldScale().y;
	}

	public float getWorldScaleZ() {
		return getWorldScale().z;
	}

	public Vector3 getWorldScale(Vector3 outScale) {
		return outScale.set(getWorldScale());
	}

	private Vector3 getWorldScale() {
		if (parentTransform == null) {
			return scale;
		} else {
			update();
			return worldTransform.getScale(tempVector);
		}
	}

	// ///////////////rotation

	public TransformComponent setRotation(Quaternion rotation) {
		return setRotation(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public TransformComponent setRotation(float x, float y, float z, float w) {
		rotation.set(x, y, z, w);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldRotation(Quaternion rotation) {
		return setWorldRotation(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public TransformComponent setWorldRotation(float x, float y, float z, float w) {
		if (parentTransform == null) {
			rotation.set(x, y, z, w);
			eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		} else {
			parentTransform.getWorldRotation(rotator).conjugate();
			rotation.set(x, y, z, w).mul(rotator);
			rotation.nor();
			eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotation(Vector3 eulerRotation) {
		return setEulerRotation(eulerRotation.x, eulerRotation.y, eulerRotation.z);
	}

	public TransformComponent setEulerRotation(float x, float y, float z) {
		rotation.setEulerAngles(y, x, z);
		eulerRotation.set(x, y, z);
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldEulerRotation(Vector3 eulerRotation) {
		return setWorldEulerRotation(eulerRotation.x, eulerRotation.y, eulerRotation.z);
	}

	public TransformComponent setWorldEulerRotation(float x, float y, float z) {
		eulerRotation.set(x, y, z);
		if (parentTransform != null) {
			eulerRotation.sub(parentTransform.getWorldEulerRotation());
		}
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotationX(float angle) {
		eulerRotation.x = angle;
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotationY(float angle) {
		eulerRotation.y = angle;
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotationZ(float angle) {
		eulerRotation.z = angle;
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent rotate(Quaternion additionalRotation) {
		this.rotation.mul(additionalRotation);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotate(float x, float y, float z, float w) {
		this.rotation.mul(x, y, z, w);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotateX(float angle) {
		rotator.set(Vector3.X, angle);
		rotation.mul(rotator);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotateY(float angle) {
		rotator.set(Vector3.Y, angle);
		rotation.mul(rotator);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotateZ(float angle) {
		rotator.set(Vector3.Z, angle);
		rotation.mul(rotator);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotate(Vector3 axis, float angle) {
		rotator.set(axis, angle);
		rotation.mul(rotator);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public void rotateAround(Vector3 point, Vector3 axis, float angle) {
		// TODO
		// tmpVec.set(point);
		// tmpVec.sub(position);
		// translate(tmpVec);
		// rotate(axis, angle);
		// tmpVec.rotate(axis, angle);
		// translate(-tmpVec.x, -tmpVec.y, -tmpVec.z);
	}

	public TransformComponent eulerRotate(Vector3 additionalEulerRotation) {
		return eulerRotate(additionalEulerRotation.x, additionalEulerRotation.y, additionalEulerRotation.z);
	}

	public TransformComponent eulerRotate(float x, float y, float z) {
		rotator.setEulerAngles(y, x, z);
		rotation.mul(rotator);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		notifyChanged();
		return this;
	}

	public float getEulerRotationX() {
		return eulerRotation.x;
	}

	public float getEulerRotationY() {
		return eulerRotation.y;
	}

	public float getEulerRotationZ() {
		return eulerRotation.z;
	}

	public Vector3 getEulerRotation(Vector3 outRotation) {
		return outRotation.set(eulerRotation);
	}

	public Quaternion getRotation(Quaternion outRotation) {
		return outRotation.set(rotation);
	}

	public float getWorldEulerRotationX() {
		return getWorldEulerRotation().x;
	}

	public float getWorldEulerRotationY() {
		return getWorldEulerRotation().y;
	}

	public float getWorldEulerRotationZ() {
		return getWorldEulerRotation().z;
	}

	public Vector3 getWorldEulerRotation(Vector3 outRotation) {
		return outRotation.set(getWorldEulerRotation());
	}

	private Vector3 getWorldEulerRotation() {
		if (parentTransform == null) {
			return eulerRotation;
		} else {
			Quaternion worldRotation = getWorldRotation();
			return tempVector.set(worldRotation.getPitch(), worldRotation.getYaw(), worldRotation.getRoll());
		}
	}

	public Quaternion getWorldRotation(Quaternion outRotation) {
		return outRotation.set(getWorldRotation());
	}

	private Quaternion getWorldRotation() {
		if (parentTransform == null) {
			return rotation;
		} else {
			update();
			return worldTransform.getRotation(rotator, true);
		}
	}

	////////////////// transform

	public void setTransform(Matrix4 newTransform) {
		transform.set(newTransform);
		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotation, true);
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());

		if (parentTransform == null) {
			worldTransform.set(transform);
		} else {
			worldTransform.set(parentTransform.getWorldTransform()).mul(transform);
		}

		transformDirty = false;
		transformInvDirty = true;
		notifyChanged(this);
	}

	public void setWorldTransform(Matrix4 newWorldTransform) {
		worldTransform.set(newWorldTransform);
		transform.set(worldTransform);

		if (parentTransform != null) {
			transform.mul(parentTransform.getWorldTransformInverse());
		}

		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotation, true);
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());

		transformDirty = false;
		transformInvDirty = true;
		notifyChanged(this);
	}

	public Matrix4 getTransform(Matrix4 outTransform) {
		return outTransform.set(getTransform());
	}

	private Matrix4 getTransform() {
		update();
		return transform;
	}

	public Matrix4 getWorldTransform(Matrix4 outTransform) {
		return outTransform.set(getWorldTransform());
	}

	private Matrix4 getWorldTransform() {
		update();
		return worldTransform;
	}

	private void update() {
		if (transformDirty) {
			transform.set(translation, rotation, scale);
			if (parentTransform == null) {
				worldTransform.set(getTransform());
			} else {
				worldTransform.set(parentTransform.getWorldTransform()).mul(getTransform());
			}
			transformDirty = false;
		}
	}

	public Matrix4 getWorldTransformInverse() {
		if (transformInvDirty) {
			worldTransformInverse.set(getWorldTransform()).inv();
			transformInvDirty = false;
		}

		return worldTransformInverse;
	}

	public Vector3 localToWorld(Vector3 point) {
		return point.mul(getWorldTransform());
	}

	public Vector3 localToWorld(Vector3 point, Vector3 out) {
		return out.set(point).mul(getWorldTransform());
	}

	public Vector3 worldToLocal(Vector3 point) {
		return point.mul(getWorldTransformInverse());
	}

	public Vector3 worldToLocal(Vector3 point, Vector3 out) {
		return out.set(point).mul(getWorldTransformInverse());
	}

	@Override
	public void propertyChanged(PropertyChangeEvent event) {
		Array<Object> propertyPath = event.propertyPath;
		if (propertyPath.size == 2 && propertyPath.indexOf(this, true) == 0) {
			notifyChanged();
		}
	}

	public Vector3 getWorldUp(Vector3 out) {
		return out.set(0, 1, 0).mul(getWorldRotation()).nor();
	}

	public Vector3 getWorldRight(Vector3 out) {
		return out.set(1, 0, 0).mul(getWorldRotation()).nor();
	}

	public Vector3 getWorldForward(Vector3 out) {
		return out.set(0, 0, 1).mul(getWorldRotation()).nor();
	}

	public Vector3 transformWorldDirection(Vector3 direction) {
		return direction.mul(getWorldRotation());
	}

	public Vector3 getUp(Vector3 out) {
		return out.set(0, 1, 0).mul(rotation).nor();
	}

	public Vector3 getRight(Vector3 out) {
		return out.set(1, 0, 0).mul(rotation).nor();
	}

	public Vector3 getForward(Vector3 out) {
		return out.set(0, 0, 1).mul(rotation).nor();
	}

	public Vector3 transformDirection(Vector3 direction) {
		return direction.mul(rotation);
	}

	public void lookAt(Vector3 target) {
		lookAt(target, getUp(tempVector));
	}

	public void lookAt(Vector3 target, Vector3 up) {
		// TODO
		// tmpVec.set(x, y, z).sub(position).nor();
		// if (!tmpVec.isZero()) {
		// float dot = tmpVec.dot(up); // up and direction must ALWAYS be orthonormal vectors
		// if (Math.abs(dot - 1) < 0.000000001f) {
		// // Collinear
		// up.set(direction).scl(-1);
		// } else if (Math.abs(dot + 1) < 0.000000001f) {
		// // Collinear opposite
		// up.set(direction);
		// }
		// direction.set(tmpVec);
		// normalizeUp();
		// }
	}

	@Override
	public void reset() {
		nodeId = -1;
		parentTransform = null;
		childTransforms.clear();
		translation.setZero();
		rotation.idt();
		eulerRotation.setZero();
		scale.set(1, 1, 1);
		transformDirty = true;
		transformInvDirty = true;
	}

	private class NodeParentChangedListener implements ObjectParentChangeListener {
		@Override
		public void parentChanged(ManagedObject oldParent, ManagedObject newParent) {
			boolean notify = false;
			if (oldParent instanceof SceneNode2) {
				SceneNode2 parentNode = (SceneNode2) oldParent;
				unsubscribeFrom(parentNode, parentComponentActivityListener);
				unsubscribeFrom(parentNode, parentNodeTransformChangedListener);
				if (parentTransform != null) {
					notify = true;
					parentTransform = null;
				}
			}

			if (newParent instanceof SceneNode2) {
				SceneNode2 parentNode = (SceneNode2) newParent;
				TransformComponent newParentTransform = parentNode.getActiveComponent(TransformComponent.class);
				if (newParentTransform != null) {
					notify = true;
					parentTransform = newParentTransform;
				}

				subscribeTo(parentNode, parentComponentActivityListener);
				subscribeTo(parentNode, parentNodeTransformChangedListener);
			}

			if (notify) {
				notifyChanged();
			}
		}
	}

	private class ParentComponentActivityListener implements NodeComponentActivityListener {
		@Override
		public void nodeComponentActivated(SceneNodeComponent2 component) {
			if (component instanceof TransformComponent) {
				parentTransform = (TransformComponent) component;
				notifyChanged();
			}
		}

		@Override
		public void nodeComponentDeactivated(SceneNodeComponent2 component) {
			if (parentTransform == component) {
				parentTransform = null;
				notifyChanged();
			}
		}
	}

	private class ParentNodeTransformChangedListener implements NodeTransformChangedListener {
		@Override
		public void onNodeTransformChanged() {
			notifyChanged();
		}
	}
}
