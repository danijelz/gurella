package com.gurella.engine.disposable;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.subscriptions.application.ApplicationShutdownListener;
import com.gurella.engine.utils.priority.Priority;

public class DisposablesService {
	//TODO Array<Disposable> -> OrderedIdentitySet<Disposable>
	private static final IdentityMap<Application, Array<Disposable>> instances = new IdentityMap<Application, Array<Disposable>>();

	private DisposablesService() {
	}

	private static Array<Disposable> getDisposables() {
		synchronized (instances) {
			Array<Disposable> array = instances.get(Gdx.app);
			if (array == null) {
				array = new Array<Disposable>();
				instances.put(Gdx.app, array);
				EventService.subscribe(new Cleaner());
			}
			return array;
		}
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

	@Priority(value = Integer.MAX_VALUE, type = ApplicationShutdownListener.class)
	private static class Cleaner implements ApplicationShutdownListener {
		@Override
		public void shutdown() {
			EventService.unsubscribe(this);
			Array<Disposable> disposables;
			synchronized (instances) {
				disposables = instances.remove(Gdx.app);
			}

			if (disposables != null) {
				for (int i = disposables.size - 1; i >= 0; i--) {
					Disposable disposable = disposables.get(i);
					disposable.dispose();
				}
			}
		}
	}
}
