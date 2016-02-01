package com.gurella.engine.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class DisposablesService {
	private static final Array<Disposable> disposables = new Array<Disposable>();

	private DisposablesService() {
	}

	public static void disposeAll() {
		for (int i = disposables.size - 1; i >= 0; i--) {
			Disposable disposable = disposables.get(i);
			disposable.dispose();
		}
		disposables.clear();
	}

	public static <T extends Disposable> T add(T disposable) {
		if (!disposables.contains(disposable, true)) {
			disposables.add(disposable);
		}
		return disposable;
	}

	public static void dispose(Disposable disposable) {
		disposables.removeValue(disposable, true);
		disposable.dispose();
	}

	public static void tryDispose(Object object) {
		if (object instanceof Disposable) {
			dispose(((Disposable) object));
		}
	}
}
