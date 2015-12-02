package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

final class MethodSignature implements Poolable {
	Class<?> declaringClass;
	String name;
	Class<?>[] parameterTypes;

	static MethodSignature obtain(Class<?> declaringClass, String name, Class<?>... parameterTypes) {
		MethodSignature signature = Pools.obtain(MethodSignature.class);
		signature.declaringClass = declaringClass;
		signature.name = name;
		signature.parameterTypes = parameterTypes;
		return signature;
	}

	@Override
	public void reset() {
		declaringClass = null;
		name = null;
		parameterTypes = null;
	}

	public void free() {
		Pools.free(this);
	}

	@Override
	public int hashCode() {
		return 31 + declaringClass.hashCode() + name.hashCode() + Arrays.hashCode(parameterTypes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (MethodSignature.class != obj.getClass()) {
			return false;
		}
		MethodSignature other = (MethodSignature) obj;
		return declaringClass.equals(other.declaringClass) && name.equals(other.name)
				&& Arrays.equals(parameterTypes, other.parameterTypes);
	}
	
	@Override
	public String toString() {
		return "MethodSignature [declaringClass=" + declaringClass + ", name=" + name + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + "]";
	}
}