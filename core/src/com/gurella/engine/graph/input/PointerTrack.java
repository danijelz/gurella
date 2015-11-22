package com.gurella.engine.graph.input;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graph.SceneNode;

public class PointerTrack implements Poolable {
	private PointerTrackerPhase phase;
	private int size;
	private LongArray time = new LongArray();
	private IntArray screenX = new IntArray();
	private IntArray screenY = new IntArray();
	private FloatArray intersectionX = new FloatArray();
	private FloatArray intersectionY = new FloatArray();
	private FloatArray intersectionZ = new FloatArray();
	private Array<SceneNode> nodes = new Array<SceneNode>();

	void add(long eventTime, int sx, int sy, Vector3 intersection, SceneNode node,
			PointerTrackerPhase newPhase) {
		size++;
		time.add(eventTime);
		screenX.add(sx);
		screenY.add(sy);
		intersectionX.add(intersection.x);
		intersectionY.add(intersection.y);
		intersectionZ.add(intersection.z);
		nodes.add(node);
		phase = newPhase;
	}

	public PointerTrackerPhase getPhase() {
		return phase;
	}

	void end() {

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

	public SceneNode getNode(int index) {
		return nodes.get(index);
	}
	
	public long getTimeSpan() {
		return time.peek() - time.first();
	}
	
	public SceneNode getCommonNode() {
		SceneNode node = nodes.get(0);
		if(node == null) {
			return null;
		}
		
		for(int i = 1; i < size; i++) {
			if(!node.equals(getNode(i))) {
				return null;
			}
		}
		
		return node;
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
		nodes.clear();
	}

	public enum PointerTrackerPhase {
		begin, move, end, idle;
	}
}
