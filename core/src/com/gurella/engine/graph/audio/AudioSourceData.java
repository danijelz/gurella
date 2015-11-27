package com.gurella.engine.graph.audio;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.audio.AudioTrack;
import com.gurella.engine.audio.Pan;
import com.gurella.engine.audio.Pitch;
import com.gurella.engine.audio.Volume;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.movement.TransformComponent;
import com.gurella.engine.pools.SynchronizedPools;

class AudioSourceData implements Poolable {
	private static Attenuation defaultAttenuation = Attenuation.ROLLOFF;

	AudioSourceComponent audioSourceComponent;
	TransformComponent transformComponent;

	private final Vector3 lastPosition = new Vector3(Float.NaN, Float.NaN, Float.NaN);
	final Vector3 position = new Vector3();
	final Vector3 velocity = new Vector3();
	final Vector3 lookAt = new Vector3();

	final Volume volume = new Volume();
	final Pan pan = new Pan();
	final Pitch pitch = new Pitch();

	LongMap<AudioTrack> activeAudioTracks = new LongMap<AudioTrack>();

	private final Quaternion tempRotation = new Quaternion();

	static AudioSourceData getInstance() {
		return SynchronizedPools.obtain(AudioSourceData.class);
	}

	void free() {
		SynchronizedPools.free(this);
	}

	void init(AudioSourceComponent initAudioSourceComponent) {
		SceneNode node = initAudioSourceComponent.getNode();
		this.audioSourceComponent = initAudioSourceComponent;
		this.transformComponent = node.getActiveComponent(TransformComponent.class);
	}

	Vector3 getPosition() {
		return position;
	}

	Attenuation getAttenuation() {
		Attenuation attenuation = audioSourceComponent.attenuation;
		return attenuation == null ? defaultAttenuation : attenuation;
	}

	float getMaxDistance() {
		return audioSourceComponent.maxDistance;
	}

	float getReferenceDistance() {
		return audioSourceComponent.referenceDistance;
	}

	float getRolloffFactor() {
		return audioSourceComponent.rollOff;
	}

	float getVolume() {
		return audioSourceComponent.volume.getVolume();
	}

	float getDopplerFactor() {
		return audioSourceComponent.dopplerFactor;
	}

	float getDopplerVelocity() {
		return audioSourceComponent.dopplerVelocity;
	}

	Vector3 getVelocity() {
		return velocity;
	}

	boolean isSpatial() {
		return audioSourceComponent.spatial;
	}

	float getInnerConeAngle() {
		return audioSourceComponent.innerConeAngle.getDegrees();
	}

	float getOuterConeAngle() {
		return audioSourceComponent.outerConeAngle.getDegrees();
	}

	float getOuterConeVolume() {
		return audioSourceComponent.outerConeVolume.getVolume();
	}

	Vector3 getLookAt() {
		return lookAt.set(audioSourceComponent.lookAt);
	}

	void removeInactiveTracks() {
		for (Iterator<AudioTrack> iter = activeAudioTracks.values().iterator(); iter.hasNext();) {
			AudioTrack track = iter.next();
			if (track.getAudioChannel() == null) {
				iter.remove();
			}
		}
	}

	public void updateSpatialData() {
		updatePosition();
		updateVelocity();
		updateLookAt();
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

	private void updateLookAt() {
		lookAt.set(audioSourceComponent.lookAt);
		if (transformComponent != null) {
			transformComponent.getWorldRotation(tempRotation);
			tempRotation.transform(lookAt);
		}
	}

	@Override
	public void reset() {
		lastPosition.set(Float.NaN, Float.NaN, Float.NaN);
		audioSourceComponent = null;
		transformComponent = null;

		volume.setVolume(1);
		pan.setPan(0);
		pitch.setPitch(1);

		activeAudioTracks.clear();
	}
}
