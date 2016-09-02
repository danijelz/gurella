package com.gurella.engine.editor.ui.dialog;

import java.io.InputStream;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorUi;

public interface EditorTitleAreaDialog extends EditorDialog {
	String getErrorMessage();

	String getMessage();

	void setErrorMessage(String newErrorMessage);

	void setMessage(String newMessage);

	void setMessage(String newMessage, TitleAreaDialogImage image);

	void setTitle(String newTitle);

	void setTitleAreaColor(Color color);

	void setTitleAreaColor(int r, int g, int b);

	void setTitleImage(EditorImage newTitleImage);

	void setTitleImage(InputStream newTitleImageStream);

	public enum TitleAreaDialogImage {
		NONE, INFORMATION, WARNING, ERROR;
	}

	public static class EditorTitleAteaDialogProperties
			extends BaseEditorDialogProperties<EditorTitleAteaDialogProperties> {
		public String title;
		public String message;
		public Color titleAreaColor;
		public InputStream titleImage;

		public EditorTitleAteaDialogProperties(DialogContentFactory dialogAreaFactory) {
			super(dialogAreaFactory);
		}

		public EditorTitleAteaDialogProperties title(String title) {
			this.title = title;
			return this;
		}

		public EditorTitleAteaDialogProperties message(String message) {
			this.message = message;
			return this;
		}

		public EditorTitleAteaDialogProperties titleAreaColor(Color titleAreaColor) {
			this.titleAreaColor = titleAreaColor;
			return this;
		}

		public EditorTitleAteaDialogProperties titleImageStream(InputStream titleImageStream) {
			this.titleImage = titleImageStream;
			return this;
		}

		public <R> R show(EditorUi ui) {
			return ui.showDialog(this.blockOnOpen(true));
		}
	}
}
