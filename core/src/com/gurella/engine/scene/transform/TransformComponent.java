package com.gurella.engine.scene.transform;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.PropertyChangeListener;
import com.gurella.engine.base.model.PropertyDescriptor;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.subscriptions.base.object.ObjectParentChangeListener;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.transform.NodeTransformChangedListener;

//TODO make logic to reparent node and update local transform
@ModelDescriptor(descriptiveName = "Transform")
public class TransformComponent extends SceneNodeComponent2 implements PropertyChangeListener, Poolable {
	private static final TransformChangedEvent event = new TransformChangedEvent();

	private transient int nodeId;

	@PropertyDescriptor(flatSerialization = true)
	private final Vector3 translation = new Vector3();

	@PropertyDescriptor(flatSerialization = true)
	private final Vector3 rotation = new Vector3();
	private final Quaternion rotationQuat = new Quaternion();

	@PropertyDescriptor(flatSerialization = true)
	private final Vector3 scale = new Vector3(1, 1, 1);

	private transient final Matrix4 transform = new Matrix4();
	private transient final Matrix4 worldTransform = new Matrix4();
	private boolean transformDirty = true;

	private final Matrix4 transformInverse = new Matrix4();
	private boolean transformInvDirty = true;
	private boolean worldTransformInvDirty = true;

	private TransformComponent parentTransform;

	private final Vector3 tempVector = new Vector3();
	private final Quaternion rotator = new Quaternion();

	private final NodeParentChangedListener nodeParentChangedListener = new NodeParentChangedListener();
	private final ParentComponentActivityListener parentComponentActivityListener = new ParentComponentActivityListener();
	private final ParentNodeTransformChangedListener parentNodeTransformChangedListener = new ParentNodeTransformChangedListener();

	@Override
	protected void componentActivated() {
		SceneNode2 node = getNode();
		nodeId = node.getInstanceId();
		subscribeTo(node, nodeParentChangedListener);

		SceneNode2 parentNode = node.getParentNode();
		if (parentNode != null) {
			parentTransform = parentNode.getComponent(TransformComponent.class);
			subscribeTo(parentNode, parentComponentActivityListener);
			subscribeTo(parentNode, parentNodeTransformChangedListener);
		}
	}

	@Override
	protected void componentDeactivated() {
		nodeId = -1;
	}

