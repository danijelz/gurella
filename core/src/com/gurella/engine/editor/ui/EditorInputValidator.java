package com.gurella.engine.editor.ui;

import com.gurella.engine.utils.Values;

public interface EditorInputValidator {
	String isValid(String newText);

	public static class BlankTextValidator implements EditorInputValidator {
		private String message;

		public BlankTextValidator(String message) {
			this.message = message;
		}

		@Override
		public String isValid(String newText) {
			return Values.isBlank(newText) ? message : null;
		}
	}

	public static class EmptyTextValidator implements EditorInputValidator {
		private String message;

		public EmptyTextValidator(String message) {
			this.message = message;
		}

		@Override
		public String isValid(String newText) {
			return newText.length() == 0 ? message : null;
		}
	}
}
