package com.gurella.studio.editor.assets;

import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.SEPARATOR;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.studio.editor.SceneEditorContext;

class AssetsViewMenu {
	private final AssetsView view;

	AssetsViewMenu(AssetsView view) {
		this.view = view;
	}

	public void show(IResource selection) {
		Menu menu = new Menu(view.getShell(), POP_UP);
		new MenuPopulator(view, selection).populate(menu);
		menu.setLocation(view.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private static class MenuPopulator {
		private final AssetsView view;
		private final Clipboard clipboard;
		private final SceneEditorContext context;
		private final int editorId;
		private final IResource selection;

		public MenuPopulator(AssetsView view, IResource selection) {
			this.view = view;
			this.clipboard = view.clipboard;
			this.context = view.editorContext;
			this.editorId = context.editorId;
			this.selection = selection;
		}

		public void populate(Menu menu) {
			boolean selected = selection != null;
			final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
			boolean assetInClipboard = clipboard.getContents(transfer) instanceof AssetSelection;

			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText("Cut");
			item.addListener(SWT.Selection, e -> view.cut(selection));
			item.setEnabled(selected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Copy");
			item.addListener(SWT.Selection, e -> view.copy(selection));
			item.setEnabled(selected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Paste");
			item.addListener(SWT.Selection, e -> view.paste(selection));
			item.setEnabled(selected && assetInClipboard);
			addSeparator(menu);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Delete");
			item.addListener(SWT.Selection, e -> view.delete(selection));
			item.setEnabled(selected);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Rename");
			item.addListener(SWT.Selection, e -> rename());
			item.setEnabled(selected);
			addSeparator(menu);
		}

		private void rename() {
			// TODO Auto-generated method stub
			String newName = "";
			view.rename(selection, newName);
		}

		private static MenuItem addSeparator(Menu menu) {
			return new MenuItem(menu, SEPARATOR);
		}
	}
}
