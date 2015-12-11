package com.gurella.engine.scene.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.gurella.engine.application.CommonUpdateOrder;
import com.gurella.engine.application.UpdateListener;
import com.gurella.engine.audio.AudioChannel;
import com.gurella.engine.audio.AudioTrack;
import com.gurella.engine.event.Listener1;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.scene.SceneGraphListener;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.SceneSystem;
import com.gurella.engine.scene.movement.TransformComponent;

//TODO attach listeners on activate
public class AudioSystem extends SceneSystem implements SceneGraphListener, UpdateListener {
	private RemoveOnFinishCompletitionCallback removeOnFinishCompletitionCallback = new RemoveOnFinishCompletitionCallback();

	private Array<AudioListenerData> activeListenersStack = new Array<AudioListenerData>();
	private IntMap<AudioListenerData> activeListeners = new IntMap<AudioListenerData>();
	private IntMap<AudioSourceData> activeSources = new IntMap<AudioSourceData>();

	private Vector3 side = new Vector3();
	private Vector3 distance = new Vector3();
	private Vector3 v1 = new Vector3();
	private Vector3 v2 = new Vector3();

	@Override
	public void componentAdded(SceneNodeComponent component) {
	}

	@Override
	public void componentRemoved(SceneNodeComponent component) {
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof AudioListenerComponent) {
			AudioListenerData audioListenerData = AudioListenerData.getInstance();
			audioListenerData.init((AudioListenerComponent) component);
			activeListeners.put(component.getNode().id, audioListenerData);
			activeListenersStack.add(audioListenerData);
		} else if (component instanceof AudioSourceComponent) {
			AudioSourceComponent audioSourceComponent = (AudioSourceComponent) component;
			audioSourceComponent.audioProcessor = this;
			AudioSourceData audioSourceData = AudioSourceData.getInstance();
			audioSourceData.init(audioSourceComponent);
			activeSources.put(audioSourceComponent.getNode().id, audioSourceData);
		} else if (component instanceof TransformComponent) {
			AudioListenerData audioListenerData = activeListeners.get(component.getNode().id);
			if (audioListenerData != null) {
				audioListenerData.transformComponent = (TransformComponent) component;
			}

			AudioSourceData audioSourceData = activeSources.get(component.getNode().id);
			if (audioSourceData != null) {
				audioSourceData.transformComponent = (TransformComponent) component;
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof AudioListenerComponent) {
			AudioListenerData audioListenerData = activeListeners.remove(component.getNode().getId());
			if (audioListenerData != null) {
				activeListenersStack.removeValue(audioListenerData, true);
				audioListenerData.free();
			}
		} else if (component instanceof AudioSourceComponent) {
			AudioSourceData audioSourceData = activeSources.remove(component.getNode().getId());
			if (audioSourceData != null) {
				((AudioSourceComponent) component).audioProcessor = null;
				LongMap<AudioTrack> activeAudioTracks = audioSourceData.activeAudioTracks;
				for (AudioTrack track : activeAudioTracks.values()) {
					track.free();
				}
				activeAudioTracks.clear();
			}
		} else if (component instanceof TransformComponent) {
			AudioListenerData audioListenerData = activeListeners.get(component.getNode().getId());
			if (audioListenerData != null) {
				audioListenerData.transformComponent = null;
			}

			AudioSourceData audioSourceData = activeSources.get(component.getNode().getId());
			if (audioSourceData != null) {
				audioSourceData.transformComponent = null;
			}
		}
	}

	AudioTrack play(AudioSourceComponent source, AudioChannel audioChannel,
			Listener1<AudioTrack> optionalCompletitionCallback) {
		return obtainTrack(source, audioChannel, optionalCompletitionCallback, false, false);
	}

	AudioTrack loop(AudioSourceComponent source, AudioChannel audioChannel,
			Listener1<AudioTrack> optionalCompletitionCallback) {
		return obtainTrack(source, audioChannel, optionalCompletitionCallback, true, false);
	}

	void playOnce(AudioSourceComponent source, AudioChannel audioChannel,
			Listener1<AudioTrack> optionalCompletitionCallback) {
		obtainTrack(source, audioChannel, optionalCompletitionCallback, false, true);
	}

