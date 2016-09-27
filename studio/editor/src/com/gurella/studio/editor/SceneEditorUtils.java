package com.gurella.studio.editor;

import java.util.function.BiConsumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.event.Event1;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.studio.editor.scene.SceneEditorPartControl;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class SceneEditorUtils {
	public static final int invalidId = -1;

	private static final IntMap<GurellaSceneEditor> idToEditor = new IntMap<>();
	private static final ObjectIntMap<SceneEditorPartControl> partControlToEditorId = new ObjectIntMap<>();
	private static final ObjectIntMap<SwtLwjglApplication> gdxAppToEditorId = new ObjectIntMap<>();
	private static final IntMap<SceneEditorContext> appIdToContext = new IntMap<>();

	private SceneEditorUtils() {
	}

	static void put(GurellaSceneEditor editor, SceneEditorPartControl partControl, SwtLwjglApplication application,
			SceneEditorContext context) {
		int id = editor.id;
		idToEditor.put(id, editor);
		partControlToEditorId.put(partControl, id);
		gdxAppToEditorId.put(application, id);
		appIdToContext.put(id, context);
	}

	static void remove(GurellaSceneEditor editor) {
		int id = editor.id;
		idToEditor.remove(id);
		partControlToEditorId.remove(partControlToEditorId.findKey(id), invalidId);
		gdxAppToEditorId.remove(gdxAppToEditorId.findKey(id), invalidId);
		appIdToContext.remove(id);
	}

	public static int getApplicationId(Control control) {
		Composite parent = control instanceof Composite ? (Composite) control : control.getParent();
		while (parent != null) {
			if (parent instanceof SceneEditorPartControl) {
				return partControlToEditorId.get((SceneEditorPartControl) parent, invalidId);
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

	public static GurellaSceneEditor getCurrentEditor() {
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

	public static <L extends EventSubscription> void notify(Event1<L, ?> event) {
		EventService.post(getCurrentApplicationId(), event, null);
	}

	public static <L extends EventSubscription> void notify(Class<L> subscriptionType, BiConsumer<L, Void> handler) {
		EventService.post(getCurrentApplicationId(), new GenericEvent<L, Void>(subscriptionType, handler), null);
	}

	public static void subscribe(Control subscriber) {
		EventService.subscribe(getApplicationId(subscriber), subscriber);
	}

	public static void unsubscribe(Control subscriber) {
		EventService.unsubscribe(getApplicationId(subscriber), subscriber);
	}

	public static <L extends EventSubscription> void notify(Control source, Event1<L, ?> event) {
		EventService.post(getApplicationId(source), event, null);
	}

	public static <L extends EventSubscription, D> void notify(Control source, Class<L> subscriptionType,
			BiConsumer<L, D> handler, D data) {
		EventService.post(getApplicationId(source), new GenericEvent<L, D>(subscriptionType, handler), data);
	}

	private static class GenericEvent<L extends EventSubscription, D> implements Event1<L, D> {
		private Class<L> subscriptionType;
		private BiConsumer<L, D> eventHandler;

		public GenericEvent(Class<L> subscriptionType, BiConsumer<L, D> eventHandler) {
			this.subscriptionType = subscriptionType;
			this.eventHandler = eventHandler;
		}

		@Override
		public void notify(L listener, D data) {
			eventHandler.accept(listener, data);
		}

		@Override
		public Class<L> getSubscriptionType() {
			return subscriptionType;
		}
	}
}
