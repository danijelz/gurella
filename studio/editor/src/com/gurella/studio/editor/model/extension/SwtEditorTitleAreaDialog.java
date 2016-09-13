package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorShell;
import com.gurella.engine.editor.ui.dialog.EditorTitleAreaDialog;
import com.gurella.studio.editor.utils.UiUtils;

public class SwtEditorTitleAreaDialog implements EditorTitleAreaDialog {
	private EditorTitleAteaDialogProperties properties;
	private TitleAreaDialogImpl dialog;
	Object returnValue;

	public SwtEditorTitleAreaDialog(EditorTitleAteaDialogProperties properties) {
		this.properties = properties;
		dialog = new TitleAreaDialogImpl(SwtEditorUi.getShell());
		setBlockOnOpen(properties.blockOnOpen);
	}

	@Override
	public void open() {
		dialog.open();
	}

	@Override
	public boolean close() {
		return dialog.close();
	}

	@Override
	public void create() {
		dialog.create();

		if (properties.trayFactory != null) {
			dialog.openTray(properties.trayFactory);
		}

		if (properties.title != null) {
			setTitle(properties.title);
		}

		if (properties.message != null) {
			setMessage(properties.message);
		}

		if (properties.titleAreaColor != null) {
			setTitleAreaColor(properties.titleAreaColor);
		}

		if (properties.titleImage != null) {
			setTitleImage(properties.titleImage);
		}
	}

	@Override
	public EditorShell getShell() {
		return SwtEditorWidget.getEditorWidget(dialog.getShell());
	}

	@Override
	public void setBlockOnOpen(boolean shouldBlock) {
		dialog.setBlockOnOpen(shouldBlock);
	}

	@Override
	public void closeTray() {
		dialog.closeTray();
	}

	@Override
	public DialogContentFactory getTray() {
		DialogTrayImpl tray = (DialogTrayImpl) dialog.getTray();
		return tray.trayFactory;
	}

	@Override
	public void openTray(DialogContentFactory tray) {
		dialog.openTray(tray);
	}

	@Override
	public String getErrorMessage() {
		return dialog.getErrorMessage();
	}

	@Override
	public String getMessage() {
		return dialog.getMessage();
	}

	@Override
	public void setErrorMessage(String newErrorMessage) {
		dialog.setErrorMessage(newErrorMessage);
	}

	@Override
	public void setMessage(String newMessage) {
		dialog.setMessage(newMessage);
	}

	@Override
	public void setMessage(String newMessage, TitleAreaDialogImage image) {
		if (image == null) {
			dialog.setMessage(newMessage);
		} else {
			dialog.setMessage(newMessage, image(image));
		}
	}

	private static int image(TitleAreaDialogImage image) {
		switch (image) {
		case ERROR:
			return IMessageProvider.ERROR;
		case INFORMATION:
			return IMessageProvider.INFORMATION;
		case WARNING:
			return IMessageProvider.WARNING;
		default:
			return IMessageProvider.NONE;
		}
	}

	@Override
	public void setTitle(String newTitle) {
		dialog.setTitle(newTitle);
	}

	@Override
	public void setTitleAreaColor(Color color) {
		dialog.setTitleAreaColor(new RGB(color.r * 255, color.g * 255, color.b * 255));
	}

	@Override
	public void setTitleAreaColor(int r, int g, int b) {
		dialog.setTitleAreaColor(new RGB(r, g, b));
	}

	@Override
	public void setTitleImage(EditorImage newTitleImage) {
		dialog.setTitleImage(SwtEditorWidget.toSwtImage(newTitleImage));
	}

	@Override
	public void setTitleImage(InputStream newTitleImageStream) {
		dialog.setTitleImage(SwtEditorWidget.toSwtImage(dialog.getShell(), newTitleImageStream));
	}

	private class TitleAreaDialogImpl extends TitleAreaDialog {
		TitleAreaDialogImpl(Shell shell) {
			super(shell);
		}

		public void openTray(DialogContentFactory trayFactory) {
			openTray(trayFactory == null ? null : new DialogTrayImpl(trayFactory));
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);
			DialogContentFactory dialogAreaFactory = properties.dialogAreaFactory;
			if (dialogAreaFactory != null) {
				SwtEditorComposite editorArea = new SwtEditorComposite(area);
				dialogAreaFactory.createContent(SwtEditorTitleAreaDialog.this, editorArea);
			}
			return area;
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			Array<DialogAction<?>> actions = properties.actions;
			if (actions == null) {
				super.createButtonsForButtonBar(parent);
				return;
			}

			for (int i = 0, n = actions.size; i < n; ++i) {
				DialogAction<?> action = actions.get(i);
				Button button = this.createButton(parent, i, action.text, action.defaultAction);

				if (action.listener != null) {
					button.addListener(SWT.Selection,
							e -> returnValue = action.listener.handle(SwtEditorTitleAreaDialog.this));
				}

				if (action.defaultAction) {
					Shell shell = parent.getShell();
					if (shell != null) {
						shell.setDefaultButton(button);
					}
				}
			}
		}
	}

	private class DialogTrayImpl extends DialogTray {
		private DialogContentFactory trayFactory;

		public DialogTrayImpl(DialogContentFactory trayFactory) {
			this.trayFactory = trayFactory;
		}

		@Override
		protected Control createContents(Composite parent) {
			Composite content = UiUtils.createComposite(parent);
			SwtEditorComposite editorContent = new SwtEditorComposite(content);
			trayFactory.createContent(SwtEditorTitleAreaDialog.this, editorContent);
			return content;
		}
	}
}
