package com.gurella.studio.editor;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.async.AsyncService;
import com.gurella.studio.editor.swtgdx.SwtLwjglApplication;
import com.gurella.studio.gdx.GdxContext;

public class SceneEditorRegistry {
	public static final int invalidId = -1;

	private static final IntMap<SceneEditor> editorsById = new IntMap<>();
	private static final ObjectIntMap<Control> idByPartControl = new ObjectIntMap<>();
	private static final ObjectIntMap<SwtLwjglApplication> idByGdxApp = new ObjectIntMap<>();

	private SceneEditorRegistry() {
	}

	static void put(SceneEditor editor, Composite content, SwtLwjglApplication application, IJavaProject javaProject) {
		int id = editor.id;
		editorsById.put(id, editor);
		idByPartControl.put(content, id);
		idByGdxApp.put(application, id);
		GdxContext.activate(id, application, javaProject);
	}

	static void remove(SceneEditor editor) {
		int id = editor.id;
		editorsById.remove(id);
		idByPartControl.remove(idByPartControl.findKey(id), invalidId);
		idByGdxApp.remove(idByGdxApp.findKey(id), invalidId);
		GdxContext.deactivate(id);
	}

	static int getEditorId(Control control) {
		Composite parent = control instanceof Composite ? (Composite) control : control.getParent();
		int id = invalidId;

		while (parent != null && id == invalidId) {
			id = idByPartControl.get(parent, invalidId);
			parent = parent.getParent();
		}

		return id;
	}

	static int getCurrentEditorId() {
		Application app = AsyncService.getApplication();
		if (app instanceof SwtLwjglApplication) {
			return idByGdxApp.get((SwtLwjglApplication) app, invalidId);
		} else {
			return invalidId;
		}
	}

	static SceneEditor getCurrentEditor() {
		Application app = AsyncService.getApplication();
		if (app instanceof SwtLwjglApplication) {
			return editorsById.get(idByGdxApp.get((SwtLwjglApplication) app, invalidId));
		} else {
			return null;
		}
	}

	public static SceneEditorContext getContext(int editorId) {
		SceneEditor sceneEditor = editorsById.get(editorId);
		return sceneEditor == null ? null : sceneEditor.sceneContext;
	}
}
