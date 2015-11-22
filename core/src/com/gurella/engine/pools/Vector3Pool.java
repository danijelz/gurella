package com.gurella.engine.pools;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class Vector3Pool {
	private static final Vector3PoolDelegate DELEGATE = new Vector3PoolDelegate();

	private Vector3Pool() {
	}

	public static void free(Vector3 vector) {
		DELEGATE.free(vector);
	}

	public static Vector3 obtain() {
		return DELEGATE.obtain();
	}

	public static Vector3 obtain(float x, float y, float z) {
		return DELEGATE.obtain(x, y, z);
	}

	public static Vector3 obtain(Vector3 vector) {
		return DELEGATE.obtain(vector);
	}

	private static class Vector3PoolDelegate extends Pool<Vector3> {
		public Vector3PoolDelegate() {
			super(16, 5000);
		}

		@Override
		protected Vector3 newObject() {
			return new Vector3();
		}

		public Vector3 obtain(float x, float y, float z) {
			return super.obtain().set(x, y, z);
		}

		public Vector3 obtain(Vector3 vector) {
			return super.obtain().set(vector);
		}
	}
}
