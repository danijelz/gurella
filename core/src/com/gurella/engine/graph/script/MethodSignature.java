package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;

final class MethodSignature implements Poolable {
	Class<?> declaringClass;
	String methodName;
	Class<?>[] methodParameterTypes;

	static MethodSignature obtain(Class<?> declaringClass, String name, Class<?>... parameterTypes) {
		MethodSignature signature = Pools.obtain(MethodSignature.class);
		signature.declaringClass = declaringClass;
		signature.methodName = name;
		signature.methodParameterTypes = parameterTypes;
		return signature;
	}

	@Override
	public void reset() {
		declaringClass = null;
		methodName = null;
		methodParameterTypes = null;
	}

	public void free() {
		Pools.free(this);
	}

	@Override
	public int hashCode() {
		return 31 + declaringClass.hashCode() + methodName.hashCode() + Arrays.hashCode(methodParameterTypes);
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
		return declaringClass.equals(other.declaringClass) && methodName.equals(other.methodName)
				&& Arrays.equals(methodParameterTypes, other.methodParameterTypes);
	}
}