package com.gurella.engine.disposable;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class DisposablesService {
	private static final ObjectMap<Application, Array<Disposable>> instances = new ObjectMap<Application, Array<Disposable>>();

	private DisposablesService() {
	}

	private static Array<Disposable> getDisposables() {
		synchronized (instances) {
			Array<Disposable> array = instances.get(Gdx.app);
			if (array == null) {
				array = new Array<Disposable>();
				instances.put(Gdx.app, array);
			}
			return array;
		}
	}

	public static void disposeAll() {
		Array<Disposable> disposables = getDisposables();
		for (int i = disposables.size - 1; i >= 0; i--) {
			Disposable disposable = disposables.get(i);
			disposable.dispose();
		}
		disposables.clear();
	}

	public static <T extends Disposable> T add(T disposable) {
		Array<Disposable> disposables = getDisposables();
		if (!disposables.contains(disposable, true)) {
			disposables.add(disposable);
		}
		return disposable;
	}

	public static <T> T tryAdd(T object) {
		if (object instanceof Disposable) {
			add(((Disposable) object));
		}
		return object;
	}

	public static void dispose(Disposable disposable) {
		Array<Disposable> disposables = getDisposables();
		disposables.removeValue(disposable, true);
		disposable.dispose();
	}

	public static void tryDispose(Object object) {
		if (object instanceof Disposable) {
			dispose(((Disposable) object));
		}
	}
}
