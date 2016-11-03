package com.gurella.studio.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.studio.editor.control.Dock;
import com.gurella.studio.editor.swtgl.SwtLwjglApplication;

public class SceneEditorRegistry {
	public static final int invalidId = -1;

	private static final IntMap<SceneEditor> idToEditor = new IntMap<>();
	private static final ObjectIntMap<Dock> partControlToEditorId = new ObjectIntMap<>();
	private static final ObjectIntMap<SwtLwjglApplication> gdxAppToEditorId = new ObjectIntMap<>();
	private static final IntMap<SceneEditorContext> idToContext = new IntMap<>();

	private SceneEditorRegistry() {
	}

	static void put(SceneEditor editor, Dock dock, SwtLwjglApplication application, SceneEditorContext context) {
		int id = editor.id;
		idToEditor.put(id, editor);
		partControlToEditorId.put(dock, id);
		gdxAppToEditorId.put(application, id);
		idToContext.put(id, context);
	}

	static void remove(SceneEditor editor) {
		int id = editor.id;
		idToEditor.remove(id);
		partControlToEditorId.remove(partControlToEditorId.findKey(id), invalidId);
		gdxAppToEditorId.remove(gdxAppToEditorId.findKey(id), invalidId);
		idToContext.remove(id);
	}

	static int getEditorId(Control control) {
		Composite parent = control instanceof Composite ? (Composite) control : control.getParent();
		while (parent != null) {
			if (parent instanceof Dock) {
				return partControlToEditorId.get((Dock) parent, invalidId);
			}
		}

		return invalidId;
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
		return idToContext.get(editorId);
	}
}
