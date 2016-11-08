package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.scene.renderable.RenderableComponent;

public class PointerTrack implements Poolable {
	private PointerTrackerPhase phase;
	private int size;
	private final LongArray time = new LongArray();
	private final IntArray screenX = new IntArray();
	private final IntArray screenY = new IntArray();
	private final FloatArray intersectionX = new FloatArray();
	private final FloatArray intersectionY = new FloatArray();
	private final FloatArray intersectionZ = new FloatArray();
	private final Array<RenderableComponent> renderables = new Array<RenderableComponent>();

	private final Vector3 temp = new Vector3();

	void add(long eventTime, int sx, int sy, Vector3 intersection, RenderableComponent renderable,
			PointerTrackerPhase newPhase) {
		size++;
		time.add(eventTime);
		screenX.add(sx);
		screenY.add(sy);
		intersectionX.add(intersection.x);
		intersectionY.add(intersection.y);
		intersectionZ.add(intersection.z);
		renderables.add(renderable);
		phase = newPhase;
	}

	public PointerTrackerPhase getPhase() {
		return phase;
	}

	public int getSize() {
		return size;
	}

	public long getTime(int index) {
		return time.get(index);
	}

	public int getScreenX(int index) {
		return screenX.get(index);
	}

	public int getScreenY(int index) {
		return screenY.get(index);
	}

	public Vector2 getScreenCoordinates(int index, Vector2 out) {
		return out.set(screenX.get(index), screenY.get(index));
	}

	public float getIntersectionX(int index) {
		return intersectionX.get(index);
	}

	public float getIntersectionY(int index) {
		return intersectionY.get(index);
	}

	public float getIntersectionZ(int index) {
		return intersectionZ.get(index);
	}

	public Vector3 getIntersection(int index) {
		return temp.set(intersectionX.get(index), intersectionY.get(index), intersectionZ.get(index));
	}

	public RenderableComponent getRenderable(int index) {
		return renderables.get(index);
	}

	public long getTimeSpan() {
		return time.peek() - time.first();
	}

	public RenderableComponent getCommonRenderable() {
		RenderableComponent renderable = renderables.get(0);
		if (renderable == null) {
			return null;
		}

		for (int i = 1; i < size; i++) {
			if (!renderable.equals(getRenderable(i))) {
				return null;
			}
		}

		return renderable;
	}

	@Override
	public void reset() {
		phase = PointerTrackerPhase.idle;
		size = 0;
		screenX.clear();
		screenY.clear();
		intersectionX.clear();
		intersectionY.clear();
		intersectionZ.clear();
		time.clear();
		renderables.clear();
	}

	public enum PointerTrackerPhase {
		begin, move, end, idle;
	}
}
