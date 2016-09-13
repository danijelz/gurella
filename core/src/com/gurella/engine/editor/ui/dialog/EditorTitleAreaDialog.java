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

	public static class EditorTitleAreaDialogProperties
			extends BaseEditorDialogProperties<EditorTitleAreaDialogProperties> {
		public String title;
		public String message;
		public Color titleAreaColor;
		public InputStream titleImage;

		public EditorTitleAreaDialogProperties(DialogContentFactory dialogAreaFactory) {
			super(dialogAreaFactory);
		}

		public EditorTitleAreaDialogProperties title(String title) {
			this.title = title;
			return this;
		}

		public EditorTitleAreaDialogProperties message(String message) {
			this.message = message;
			return this;
		}

		public EditorTitleAreaDialogProperties titleAreaColor(Color titleAreaColor) {
			this.titleAreaColor = titleAreaColor;
			return this;
		}

		public EditorTitleAreaDialogProperties titleImageStream(InputStream titleImageStream) {
			this.titleImage = titleImageStream;
			return this;
		}

		public <R> R show(EditorUi ui) {
			return ui.showTitleAreaDialog(this.blockOnOpen(true));
		}
	}
}
