package com.gurella.engine.editor.ui.dialog;

import static com.gurella.engine.utils.Values.cast;

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

	DialogPart getTray();

	DialogPart getContent();

	void openTray(DialogPart tray);

	public interface DialogPart {
		void init(EditorDialog dialog, EditorComposite parent);
	}

	public interface DialogActionListener<T> {
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

	public static class BaseEditorDialogProperties<T extends BaseEditorDialogProperties<T>> {
		public DialogPart content;
		public DialogPart tray;
		public Array<DialogAction<?>> actions;
		public boolean blockOnOpen = true;

		public BaseEditorDialogProperties(DialogPart content) {
			this.content = content;
		}

		public T content(DialogPart content) {
			this.content = content;
			return cast(this);
		}

		public T tray(DialogPart tray) {
			this.tray = tray;
			return cast(this);
		}

		public T blockOnOpen(boolean blockOnOpen) {
			this.blockOnOpen = blockOnOpen;
			return cast(this);
		}

		public T action(DialogAction<?> action) {
			if (action == null) {
				throw new NullPointerException("action is null");
			}

			if (actions == null) {
				actions = new Array<DialogAction<?>>();
			}

			actions.add(action);
			return cast(this);
		}

		public <A> T action(String text, boolean defaultAction, DialogActionListener<A> listener) {
			if (text == null) {
				throw new NullPointerException("action is null");
			}

			if (actions == null) {
				actions = new Array<DialogAction<?>>();
			}

			actions.add(new DialogAction<A>(text, defaultAction, listener));
			return cast(this);
		}

		public <A> T action(String text, DialogActionListener<A> listener) {
			return action(text, false, listener);
		}

		public T action(String text, boolean defaultAction) {
			return action(text, defaultAction, null);
		}

		public T action(String text) {
			return action(text, false, null);
		}
	}

	public static class EditorDialogProperties extends BaseEditorDialogProperties<EditorDialogProperties> {
		public EditorDialogProperties(DialogPart dialogAreaFactory) {
			super(dialogAreaFactory);
		}

		public <R> R show(EditorUi ui) {
			return ui.showDialog(this.blockOnOpen(true));
		}
	}
}
