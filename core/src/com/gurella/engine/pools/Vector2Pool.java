package com.gurella.engine.pools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Vector2Pool {
	private static final Vector2PoolDelegate DELEGATE = new Vector2PoolDelegate();

	private Vector2Pool() {
	}

	public static void free(Vector2 vector) {
		DELEGATE.free(vector);
	}

	public static Vector2 obtain() {
		return DELEGATE.obtain();
	}

	public static Vector2 obtain(float x, float y) {
		return DELEGATE.obtain(x, y);
	}

	public static Vector2 obtain(Vector2 vector) {
		return DELEGATE.obtain(vector);
	}

	private static class Vector2PoolDelegate extends Pool<Vector2> {
		public Vector2PoolDelegate() {
			super(16, 5000);
		}

		@Override
		protected Vector2 newObject() {
			return new Vector2();
		}

		public Vector2 obtain(float x, float y) {
			return super.obtain().set(x, y);
		}

		public Vector2 obtain(Vector2 vector) {
			return super.obtain().set(vector);
		}
	}
}