	private void notifyChanged() {
		if (!transformDirty) {
			transformDirty = true;
			transformInvDirty = true;
			worldTransformInvDirty = true;
			EventService.post(nodeId, event);
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
		this.translation.set(translation);
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
		update();
		if (parentTransform == null) {
			return translation;
		} else {
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
		this.scale.set(scale);
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
		update();
		if (parentTransform == null) {
			return scale;
		} else {
			return worldTransform.getScale(tempVector);
		}
	}

	// ///////////////rotation

	public TransformComponent setRotationQuat(Quaternion rotation) {
		return setRotation(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public TransformComponent setRotation(float x, float y, float z, float w) {
		rotationQuat.set(x, y, z, w);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldRotation(Quaternion rotation) {
		return setWorldRotation(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public TransformComponent setWorldRotation(float x, float y, float z, float w) {
		if (parentTransform == null) {
			rotationQuat.set(x, y, z, w);
			rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		} else {
			parentTransform.getWorldRotation(rotator).conjugate();
			rotationQuat.set(x, y, z, w).mul(rotator);
			rotationQuat.nor();
			rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		}

		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotation(Vector3 eulerDegrees) {
		return setEulerRotation(eulerDegrees.x, eulerDegrees.y, eulerDegrees.z);
	}

	public TransformComponent setEulerRotation(float x, float y, float z) {
		rotationQuat.setEulerAngles(y, x, z);
		rotation.set(x, y, z);
		notifyChanged();
		return this;
	}

	public TransformComponent setWorldEulerRotation(Vector3 eulerDegrees) {
		return setWorldEulerRotation(eulerDegrees.x, eulerDegrees.y, eulerDegrees.z);
	}

	public TransformComponent setWorldEulerRotation(float xDegrees, float yDegrees, float zDegrees) {
		rotation.set(xDegrees, yDegrees, zDegrees);
		if (parentTransform != null) {
			rotation.sub(parentTransform.getWorldEulerRotation());
		}
		rotationQuat.setEulerAngles(rotation.y, rotation.x, rotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotationX(float degrees) {
		rotation.x = degrees;
		rotationQuat.setEulerAngles(rotation.y, rotation.x, rotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotationY(float degrees) {
		rotation.y = degrees;
		rotationQuat.setEulerAngles(rotation.y, rotation.x, rotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent setEulerRotationZ(float degrees) {
		rotation.z = degrees;
		rotationQuat.setEulerAngles(rotation.y, rotation.x, rotation.z);
		notifyChanged();
		return this;
	}

	public TransformComponent rotate(Quaternion additionalRotation) {
		this.rotationQuat.mul(additionalRotation);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotate(float x, float y, float z, float w) {
		this.rotationQuat.mul(x, y, z, w);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotateX(float degrees) {
		rotator.set(Vector3.X, degrees);
		rotationQuat.mul(rotator);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotateY(float degrees) {
		rotator.set(Vector3.Y, degrees);
		rotationQuat.mul(rotator);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotateZ(float degrees) {
		rotator.set(Vector3.Z, degrees);
		rotationQuat.mul(rotator);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public TransformComponent rotate(Vector3 axis, float degrees) {
		rotator.set(axis, degrees);
		rotationQuat.mul(rotator);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public void rotateAround(Vector3 point, Vector3 axis, float degrees) {
		update();
		transform.getTranslation(tempVector);
		tempVector.x = point.x - tempVector.x;
		tempVector.y = point.y - tempVector.y;
		tempVector.z = point.z - tempVector.z;
		transform.translate(tempVector);
		transform.rotate(axis, degrees);
		tempVector.rotate(axis, degrees).scl(-1);
		transform.translate(tempVector);
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());

		if (parentTransform == null) {
			worldTransform.set(transform);
		} else {
			worldTransform.set(parentTransform.getWorldTransform()).mul(transform);
		}

		transformDirty = false;
		transformInvDirty = true;
		worldTransformInvDirty = true;
		EventService.post(nodeId, event);
	}

	public void rotateAroundWorld(Vector3 point, Vector3 axis, float degrees) {
		update();
		worldTransform.getTranslation(tempVector);
		tempVector.x = point.x - tempVector.x;
		tempVector.y = point.y - tempVector.y;
		tempVector.z = point.z - tempVector.z;
		worldTransform.translate(tempVector);
		worldTransform.rotate(axis, degrees);
		tempVector.rotate(axis, degrees).scl(-1);
		worldTransform.translate(tempVector);

		transform.set(worldTransform);
		if (parentTransform != null) {
			transform.mul(parentTransform.getWorldTransformInverse());
		}

		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotationQuat, true);
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());

		transformDirty = false;
		transformInvDirty = true;
		worldTransformInvDirty = true;
		EventService.post(nodeId, event);
	}

	public TransformComponent eulerRotate(Vector3 additionalEulerRotation) {
		return eulerRotate(additionalEulerRotation.x, additionalEulerRotation.y, additionalEulerRotation.z);
	}

	public TransformComponent eulerRotate(float x, float y, float z) {
		rotator.setEulerAngles(y, x, z);
		rotationQuat.mul(rotator);
		rotationQuat.nor();
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());
		notifyChanged();
		return this;
	}

	public float getEulerRotationX() {
		return rotation.x;
	}

	public float getEulerRotationY() {
		return rotation.y;
	}

	public float getEulerRotationZ() {
		return rotation.z;
	}

	public Vector3 getEulerRotation(Vector3 outRotation) {
		return outRotation.set(rotation);
	}

	public Quaternion getRotation(Quaternion outRotation) {
		return outRotation.set(rotationQuat);
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
			return rotation;
		} else {
			Quaternion worldRotation = getWorldRotation();
			return tempVector.set(worldRotation.getPitch(), worldRotation.getYaw(), worldRotation.getRoll());
		}
	}

	public Quaternion getWorldRotation(Quaternion outRotation) {
		return outRotation.set(getWorldRotation());
	}

	private Quaternion getWorldRotation() {
		update();
		if (parentTransform == null) {
			return rotationQuat;
		} else {
			return worldTransform.getRotation(rotator, true);
		}
	}

	////////////////// transform

	public void setTransform(Matrix4 newTransform) {
		transform.set(newTransform);
		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotationQuat, true);
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());

		if (parentTransform == null) {
			worldTransform.set(transform);
		} else {
			worldTransform.set(parentTransform.getWorldTransform()).mul(transform);
		}

		transformDirty = false;
		transformInvDirty = true;
		worldTransformInvDirty = true;
		EventService.post(nodeId, event);
	}

	public void setWorldTransform(Matrix4 newWorldTransform) {
		worldTransform.set(newWorldTransform);
		transform.set(worldTransform);
		if (parentTransform != null) {
			transform.mul(parentTransform.getWorldTransformInverse());
		}

		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotationQuat, true);
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());

		transformDirty = false;
		transformInvDirty = true;
		worldTransformInvDirty = true;
		EventService.post(nodeId, event);
	}

	public Matrix4 getTransform(Matrix4 outTransform) {
		return outTransform.set(getTransform());
	}

	private final Matrix4 getTransform() {
		update();
		return transform;
	}

	public Matrix4 getWorldTransform(Matrix4 outTransform) {
		return outTransform.set(getWorldTransform());
	}

	private final Matrix4 getWorldTransform() {
		update();
		return worldTransform;
	}

	private void update() {
		if (transformDirty) {
			transform.set(translation, rotationQuat, scale);
			if (parentTransform == null) {
				worldTransform.set(transform);
			} else {
				worldTransform.set(parentTransform.getWorldTransform()).mul(transform);
			}
			transformDirty = false;
		}
	}

	public Matrix4 getWorldTransformInverse() {
		if (worldTransformInvDirty) {
			transformInverse.set(getWorldTransform());
			Matrix4.inv(transformInverse.val);
			transformInvDirty = true;
			worldTransformInvDirty = false;
		}

		return transformInverse;
	}

	public Matrix4 getTransformInverse() {
		if (transformInvDirty) {
			transformInverse.set(getTransform());
			Matrix4.inv(transformInverse.val);
			transformInvDirty = false;
			worldTransformInvDirty = true;
		}

		return transformInverse;
	}

	/////////// world transfoms

	public Vector3 transformPointToWorld(Vector3 point) {
		return point.mul(getWorldTransform());
	}

	public Vector3 transformPointFromWorld(Vector3 point) {
		return point.mul(getWorldTransformInverse());
	}

	public Vector3 transformDirectionToWorld(Vector3 direction) {
		return direction.mul(getWorldRotation());
	}

	public Vector3 transformDirectionFromWorld(Vector3 direction) {
		getWorldTransformInverse().getRotation(rotator);
		return direction.mul(rotator);
	}

	public Vector3 transformVectorToWorld(Vector3 vector) {
		return vector.mul(getWorldRotation()).add(getWorldTranslation());
	}

	public Vector3 transformVectorFromWorld(Vector3 vector) {
		Matrix4 worldTransformInverse = getWorldTransformInverse();
		worldTransformInverse.getRotation(rotator);
		return vector.mul(rotator).add(worldTransformInverse.getTranslation(tempVector));
	}

	public BoundingBox transformBoundsToWorld(BoundingBox out) {
		return out.mul(getWorldTransform());
	}

	public BoundingBox transformBoundsFromWorld(BoundingBox out) {
		return out.mul(getWorldTransformInverse());
	}

	public Ray transformRayFromWorld(Ray out) {
		transformPointFromWorld(out.origin);
		transformDirectionFromWorld(out.direction);
		out.direction.nor();
		return out;
	}

	public Vector3 getWorldUp(Vector3 out) {
		return transformDirectionToWorld(out.set(0, 1, 0));
	}

	public Vector3 getWorldRight(Vector3 out) {
		return transformDirectionToWorld(out.set(1, 0, 0));
	}

	public Vector3 getWorldForward(Vector3 out) {
		return transformDirectionToWorld(out.set(0, 0, 1));
	}

	/////////// local transfoms

	public Vector3 transformPoint(Vector3 point) {
		return point.mul(getTransform());
	}

	public Vector3 transformPointFromLocal(Vector3 point) {
		return point.mul(getTransformInverse());
	}

	public Vector3 transformDirection(Vector3 direction) {
		return rotationQuat.transform(direction);
	}

	public Vector3 transformDirectionFromLocal(Vector3 direction) {
		getTransformInverse().getRotation(rotator);
		return direction.mul(rotator);
	}

	public Vector3 transformVector(Vector3 vector) {
		return vector.mul(rotationQuat).add(translation);
	}

	public Vector3 transformVectorFromLocal(Vector3 vector) {
		Matrix4 transformInverse = getTransformInverse();
		transformInverse.getRotation(rotator);
		return vector.mul(rotator).add(transformInverse.getTranslation(tempVector));
	}

	public BoundingBox transformBounds(BoundingBox out) {
		return out.mul(getTransform());
	}

	public BoundingBox transformBoundsFromLocal(BoundingBox out) {
		return out.mul(getTransformInverse());
	}

	public Ray transformRayFromLocal(Ray out) {
		transformPointFromLocal(out.origin);
		transformDirectionFromLocal(out.direction);
		out.direction.nor();
		return out;
	}

	public Vector3 getUp(Vector3 out) {
		return transformDirection(out.set(0, 1, 0));
	}

	public Vector3 getRight(Vector3 out) {
		return transformDirection(out.set(1, 0, 0));
	}

	public Vector3 getForward(Vector3 out) {
		return transformDirection(out.set(0, 0, 1));
	}

	////// lookAt

	public void lookAt(Vector3 target) {
		lookAt(target, tempVector.set(0, 1, 0));
	}

	public void lookAt(Vector3 target, Vector3 up) {
		update();
		transform.setToLookAt(translation, target, up).scl(scale);
		transform.getRotation(rotationQuat, true);
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());

		if (parentTransform == null) {
			worldTransform.set(transform);
		} else {
			worldTransform.set(parentTransform.getWorldTransform()).mul(transform);
		}

		transformDirty = false;
		transformInvDirty = true;
		worldTransformInvDirty = true;
		EventService.post(nodeId, event);
	}

	public void lookAtWorld(Vector3 target) {
		lookAtWorld(target, tempVector.set(0, 1, 0));
	}

	public void lookAtWorld(Vector3 target, Vector3 up) {
		update();
		worldTransform.getTranslation(translation);
		worldTransform.getScale(scale);
		worldTransform.setToLookAt(translation, target, up).scl(translation);

		transform.set(worldTransform);

		if (parentTransform != null) {
			transform.mul(parentTransform.getWorldTransformInverse());
		}

		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotationQuat, true);
		rotation.set(rotationQuat.getPitch(), rotationQuat.getYaw(), rotationQuat.getRoll());

		transformDirty = false;
		transformInvDirty = true;
		worldTransformInvDirty = true;
		EventService.post(nodeId, event);
	}

	@Override
	public void propertyChanged(String propertyName, Object oldValue, Object newValue) {
		if ("rotation".equals(propertyName)) {
			rotationQuat.setEulerAngles(rotation.y, rotation.x, rotation.z);
		}
		notifyChanged();
	}

	@Override
	public void reset() {
		nodeId = -1;
		parentTransform = null;
		translation.setZero();
		rotationQuat.idt();
		rotation.setZero();
		scale.set(1, 1, 1);
		transformDirty = true;
		transformInvDirty = true;
		worldTransformInvDirty = true;
	}

	private class NodeParentChangedListener implements ObjectParentChangeListener {
		@Override
		public void parentChanged(ManagedObject oldParent, ManagedObject newParent) {
			boolean notify = false;
			boolean updateWorldTransform = false;

			if (oldParent instanceof SceneNode2) {
				updateWorldTransform = true;
				getWorldTransform(transformInverse);

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
				TransformComponent newParentTransform = parentNode.getComponent(TransformComponent.class);
				if (newParentTransform != null) {
					notify = true;
					parentTransform = newParentTransform;
				}

				subscribeTo(parentNode, parentComponentActivityListener);
				subscribeTo(parentNode, parentNodeTransformChangedListener);
			}

			if (updateWorldTransform) {
				setWorldTransform(transformInverse);
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

	private static class TransformChangedEvent implements Event<NodeTransformChangedListener> {
		@Override
		public Class<NodeTransformChangedListener> getSubscriptionType() {
			return NodeTransformChangedListener.class;
		}

		@Override
		public void dispatch(NodeTransformChangedListener listener) {
			listener.onNodeTransformChanged();
		}
	}
}
