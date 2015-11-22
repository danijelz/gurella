package com.gurella.studio.common;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gurella.studio.GdxEditor;
import com.kotcrab.vis.ui.widget.VisLabel;

public class InputDialog extends Dialog {
	private TextField inputField;
	private TextButton confirmButton;
	private TextButton cancleButton;
	
	private InputDialogListener inputDialogListener;

	public InputDialog(String title, InputDialogListener inputDialogListener) {
		super(title, GdxEditor.skin, "default");
		
		this.inputDialogListener = inputDialogListener;
		
		inputField = new TextField("", GdxEditor.skin);
		
		confirmButton = new TextButton("Confirm", GdxEditor.skin);
		confirmButton.addListener(new ConfirmClickListener());
		
		cancleButton = new TextButton("Cancle", GdxEditor.skin);
		cancleButton.addListener(new CancleClickListener());
		
		add(new VisLabel("Value: "));
		add(inputField).expandX();
		row();
		add(confirmButton).fillX();
		add(cancleButton);
	}
	
	public void setConfirmListener(ClickListener confirmListener) {
		confirmButton.addListener(confirmListener);
	}

	public String getInput() {
		return inputField.getText();
	}
	
	public interface InputDialogListener {
		void onCancle();
		
		void onConfirm(String value);
	}

	public static class InputDialogAdapter implements InputDialogListener {
		@Override
		public void onCancle() {
		}

		@Override
		public void onConfirm(String value) {
		}
	}
	
	private class ConfirmClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			inputDialogListener.onConfirm(inputField.getText());
			hide();
		}
	}

	private class CancleClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			inputDialogListener.onCancle();
			hide();
		}
	}
}
