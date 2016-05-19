package com.gurella.studio.editor;

import java.util.function.Consumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.event.SubscriptionEvent;
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

	public static void subscribe(Object subscriber) {
		EventService.subscribe(getCurrentApplicationId(), subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		EventService.unsubscribe(getCurrentApplicationId(), subscriber);
	}

	public static <L extends EventSubscription> void notify(SubscriptionEvent<L> event) {
		EventService.notify(getCurrentApplicationId(), event);
	}

	public static <L extends EventSubscription> void notify(Class<L> subscriptionType, Consumer<L> handler) {
		EventService.notify(getCurrentApplicationId(), new GenericSubscriptionEvent<L>(subscriptionType, handler));
	}

	public static void subscribe(Control subscriber) {
		EventService.subscribe(getApplicationId(subscriber), subscriber);
	}

	public static void unsubscribe(Control subscriber) {
		EventService.unsubscribe(getApplicationId(subscriber), subscriber);
	}

	public static <L extends EventSubscription> void notify(Control source, SubscriptionEvent<L> event) {
		EventService.notify(getApplicationId(source), event);
	}

	public static <L extends EventSubscription> void notify(Control source, Class<L> subscriptionType,
			Consumer<L> handler) {
		EventService.notify(getApplicationId(source), new GenericSubscriptionEvent<L>(subscriptionType, handler));
	}

	private static class GenericSubscriptionEvent<L extends EventSubscription> extends SubscriptionEvent<L> {
		private Consumer<L> eventHandler;

		public GenericSubscriptionEvent(Class<L> subscriptionType, Consumer<L> eventHandler) {
			super(subscriptionType);
			this.eventHandler = eventHandler;
		}

		@Override
		protected void notify(L listener) {
			eventHandler.accept(listener);
		}
	}
}
