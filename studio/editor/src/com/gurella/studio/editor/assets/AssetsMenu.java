package com.gurella.studio.editor.assets;

import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.SEPARATOR;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.Try;

class AssetsMenu {
	private final AssetsView view;

	AssetsMenu(AssetsView view) {
		this.view = view;
	}

	void show(IResource selection) {
		Menu menu = new Menu(view.getShell(), POP_UP);
		new MenuPopulator(view, selection).populate(menu);
		menu.setLocation(view.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private static class MenuPopulator {
		private final AssetsView view;
		private final Clipboard clipboard;
		private final IResource selection;

		public MenuPopulator(AssetsView view, IResource selection) {
			this.view = view;
			this.clipboard = view.clipboard;
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
			IResource sceneResource = view.editorContext.sceneResource;
			item.setEnabled(selected && !selection.equals(sceneResource));

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Rename");
			item.addListener(SWT.Selection, e -> rename());
			item.setEnabled(selected);
			addSeparator(menu);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Folder");
			item.addListener(SWT.Selection, e -> addNewFolder());
			item.setEnabled(selection instanceof IFolder);

			item = new MenuItem(menu, SWT.PUSH);
			item.setText("Import");
			item.addListener(SWT.Selection, e -> importAssets());
			item.setEnabled(selection instanceof IFolder);
			addSeparator(menu);

			addCreateSubMenu(menu);
		}

		private static MenuItem addSeparator(Menu menu) {
			return new MenuItem(menu, SEPARATOR);
		}

		private void rename() {
			// TODO move to view
			String name = selection.getName();
			InputDialog dlg = new InputDialog(view.getShell(), "Rename", "Enter new name", name, this::validateRename);
			if (dlg.open() == Window.OK) {
				String newName = dlg.getValue();
				view.rename(selection, newName);
			}
		}

		private String validateRename(String newFileName) {
			if (Values.isBlank(newFileName)) {
				return "Name must not be empty";
			}

			IResource member = selection.getParent().findMember(newFileName);
			if (member != null && member.exists()) {
				return "Resource with that name already exists";
			} else {
				return null;
			}
		}

		private void addNewFolder() {
			InputDialog dlg = new InputDialog(view.getShell(), "Rename", "Enter new name", "New folder",
					this::validateNewFolderName);
			if (dlg.open() == Window.OK) {
				String name = dlg.getValue();
				IFolder newFolder = ((IFolder) selection).getFolder(name);
				Try.successful(newFolder).peek(f -> f.create(true, true, new NullProgressMonitor()))
						.onFailure(e -> GurellaStudioPlugin.log(e, "Error creating new folder"));
			}
		}

		private String validateNewFolderName(String newFileName) {
			if (Values.isBlank(newFileName)) {
				return "Name must not be empty";
			}

			IResource member = ((IFolder) selection).findMember(newFileName);
			if (member != null && member.exists()) {
				return "Resource with that name already exists";
			} else {
				return null;
			}
		}

		private void importAssets() {
			FileDialog dlg = new FileDialog(view.getShell(), SWT.MULTI);
			if (dlg.open() == null) {
				return;
			}

			IFolder folder = (IFolder) selection;
			String path = dlg.getFilterPath();
			for (String fileName : dlg.getFileNames()) {
				File file = new File(path, fileName);
				IFile newFile = folder.getFile(fileName);
				Try.successful(newFile).peek(f -> f.create(new FileInputStream(file), true, null)).getUnchecked();
			}
		}

		private void addCreateSubMenu(Menu menu) {
			MenuItem subItem = new MenuItem(menu, SWT.CASCADE);
			subItem.setText("Create");
			Menu subMenu = new Menu(menu);
			subItem.setMenu(subMenu);

			MenuItem item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Prefab");
			item.addListener(SWT.Selection, e -> addNewFolder());
			item.setEnabled(selection instanceof IFolder);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Material");
			item.addListener(SWT.Selection, e -> addNewFolder());
			item.setEnabled(selection instanceof IFolder);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Render target");
			item.addListener(SWT.Selection, e -> addNewFolder());
			item.setEnabled(selection instanceof IFolder);

			item = new MenuItem(subMenu, SWT.PUSH);
			item.setText("Scene");
			item.addListener(SWT.Selection, e -> addNewFolder());
			item.setEnabled(selection instanceof IFolder);
		}
	}
}
