package com.gurella.engine.graph.movement;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.PropertyValue;
import com.gurella.engine.resource.model.ResourceProperty;
import com.gurella.engine.signal.Listener1;
import com.gurella.engine.signal.Signal1.Signal1Impl;

public class TransformComponent extends SceneNodeComponent {
	private final Quaternion rotator = new Quaternion(0, 0, 0, 0);

	@ResourceProperty
	private final Vector3 translation = new Vector3();
	private final Vector3 worldTranslation = new Vector3();
	private boolean worldTranslationDirty = true;

	private final Quaternion rotation = new Quaternion();
	private final Quaternion worldRotation = new Quaternion();
	private boolean worldRotationDirty = true;

	@ResourceProperty(descriptiveName = "rotation")
	private final Vector3 eulerRotation = new Vector3();
	private final Vector3 worldEulerRotation = new Vector3();
	private boolean worldEulerRotationDirty = true;

	@ResourceProperty
	@DefaultValue(compositeValues = { @PropertyValue(name = "x", floatValue = 1),
			@PropertyValue(name = "y", floatValue = 1), @PropertyValue(name = "z", floatValue = 1) })
	private final Vector3 scale = new Vector3(1, 1, 1);
	private final Vector3 worldScale = new Vector3(1, 1, 1);
	private boolean worldScaleDirty = true;

	private final Matrix4 transform = new Matrix4();
	private boolean transformDirty = true;

	private final Matrix4 worldTransform = new Matrix4();
	private boolean worldlTransformDirty = true;

	private final Matrix4 worldTransformInverse = new Matrix4();
	private boolean worldlTransformInvDirty = true;

	private TransformComponent parentTransform;
	private final Array<TransformComponent> childTransforms = new Array<TransformComponent>();

	private final ParentChangedListener parentChangedListener = new ParentChangedListener();
	private final ParentComponentActivatedListener parentComponentActivatedListener = new ParentComponentActivatedListener();
	private final ParentComponentDeactivatedListener parentComponentDeactivatedListener = new ParentComponentDeactivatedListener();
	private final ChildAddedListener childAddedListener = new ChildAddedListener();
	private final ChildRemovedListener childRemovedListener = new ChildRemovedListener();
	private final ChildComponentActivatedListener childComponentActivatedListener = new ChildComponentActivatedListener();
	private final ChildComponentDeactivatedListener childComponentDeactivatedListener = new ChildComponentDeactivatedListener();

	public final Signal1Impl<TransformComponent> dirtySignal = new Signal1Impl<TransformComponent>() {
		@Override
		protected void dispatch(Listener1<TransformComponent> listener, TransformComponent event) {
			super.dispatch(listener, event);
			for (int i = 0; i < childTransforms.size; i++) {
				TransformComponent childTransform = childTransforms.get(i);
				childTransform.dirtySignal.dispatch(childTransform);
			}
		}
	};

	// //////////translate

	public TransformComponent translate(Vector3 additionalTranslation) {
		this.translation.add(additionalTranslation);
		dirtySignal.dispatch(this);

		if (!worldTranslationDirty) {
			this.worldTranslation.add(additionalTranslation);
			markChildrenWorldTranslationDirty();
		}

		return this;
	}

