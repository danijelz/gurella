package com.gurella.engine.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class DisposableManager {
	private Array<Disposable> disposables = new Array<Disposable>();

	public void dispose() {
		for (int i = disposables.size - 1; i >= 0; i--) {
			Disposable disposable = disposables.get(i);
			disposable.dispose();
		}
		disposables.clear();
	}

	public <T extends Disposable> T add(T disposable) {
		disposables.add(disposable);
		return disposable;
	}
	
	public void dispose(Disposable disposable) {
		if(disposables.removeValue(disposable, true)) {
			disposable.dispose();
		}
	}
}
