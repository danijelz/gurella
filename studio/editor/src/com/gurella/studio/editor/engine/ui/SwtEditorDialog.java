package com.gurella.studio.editor.engine.ui;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.ui.EditorShell;
import com.gurella.engine.editor.ui.dialog.EditorDialog;
import com.gurella.studio.editor.utils.UiUtils;

public class SwtEditorDialog implements EditorDialog {
	private EditorDialogProperties properties;
	private TrayDialogImpl dialog;
	Object returnValue;

	public SwtEditorDialog(EditorDialogProperties properties) {
		this.properties = properties;
		dialog = new TrayDialogImpl(SwtEditorUi.getShell());
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

		if (properties.tray!= null) {
			dialog.openTray(properties.tray);
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
	public DialogPart getTray() {
		DialogTrayImpl tray = (DialogTrayImpl) dialog.getTray();
		return tray.trayFactory;
	}

	@Override
	public void openTray(DialogPart tray) {
		dialog.openTray(tray);
	}

	@Override
	public DialogPart getContent() {
		return properties.content;
	}

	private class TrayDialogImpl extends TrayDialog {
		TrayDialogImpl(Shell shell) {
			super(shell);
		}

		public void openTray(DialogPart trayFactory) {
			openTray(trayFactory == null ? null : new DialogTrayImpl(trayFactory));
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);
			DialogPart content = properties.content;
			if (content != null) {
				SwtEditorComposite editorArea = new SwtEditorComposite(area);
				content.init(SwtEditorDialog.this, editorArea);
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
					button.addListener(SWT.Selection, e -> returnValue = action.listener.handle(SwtEditorDialog.this));
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
		private DialogPart trayFactory;

		public DialogTrayImpl(DialogPart trayFactory) {
			this.trayFactory = trayFactory;
		}

		@Override
		protected Control createContents(Composite parent) {
			Composite content = UiUtils.createComposite(parent);
			SwtEditorComposite editorContent = new SwtEditorComposite(content);
			trayFactory.init(SwtEditorDialog.this, editorContent);
			return content;
		}
	}
}