	private AudioTrack obtainTrack(AudioSourceComponent source, AudioChannel audioChannel,
			Listener1<AudioTrack> optionalCompletitionCallback, boolean loop, boolean removeOnFinish) {
		AudioSourceData audioSourceData = activeSources.get(source.getNode().id);
		if (canObtainTrack(audioSourceData, source.repeatable)) {
			AudioTrack track = AudioTrack.newInstance(getNonNullAudioChannel(audioChannel), source.audioClip);
			track.setPriority(source.priority);
			audioSourceData.activeAudioTracks.put(track.getHandle(), track);
			track.completitionCallbacks.addListener(optionalCompletitionCallback);

			if (removeOnFinish) {
				track.completitionCallbacks.addListener(removeOnFinishCompletitionCallback);
			}

			if (loop) {
				track.loop();
			} else {
				track.play();
			}

			return track;
		} else {
			return null;
		}
	}

	private static boolean canObtainTrack(AudioSourceData audioSourceData, boolean repeatable) {
		if (audioSourceData == null) {
			return false;
		} else {
			audioSourceData.removeInactiveTracks();
			return repeatable || audioSourceData.activeAudioTracks.size == 0;
		}
	}

	private static AudioChannel getNonNullAudioChannel(AudioChannel audioChannel) {
		return audioChannel == null ? GraphAudioChannel.GAME.getAudioChannel() : audioChannel;
	}

	void free(AudioSourceComponent source, AudioTrack track) {
		AudioSourceData audioSourceData = activeSources.get(source.getNode().id);
		if (audioSourceData != null && audioSourceData.activeAudioTracks.remove(track.getHandle()) != null) {
			track.free();
		}
	}

	@Override
	public int getOrdinal() {
		return CommonUpdateOrder.PRE_RENDER;
	}

	@Override
	public void update() {
		AudioListenerData listener = getActiveListener();
		if (listener == null) {
			mute();
		} else {
			update(listener);
		}
	}

	private AudioListenerData getActiveListener() {
		int size = activeListenersStack.size;
		return size > 0 ? activeListenersStack.get(size - 1) : null;
	}

	private void mute() {
		for (AudioSourceData source : activeSources.values()) {
			source.removeInactiveTracks();
			for (AudioTrack track : source.activeAudioTracks.values()) {
				track.setVolume(0);
			}
		}
	}

	private void update(AudioListenerData listener) {
		listener.updateSpatialData();
		for (AudioSourceData source : activeSources.values()) {
			source.updateSpatialData();
			updateSource(listener, source);
		}
	}

	private void updateSource(AudioListenerData listener, AudioSourceData source) {
		source.removeInactiveTracks();
		updateSpatialData(listener, source);
		for (AudioTrack track : source.activeAudioTracks.values()) {
			track.update(source.volume.getVolume(), source.pan.getPan(), source.pitch.getPitch());
		}
	}

	private void updateSpatialData(AudioListenerData listener, AudioSourceData source) {
		if (source.isSpatial()) {
			// TODO cache old data and only update if values change
			updateVolume(listener, source);

			if (source.volume.getVolume() > 0) {
				updatePan(listener, source);
				updatePitch(listener, source);
			}
		} else {
			source.volume.set(source.audioSourceComponent.volume);
			source.pan.set(source.audioSourceComponent.pan);
			source.pitch.set(source.audioSourceComponent.pitch);
		}
	}

	/**
	 * Calculates the gain for this source based on its attenuation model and distance from the listener.
	 */
	private void updateVolume(AudioListenerData listener, AudioSourceData source) {
		float distanceFromListener = listener.getPosition().dst(source.getPosition());

		// Calculate the source's gain using the specified attenuation model:
		float volume;
		float maxDistance = source.getMaxDistance();
		float rolloffFactor = source.getRolloffFactor();
		float referenceDistance = source.getReferenceDistance();

		switch (source.getAttenuation()) {
		case LINEAR:
			if (distanceFromListener <= 0) {
				volume = 1.0f;
			} else if (distanceFromListener >= maxDistance) {
				volume = 0.0f;
			} else {
				volume = 1.0f - (distanceFromListener / maxDistance);
			}
			break;
		case ROLLOFF:
			if (distanceFromListener <= 0) {
				volume = 1.0f;
			} else {
				float tweakFactor = 0.0005f;
				float attenuationFactor = rolloffFactor * distanceFromListener * distanceFromListener * tweakFactor;
				if (attenuationFactor < 0) {
					attenuationFactor = 0;
				}

				volume = 1.0f / (1 + attenuationFactor);
			}
			break;
		case LINEAR_ROLLOFF:
			volume = 1.0f
					- rolloffFactor * (distanceFromListener - referenceDistance) / (maxDistance - referenceDistance);
			break;
		case EXPONENTIAL:
			volume = (float) Math.pow(distanceFromListener / referenceDistance, -rolloffFactor);
			break;
		case INVERSE:
			float clampedDistance = distanceFromListener < referenceDistance ? referenceDistance : distanceFromListener;
			volume = referenceDistance / (referenceDistance + rolloffFactor * (clampedDistance - referenceDistance));
			break;
		default:
			volume = 1.0f;
			break;
		}

		source.volume.setVolume(volume * getConeVolume(listener, source));
	}

