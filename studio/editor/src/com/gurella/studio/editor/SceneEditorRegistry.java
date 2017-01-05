package com.gurella.studio.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class SceneEditorRegistry {
	public static final int invalidId = -1;

	private static final IntMap<SceneEditor> idToEditor = new IntMap<>();
	private static final ObjectIntMap<Control> partControlToEditorId = new ObjectIntMap<>();
	private static final ObjectIntMap<SwtLwjglApplication> gdxAppToEditorId = new ObjectIntMap<>();

	private SceneEditorRegistry() {
	}

	static void put(SceneEditor editor, Composite content, SwtLwjglApplication application) {
		int id = editor.id;
		idToEditor.put(id, editor);
		partControlToEditorId.put(content, id);
		gdxAppToEditorId.put(application, id);
	}

	static void remove(SceneEditor editor) {
		int id = editor.id;
		idToEditor.remove(id);
		partControlToEditorId.remove(partControlToEditorId.findKey(id), invalidId);
		gdxAppToEditorId.remove(gdxAppToEditorId.findKey(id), invalidId);
	}

	static int getEditorId(Control control) {
		Composite parent = control instanceof Composite ? (Composite) control : control.getParent();
		int id = invalidId;

		while (parent != null && id == invalidId) {
			id = partControlToEditorId.get(parent, invalidId);
			parent = parent.getParent();
		}

		return id;
	}

	static int getCurrentEditorId() {
		Application app = Gdx.app;
		if (app instanceof SwtLwjglApplication) {
			return gdxAppToEditorId.get((SwtLwjglApplication) app, invalidId);
		} else {
			return invalidId;
		}
	}

	static SceneEditor getCurrentEditor() {
		Application app = Gdx.app;
		if (app instanceof SwtLwjglApplication) {
			return idToEditor.get(gdxAppToEditorId.get((SwtLwjglApplication) app, invalidId));
		} else {
			return null;
		}
	}

	public static SceneEditorContext getContext(int editorId) {
		SceneEditor sceneEditor = idToEditor.get(editorId);
		return sceneEditor == null ? null : sceneEditor.sceneContext;
	}
}
