package com.gurella.studio.editor.model.extension;

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

		if (properties.trayFactory != null) {
			dialog.openTray(properties.trayFactory);
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

	private class TrayDialogImpl extends TrayDialog {
		TrayDialogImpl(Shell shell) {
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
				dialogAreaFactory.createContent(SwtEditorDialog.this, editorArea);
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
		private DialogContentFactory trayFactory;

		public DialogTrayImpl(DialogContentFactory trayFactory) {
			this.trayFactory = trayFactory;
		}

		@Override
		protected Control createContents(Composite parent) {
			Composite content = UiUtils.createComposite(parent);
			SwtEditorComposite editorContent = new SwtEditorComposite(content);
			trayFactory.createContent(SwtEditorDialog.this, editorContent);
			return content;
		}
	}
}