	private float getConeVolume(AudioListenerData listener, AudioSourceData source) {
		float inerConeAngle = Math.abs(source.getInnerConeAngle());
		if (inerConeAngle > 180) {
			return 1;
		}

		float angleFromSource = Math.abs(getAngleFromSource(listener, source));
		if (angleFromSource <= inerConeAngle) {
			return 1;
		}

		float outerConeAngle = Math.abs(source.getOuterConeAngle());
		float outerConeVolume = source.getOuterConeVolume();
		if (angleFromSource > outerConeAngle || outerConeAngle <= inerConeAngle) {
			return 0;
		}

		float outerConeAngleDelta = outerConeAngle - inerConeAngle;
		float angleFromSourceDelta = angleFromSource - inerConeAngle;
		return 1 + (angleFromSourceDelta) * (outerConeVolume - 1) / (outerConeAngleDelta);
	}

	private float getAngleFromSource(AudioListenerData listener, AudioSourceData source) {
		Vector3 sourceLookAt = source.getLookAt();
		Vector3 sourcePosition = source.getPosition();
		Vector3 listenerPosition = listener.getPosition();

		v1.set(sourceLookAt).nor();
		v2.set(listenerPosition).sub(sourcePosition).nor();

		return (float) Math.acos(v1.dot(v2)) * MathUtils.radiansToDegrees;
	}

	/**
	 * Calculates the panning for this source based on its position in relation to the listener.
	 */
	private void updatePan(AudioListenerData listener, AudioSourceData source) {
		if (source.getAttenuation() == Attenuation.NONE) {
			source.pan.setPan(0);
			return;
		}

		distance.set(source.getPosition()).sub(listener.getPosition());
		side.set(listener.getUp()).crs(listener.getLookAt()).nor();

		float x = distance.dot(side);
		float z = distance.dot(listener.getLookAt());

		float absReferenceDistance = Math.abs(source.getReferenceDistance());
		if (z >= 0 && z < Math.abs(absReferenceDistance)) {
			z = absReferenceDistance;
		} else if (z < 0 && z > -absReferenceDistance) {
			z = -absReferenceDistance;
		}

		float angle = MathUtils.atan2(x, z);
		float pan = -MathUtils.sin(angle);

		source.pan.setPan(pan);
	}

	/**
	 * Calculates the pitch for this source based on its position in relation to the listener.
	 */
	private static void updatePitch(AudioListenerData listener, AudioSourceData source) {
		if (source.getDopplerFactor() == 0) {
			source.pitch.set(source.audioSourceComponent.pitch);
		} else {
			float SS = 343.3f;

			Vector3 sourceVelocity = source.getVelocity();
			Vector3 listenerVelocity = listener.getVelocity();
			float dopplerVelocity = source.getDopplerVelocity();
			float dopplerFactor = source.getDopplerFactor();
			Vector3 SL = SynchronizedPools.obtain(Vector3.class).set(listener.getPosition()).sub(source.getPosition());

			float vls = SL.isZero() ? 1.0f : SL.dot(listenerVelocity) / SL.len();
			float vss = SL.isZero() ? 1.0f : SL.dot(sourceVelocity) / SL.len();

			vls = Math.min(vls, SS / dopplerFactor);
			vss = Math.min(vss, SS / dopplerFactor);
			float newPitch = source.audioSourceComponent.pitch.getPitch() * (SS * dopplerVelocity - dopplerFactor * vls)
					/ (SS * dopplerVelocity - dopplerFactor * vss);

			source.pitch.setPitch(newPitch);

			SynchronizedPools.free(SL);
		}
	}

	private class RemoveOnFinishCompletitionCallback implements Listener1<AudioTrack> {
		@Override
		public void handle(AudioTrack track) {
			track.free();
		}
	}
}
