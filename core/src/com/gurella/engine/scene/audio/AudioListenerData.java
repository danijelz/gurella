package com.gurella.engine.scene.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.transform.TransformComponent;

class AudioListenerData implements Poolable {
	AudioListenerComponent audioListenerComponent;
	TransformComponent transformComponent;

	private final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	final Vector3 position = new Vector3();
	final Vector3 velocity = new Vector3();
	final Vector3 up = new Vector3();
	final Vector3 direction = new Vector3();

	private final Quaternion tempRotation = new Quaternion();

	static AudioListenerData getInstance() {
		return PoolService.obtain(AudioListenerData.class);
	}

	void free() {
		PoolService.free(this);
	}

	void init(AudioListenerComponent audioListenerComponent) {
		this.audioListenerComponent = audioListenerComponent;
		SceneNode node = audioListenerComponent.getNode();
		this.transformComponent = node.getComponent(TransformComponent.class);
	}

	Vector3 getPosition() {
		return position;
	}

	Vector3 getUp() {
		return up.set(audioListenerComponent.up);
	}

	Vector3 getDirection() {
		return direction.set(audioListenerComponent.direction);
	}

	Vector3 getVelocity() {
		return velocity;
	}

	public void updateSpatialData() {
		updatePosition();
		updateVelocity();
		updateUpAndDirection();
	}

	private void updatePosition() {
		if (transformComponent == null) {
			position.setZero();
		} else {
			transformComponent.getWorldTranslation(position);
		}
	}

	private Vector3 updateVelocity() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (deltaTime == 0) {
			return velocity;
		}

		if (lastPosition.x == Float.NaN) {
			velocity.setZero();
		} else {
			velocity.set(position).sub(lastPosition);
			velocity.scl(1.0f / deltaTime);
		}

		lastPosition.set(position);

		return velocity;
	}

	private void updateUpAndDirection() {
		up.set(audioListenerComponent.up);
		direction.set(audioListenerComponent.direction);
		if (transformComponent != null) {
			transformComponent.getWorldRotation(tempRotation);
			tempRotation.transform(up);
			tempRotation.transform(direction);
		}
	}

	@Override
	public void reset() {
		lastPosition.set(Float.NaN, Float.NaN, Float.NaN);
		audioListenerComponent = null;
		transformComponent = null;
	}
}
