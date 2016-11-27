package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;

public class ObjectInstanceMask<T extends ManagedObject> implements Predicate<T>, Poolable {
	private boolean allAlowed = true;
	private final IntSet allowed = new IntSet();
	private final IntSet ignored = new IntSet();

	public ObjectInstanceMask<T> allowed(T managedObject) {
		return allowed(managedObject.instanceId);
	}

	public ObjectInstanceMask<T> allowed(int instanceId) {
		ignored.remove(instanceId);
		allowed.add(instanceId);
		allAlowed = false;
		return this;
	}

	public ObjectInstanceMask<T> ignored(T managedObject) {
		return ignored(managedObject.instanceId);
	}

	public ObjectInstanceMask<T> ignored(int instanceId) {
		allowed.remove(instanceId);
		ignored.add(instanceId);
		allAlowed = allowed.size < 1;
		return this;
	}

	public boolean isValid(T managedObject) {
		return managedObject != null && isValid(managedObject.instanceId);
	}

	public boolean isValid(int instanceId) {
		return ignored.contains(instanceId) ? false : allAlowed ? true : allowed.contains(instanceId);
	}

	@Override
	public void reset() {
		allAlowed = true;
		allowed.clear();
		ignored.clear();
	}

	@Override
	public boolean evaluate(T obj) {
		return isValid(obj.instanceId);
	}
}
