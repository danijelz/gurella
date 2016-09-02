package com.gurella.engine.editor.ui.dialog;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorShell;
import com.gurella.engine.editor.ui.EditorUi;

public interface EditorDialog {
	void open();

	boolean close();

	void create();

	EditorShell getShell();

	void setBlockOnOpen(boolean shouldBlock);

	void closeTray();

	DialogContentFactory getTray();

	void openTray(DialogContentFactory tray);

	public static interface DialogContentFactory {
		void createContent(EditorDialog dialog, EditorComposite parent);
	}

	public static interface DialogActionListener<T> {
		T handle(EditorDialog dialog);
	}

	public static class DialogAction<T> {
		public String text;
		public boolean defaultAction;
		public DialogActionListener<T> listener;

		public DialogAction() {
		}

		public DialogAction(String text, boolean defaultAction, DialogActionListener<T> listener) {
			this.text = text;
			this.defaultAction = defaultAction;
			this.listener = listener;
		}
	}

	public static class EditorDialogProperties {
		public DialogContentFactory dialogAreaFactory;
		public DialogContentFactory trayFactory;
		public boolean openTrayAtStart;
		public Array<DialogAction<?>> actions;
		public boolean blockOnOpen = true;

		public EditorDialogProperties(DialogContentFactory dialogAreaFactory) {
			this.dialogAreaFactory = dialogAreaFactory;
		}

		public EditorDialogProperties dialogAreaFactory(DialogContentFactory dialogAreaFactory) {
			this.dialogAreaFactory = dialogAreaFactory;
			return this;
		}

		public EditorDialogProperties trayFactory(DialogContentFactory trayFactory) {
			this.trayFactory = trayFactory;
			return this;
		}

		public EditorDialogProperties openTrayAtStart(boolean openTrayAtStart) {
			this.openTrayAtStart = openTrayAtStart;
			return this;
		}

		public EditorDialogProperties blockOnOpen(boolean blockOnOpen) {
			this.blockOnOpen = blockOnOpen;
			return this;
		}

		public EditorDialogProperties action(DialogAction<?> action) {
			if (action == null) {
				throw new NullPointerException("action is null");
			}

			if (actions == null) {
				actions = new Array<DialogAction<?>>();
			}

			actions.add(action);
			return this;
		}

		public <T> EditorDialogProperties action(String text, boolean defaultAction, DialogActionListener<T> listener) {
			if (text == null) {
				throw new NullPointerException("action is null");
			}

			if (actions == null) {
				actions = new Array<DialogAction<?>>();
			}

			actions.add(new DialogAction<T>(text, defaultAction, listener));
			return this;
		}

		public <T> EditorDialogProperties action(String text, DialogActionListener<T> listener) {
			return action(text, false, listener);
		}

		public EditorDialogProperties action(String text, boolean defaultAction) {
			return action(text, defaultAction, null);
		}

		public EditorDialogProperties action(String text) {
			return action(text, false, null);
		}

		public <T> T show(EditorUi ui) {
			return ui.showDialog(this.blockOnOpen(true));
		}
	}
}
