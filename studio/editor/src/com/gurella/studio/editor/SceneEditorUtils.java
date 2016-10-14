package com.gurella.studio.editor;

import java.util.function.Consumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class SceneEditorUtils {
	public static final int invalidId = -1;

	private static final IntMap<SceneEditor> idToEditor = new IntMap<>();
	private static final ObjectIntMap<Dock> partControlToEditorId = new ObjectIntMap<>();
	private static final ObjectIntMap<SwtLwjglApplication> gdxAppToEditorId = new ObjectIntMap<>();
	private static final IntMap<SceneEditorContext> appIdToContext = new IntMap<>();

	private SceneEditorUtils() {
	}

	static void put(SceneEditor editor, Dock partControl, SwtLwjglApplication application,
			SceneEditorContext context) {
		int id = editor.id;
		idToEditor.put(id, editor);
		partControlToEditorId.put(partControl, id);
		gdxAppToEditorId.put(application, id);
		appIdToContext.put(id, context);
	}

	static void remove(SceneEditor editor) {
		int id = editor.id;
		idToEditor.remove(id);
		partControlToEditorId.remove(partControlToEditorId.findKey(id), invalidId);
		gdxAppToEditorId.remove(gdxAppToEditorId.findKey(id), invalidId);
		appIdToContext.remove(id);
	}

	public static int getApplicationId(Control control) {
		Composite parent = control instanceof Composite ? (Composite) control : control.getParent();
		while (parent != null) {
			if (parent instanceof Dock) {
				return partControlToEditorId.get((Dock) parent, invalidId);
			}
		}

		return invalidId;
	}

	public static int getCurrentApplicationId() {
		Application app = Gdx.app;
		if (app instanceof SwtLwjglApplication) {
			return gdxAppToEditorId.get((SwtLwjglApplication) app, invalidId);
		} else {
			return invalidId;
		}
	}

	public static SceneEditor getCurrentEditor() {
		Application app = Gdx.app;
		if (app instanceof SwtLwjglApplication) {
			return idToEditor.get(gdxAppToEditorId.get((SwtLwjglApplication) app, invalidId));
		} else {
			return null;
		}
	}

	public static void subscribe(Object subscriber) {
		EventService.subscribe(getCurrentApplicationId(), subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		EventService.unsubscribe(getCurrentApplicationId(), subscriber);
	}

	public static <L extends EventSubscription> void notify(Event<L> event) {
		EventService.post(getCurrentApplicationId(), event);
	}

	public static <L extends EventSubscription> void notify(Class<L> subscriptionType, Consumer<L> dispatcher) {
		EventService.post(getCurrentApplicationId(), new GenericEvent<L>(subscriptionType, dispatcher));
	}

	public static void subscribe(Control subscriber) {
		EventService.subscribe(getApplicationId(subscriber), subscriber);
	}

	public static void unsubscribe(Control subscriber) {
		EventService.unsubscribe(getApplicationId(subscriber), subscriber);
	}

	public static <L extends EventSubscription> void notify(Control source, Event<L> event) {
		EventService.post(getApplicationId(source), event);
	}

	public static <L extends EventSubscription> void notify(Control source, Class<L> subscriptionType,
			Consumer<L> dispatcher) {
		EventService.post(getApplicationId(source), new GenericEvent<L>(subscriptionType, dispatcher));
	}

	private static class GenericEvent<L extends EventSubscription> implements Event<L> {
		private Class<L> subscriptionType;
		private Consumer<L> dispatcher;

		public GenericEvent(Class<L> subscriptionType, Consumer<L> dispatcher) {
			this.subscriptionType = subscriptionType;
			this.dispatcher = dispatcher;
		}

		@Override
		public void dispatch(L listener) {
			dispatcher.accept(listener);
		}

		@Override
		public Class<L> getSubscriptionType() {
			return subscriptionType;
		}
	}
}