	public TransformComponent translate(float x, float y, float z) {
		this.translation.add(x, y, z);

		if (!worldTranslationDirty) {
			this.worldTranslation.add(x, y, z);
			markChildrenWorldTranslationDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent translateX(float x) {
		this.translation.x += x;

		if (!worldTranslationDirty) {
			this.worldTranslation.x += x;
			markChildrenWorldTranslationDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent translateY(float y) {
		this.translation.y += y;

		if (!worldTranslationDirty) {
			this.worldTranslation.y += y;
			markChildrenWorldTranslationDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent translateZ(float z) {
		this.translation.z += z;

		if (!worldTranslationDirty) {
			this.worldTranslation.z += z;
			markChildrenWorldTranslationDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setTranslation(float x, float y, float z) {
		this.translation.set(x, y, z);
		markWorldTranslationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setTranslation(Vector3 translation) {
		this.translation.set(translation);
		markWorldTranslationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setTranslationX(float x) {
		this.translation.x = x;
		markWorldTranslationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setTranslationY(float y) {
		this.translation.y = y;
		markWorldTranslationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setTranslationZ(float z) {
		this.translation.z = z;
		markWorldTranslationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setWorldTranslation(Vector3 translation) {
		return setWorldTranslation(translation.x, translation.y, translation.z);
	}

	public TransformComponent setWorldTranslation(float x, float y, float z) {
		this.translation.set(x, y, z);
		this.worldTranslation.set(x, y, z);

		if (parentTransform != null) {
			this.translation.sub(parentTransform.getWorldTranslation());
		}

		worldTranslationDirty = false;
		markWorldTranslationHierarchyDirty();
		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setWorldTranslationX(float x) {
		this.translation.x = x;

		if (parentTransform != null) {
			this.translation.x -= parentTransform.getWorldTranslationX();
		}

		if (!worldTranslationDirty) {
			this.worldTranslation.x = x;
			markWorldTranslationHierarchyDirty();
		}

		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setWorldTranslationY(float y) {
		this.translation.y = y;

		if (parentTransform != null) {
			this.translation.y -= parentTransform.getWorldTranslationY();
		}

		if (!worldTranslationDirty) {
			this.worldTranslation.y = y;
			markWorldTranslationHierarchyDirty();
		}

		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setWorldTranslationZ(float z) {
		this.translation.z = z;

		if (parentTransform != null) {
			this.translation.z -= parentTransform.getWorldTranslationZ();
		}

		if (!worldTranslationDirty) {
			this.worldTranslation.z = z;
			markWorldTranslationHierarchyDirty();
		}

		dirtySignal.dispatch(this);

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
		if (worldTranslationDirty) {
			worldTranslation.set(translation);

			if (parentTransform != null) {
				worldTranslation.add(parentTransform.getWorldTranslation());
			}

			worldTranslationDirty = false;
		}

		return worldTranslation;
	}

	private void markWorldTranslationDirty() {
		transformDirty = true;

		if (!worldTranslationDirty) {
			worldTranslationDirty = true;
			worldlTransformDirty = true;
			worldlTransformInvDirty = true;
			markChildrenWorldTranslationDirty();
		}
	}

	private void markChildrenWorldTranslationDirty() {
		for (SceneNode child : getNode().children) {
			TransformComponent childTransformComponent = child.getComponent(TransformComponent.class);
			childTransformComponent.markWorldTranslationDirty();
		}
	}

	private void markWorldTranslationHierarchyDirty() {
		transformDirty = true;
		worldlTransformDirty = true;
		worldlTransformInvDirty = true;
		markChildrenWorldTranslationDirty();
	}

	// ////////////scale

	public TransformComponent scale(Vector3 additionalScale) {
		this.scale.add(additionalScale);
		markWorldScaleDirty();

		if (!worldScaleDirty) {
			this.worldScale.add(additionalScale);
			markChildrenWorldScaleDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent scale(float x, float y, float z) {
		this.scale.add(x, y, z);

		if (!worldScaleDirty) {
			this.worldScale.add(x, y, z);
			markChildrenWorldScaleDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent scaleX(float x) {
		this.scale.x += x;

		if (!worldScaleDirty) {
			this.worldScale.x += x;
			markChildrenWorldScaleDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent scaleY(float y) {
		this.scale.y += y;

		if (!worldScaleDirty) {
			this.worldScale.y += y;
			markChildrenWorldScaleDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent scaleZ(float z) {
		this.scale.z += z;

		if (!worldScaleDirty) {
			this.worldScale.z += z;
			markChildrenWorldScaleDirty();
		}

		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setScale(Vector3 scale) {
		this.scale.set(scale);
		markWorldScaleDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setScale(float x, float y, float z) {
		this.scale.set(x, y, z);
		markWorldScaleDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setScaleX(float x) {
		this.scale.x = x;
		markWorldScaleDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setScaleY(float y) {
		this.scale.y = y;
		markWorldScaleDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setScaleZ(float z) {
		this.scale.z = z;
		markWorldScaleDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setWorldScale(Vector3 scale) {
		return setWorldScale(scale.x, scale.y, scale.z);
	}

	public TransformComponent setWorldScale(float x, float y, float z) {
		this.scale.set(x, y, z);
		this.worldScale.set(x, y, z);

		if (parentTransform != null) {
			this.scale.sub(parentTransform.getWorldScale());
		}

		worldScaleDirty = false;
		markWorldScaleHierarchyDirty();
		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setWorldScaleX(float x) {
		this.scale.x = x;

		if (parentTransform != null) {
			this.scale.x -= parentTransform.getWorldScaleX();
		}

		if (!worldScaleDirty) {
			this.worldScale.x = x;
			markWorldScaleHierarchyDirty();
		}

		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setWorldScaleY(float y) {
		this.scale.y = y;

		if (parentTransform != null) {
			this.scale.y -= parentTransform.getWorldScaleY();
		}

		if (!worldScaleDirty) {
			this.worldScale.y = y;
			markWorldScaleHierarchyDirty();
		}

		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setWorldScaleZ(float z) {
		this.scale.z = z;

		if (parentTransform != null) {
			this.scale.z -= parentTransform.getWorldScaleZ();
		}

		if (!worldScaleDirty) {
			this.worldScale.z = z;
			markWorldScaleHierarchyDirty();
		}

		dirtySignal.dispatch(this);

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
		if (worldScaleDirty) {
			worldScale.set(scale);

			if (parentTransform != null) {
				worldScale.scl(parentTransform.getWorldScale());
			}

			worldScaleDirty = false;
		}

		return worldScale;
	}

	private void markWorldScaleDirty() {
		transformDirty = true;

		if (!worldScaleDirty) {
			worldScaleDirty = true;
			worldlTransformDirty = true;
			worldlTransformInvDirty = true;
			markChildrenWorldScaleDirty();
		}
	}

	private void markChildrenWorldScaleDirty() {
		for (SceneNode child : getNode().children) {
			TransformComponent childTransformComponent = child.getComponent(TransformComponent.class);
			childTransformComponent.markWorldScaleDirty();
		}
	}

	private void markWorldScaleHierarchyDirty() {
		transformDirty = true;
		worldlTransformDirty = true;
		worldlTransformInvDirty = true;
		markChildrenWorldScaleDirty();
	}

	// ///////////////rotation

	public TransformComponent setRotation(Quaternion rotation) {
		return setRotation(rotation.x, rotation.y, rotation.z, rotation.w);
	}

	public TransformComponent setRotation(float x, float y, float z, float w) {
		rotation.set(x, y, z, w);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setWorldRotation(float x, float y, float z, float w) {
		worldRotation.set(x, y, z, w);
		worldRotation.nor();
		worldEulerRotation.set(worldRotation.getPitch(), worldRotation.getYaw(), worldRotation.getRoll());

		if (parentTransform != null) {
			parentTransform.getWorldRotation(rotator).conjugate();
			rotation.set(worldRotation).mul(rotator);
			rotation.nor();
			eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		} else {
			rotation.set(worldRotation);
			eulerRotation.set(worldEulerRotation);
		}

		worldRotationDirty = false;
		markWorldRotationHierarchyDirty();
		dirtySignal.dispatch(this);

		return this;
	}

	// TODO wrong
	public TransformComponent setRotationX(float angle) {
		rotation.set(Vector3.X, angle);
		eulerRotation.x = angle;
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setRotationY(float angle) {
		rotation.set(Vector3.Y, angle);
		eulerRotation.y = angle;
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setRotationZ(float angle) {
		rotation.set(Vector3.Z, angle);
		eulerRotation.z = angle;
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setEulerRotation(Vector3 eulerRotation) {
		return setEulerRotation(eulerRotation.x, eulerRotation.y, eulerRotation.z);
	}

	public TransformComponent setEulerRotation(float x, float y, float z) {
		rotation.setEulerAngles(y, x, z);
		eulerRotation.set(x, y, z);
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setWorldEulerRotation(Vector3 eulerRotation) {
		return setWorldEulerRotation(eulerRotation.x, eulerRotation.y, eulerRotation.z);
	}

	public TransformComponent setWorldEulerRotation(float x, float y, float z) {
		worldRotation.setEulerAngles(y, x, z);
		worldEulerRotation.set(x, y, z);

		eulerRotation.set(x, y, z);

		if (parentTransform != null) {
			eulerRotation.sub(parentTransform.getWorldEulerRotation());
		}

		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);

		worldRotationDirty = false;
		markWorldRotationHierarchyDirty();
		dirtySignal.dispatch(this);

		return this;
	}

	public TransformComponent setEulerRotationX(float angle) {
		eulerRotation.x = angle;
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setEulerRotationY(float angle) {
		eulerRotation.y = angle;
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent setEulerRotationZ(float angle) {
		eulerRotation.z = angle;
		rotation.setEulerAngles(eulerRotation.y, eulerRotation.x, eulerRotation.z);
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent rotate(Quaternion additionalRotation) {
		this.rotation.mul(additionalRotation);
		rotation.nor();
		eulerRotation.add(additionalRotation.getPitch(), additionalRotation.getYaw(), additionalRotation.getRoll());
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent rotate(float x, float y, float z, float w) {
		this.rotation.mul(x, y, z, w);
		rotation.nor();
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent rotateX(float angle) {
		rotator.set(Vector3.X, angle);
		rotation.mul(rotator);
		eulerRotation.x += angle;
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent rotateY(float angle) {
		rotator.set(Vector3.Y, angle);
		rotation.mul(rotator);
		eulerRotation.y += angle;
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent rotateZ(float angle) {
		rotator.set(Vector3.Z, angle);
		rotation.mul(rotator);
		eulerRotation.z += angle;
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
		return this;
	}

	public TransformComponent eulerRotate(Vector3 additionalEulerRotation) {
		return eulerRotate(additionalEulerRotation.x, additionalEulerRotation.y, additionalEulerRotation.z);
	}

	public TransformComponent eulerRotate(float x, float y, float z) {
		rotator.setEulerAngles(y, x, z);
		rotation.mul(rotator);
		eulerRotation.add(x, y, z);
		markWorldRotationDirty();
		dirtySignal.dispatch(this);
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
		if (worldEulerRotationDirty) {
			worldEulerRotation.set(eulerRotation);

			if (parentTransform != null) {
				worldEulerRotation.add(parentTransform.getWorldEulerRotation());
			}

			worldEulerRotationDirty = false;
		}

		return worldEulerRotation;
	}

	public Quaternion getWorldRotation(Quaternion outRotation) {
		return outRotation.set(getWorldRotation());
	}

	private Quaternion getWorldRotation() {
		if (worldRotationDirty) {

			if (parentTransform == null) {
				worldRotation.set(rotation);
			} else {
				worldRotation.set(parentTransform.getWorldRotation()).mul(rotation);
			}

			worldRotationDirty = false;
		}

		return worldRotation;
	}

	private void markWorldRotationDirty() {
		transformDirty = true;

		if (!worldRotationDirty) {
			worldRotationDirty = true;
			worldEulerRotationDirty = true;
			worldlTransformDirty = true;
			worldlTransformInvDirty = true;
			markChildrenWorldRotationDirty();
		}
	}

	private void markChildrenWorldRotationDirty() {
		for (SceneNode child : getNode().children) {
			TransformComponent childTransformComponent = child.getComponent(TransformComponent.class);
			childTransformComponent.markWorldRotationDirty();
		}
	}

	private void markWorldRotationHierarchyDirty() {
		transformDirty = true;
		worldlTransformDirty = true;
		worldlTransformInvDirty = true;
		markChildrenWorldRotationDirty();
	}

	// ////////////////transform

	public void setTransform(Matrix4 newTransform) {
		transform.set(newTransform);
		transform.getTranslation(translation);
		transform.getScale(scale);
		transform.getRotation(rotation, true);
		eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		markWorldTransformDirty();
		dirtySignal.dispatch(this);
	}

	public void setWorldTransform(Matrix4 newWorldTransform) {
		worldTransform.set(newWorldTransform);
		worldTransform.getTranslation(worldTranslation);
		worldTransform.getScale(worldScale);
		worldTransform.getRotation(worldRotation, true);
		worldEulerRotation.set(worldRotation.getPitch(), worldRotation.getYaw(), worldRotation.getRoll());

		if (parentTransform == null) {
			transform.set(worldTransform);
			translation.set(worldTranslation);
			scale.set(worldScale);
			rotation.set(worldRotation);
			eulerRotation.set(worldEulerRotation);
		} else {
			transform.set(worldTransform).mul(parentTransform.getWorldTransformInverse());
			transform.getTranslation(translation);
			transform.getScale(scale);
			transform.getRotation(rotation, true);
			eulerRotation.set(rotation.getPitch(), rotation.getYaw(), rotation.getRoll());
		}

		worldlTransformDirty = false;
		worldlTransformInvDirty = false;
		worldTranslationDirty = false;
		worldScaleDirty = false;
		worldRotationDirty = false;
		worldEulerRotationDirty = false;

		markChildrenWorldTransformDirty();
		dirtySignal.dispatch(this);
	}

	private void markTransformDirty() {
		transformDirty = true;
		markWorldTransformDirty();
	}

	private void markWorldTransformDirty() {
		if (!worldlTransformDirty || !worldTranslationDirty || !worldScaleDirty || !worldRotationDirty) {
			worldlTransformDirty = true;
			worldlTransformInvDirty = true;
			worldTranslationDirty = true;
			worldScaleDirty = true;
			worldRotationDirty = true;
			worldEulerRotationDirty = true;
			markChildrenWorldTransformDirty();
		}
	}

	private void markChildrenWorldTransformDirty() {
		for (SceneNode child : getNode().children) {
			TransformComponent childTransformComponent = child.getComponent(TransformComponent.class);
			childTransformComponent.markWorldTransformDirty();
		}
	}

	private Matrix4 getTransform() {
		if (transformDirty) {
			transform.set(translation, rotation, scale);
			transformDirty = false;
		}

		return transform;
	}

	public Matrix4 getTransform(Matrix4 outTransform) {
		return outTransform.set(getTransform());
	}

	private Matrix4 getWorldTransform() {
		if (worldlTransformDirty) {
			if (parentTransform == null) {
				worldTransform.set(getTransform());
			} else {
				worldTransform.set(parentTransform.getWorldTransform()).mul(getTransform());
			}

			worldlTransformDirty = false;
		}

		return worldTransform;
	}

	public Matrix4 getWorldTransform(Matrix4 outTransform) {
		return outTransform.set(getWorldTransform());
	}

	public Matrix4 getWorldTransformInverse() {
		if (worldlTransformInvDirty) {
			worldTransformInverse.set(getWorldTransform()).inv();
			worldlTransformInvDirty = false;
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
	protected void resetted() {
		super.resetted();
		parentTransform = null;
		childTransforms.clear();
		dirtySignal.clear();
		translation.setZero();
		rotation.idt();
		eulerRotation.setZero();
		scale.set(1, 1, 1);
		markTransformDirty();
	}

	@Override
	protected void activated() {
		super.activated();
		SceneNode node = getNode();
		node.parentChangedSignal.addListener(parentChangedListener);

		SceneNode parent = node.getParent();
		if (parent != null) {
			parentTransform = parent.getComponent(TransformComponent.class);
			parent.componentActivatedSignal.addListener(parentComponentActivatedListener);
			parent.componentDeactivatedSignal.addListener(parentComponentDeactivatedListener);
		}

		node.childAddedSignal.addListener(childAddedListener);
		node.childRemovedSignal.addListener(childRemovedListener);
	}

	@Override
	protected void deactivated() {
		super.deactivated();
		SceneNode node = getNode();
		node.parentChangedSignal.removeListener(parentChangedListener);

		SceneNode parent = node.getParent();
		if (parent != null) {
			parentTransform = null;
			parent.componentActivatedSignal.removeListener(parentComponentActivatedListener);
			parent.componentDeactivatedSignal.removeListener(parentComponentDeactivatedListener);
		}

		node.childAddedSignal.removeListener(childAddedListener);
		node.childRemovedSignal.removeListener(childRemovedListener);
	}

	private class ParentChangedListener implements Listener1<SceneNode> {
		@Override
		public void handle(SceneNode newParent) {
			if (newParent != null) {
				parentTransform = newParent.getActiveComponent(TransformComponent.class);
			}
		}
	}

	private class ParentComponentActivatedListener implements Listener1<SceneNodeComponent> {
		@Override
		public void handle(SceneNodeComponent component) {
			if (component instanceof TransformComponent) {
				parentTransform = (TransformComponent) component;
			}
		}
	}

	private class ParentComponentDeactivatedListener implements Listener1<SceneNodeComponent> {
		@Override
		public void handle(SceneNodeComponent component) {
			if (parentTransform == component) {
				parentTransform = null;
			}
		}
	}

	private class ChildAddedListener implements Listener1<SceneNode> {
		@Override
		public void handle(SceneNode child) {
			child.componentActivatedSignal.addListener(childComponentActivatedListener);
			child.componentDeactivatedSignal.addListener(childComponentDeactivatedListener);
		}
	}

	private class ChildRemovedListener implements Listener1<SceneNode> {
		@Override
		public void handle(SceneNode child) {
			child.componentActivatedSignal.removeListener(childComponentActivatedListener);
			child.componentDeactivatedSignal.removeListener(childComponentDeactivatedListener);
		}
	}

	private class ChildComponentActivatedListener implements Listener1<SceneNodeComponent> {
		@Override
		public void handle(SceneNodeComponent component) {
			if (component instanceof TransformComponent) {
				childTransforms.add((TransformComponent) component);
			}
		}
	}

	private class ChildComponentDeactivatedListener implements Listener1<SceneNodeComponent> {
		@Override
		public void handle(SceneNodeComponent component) {
			if (component instanceof TransformComponent) {
				childTransforms.removeValue((TransformComponent) component, true);
			}
		}
	}
}
