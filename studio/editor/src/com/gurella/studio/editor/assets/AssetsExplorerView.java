package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.AssetType;
import com.gurella.studio.editor.GurellaSceneEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectorView.Inspectable;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SelectionMessage;

public class AssetsExplorerView extends SceneEditorView {
	private static final String GURELLA_PROJECT_FILE_EXTENSION = "gprj";

	Tree tree;
	IResource rootResource;

	public AssetsExplorerView(GurellaSceneEditor editor, int style) {
		super(editor, "Assets", GurellaStudioPlugin.createImage("icons/resource_persp.gif"), style);

		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		tree = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		initTree(editor);
		tree.addListener(SWT.MouseUp, (e) -> presentInspectable());

		final DragSource source = new DragSource(tree, DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { ResourceTransfer.getInstance() });
		source.addDragListener(new AssetsDragSource());

		AssetsTreeChangedListener listener = new AssetsTreeChangedListener(this);
		IWorkspace workspace = getSceneEditor().getWorkspace();
		workspace.addResourceChangeListener(listener);
		addDisposeListener(e -> workspace.removeResourceChangeListener(listener));
	}

	private void initTree(GurellaSceneEditor editor) {
		try {
			createItems(editor);
		} catch (CoreException e) {
			tree.dispose();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createItems(GurellaSceneEditor editor) throws CoreException {
		IPath rootPath = findAssetsRoot().makeRelativeTo(editor.getProject().getLocation());
		rootResource = editor.getProject().findMember(rootPath);
		if (rootResource instanceof IContainer) {
			createItems(null, rootResource);
		}
	}

	private void presentInspectable() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length < 1) {
			return;
		}

		Object data = selection[0].getData();
		if (data instanceof IFile) {
			postMessage(new SelectionMessage(getInspectable()));
		}
	}

	private Inspectable<?> getInspectable() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof IFile) {
				IFile file = (IFile) data;
				String extension = file.getFileExtension();
				if (AssetType.texture.containsExtension(extension)) {
					return new TextureInspectable(file);
				} else if (AssetType.pixmap.containsExtension(extension)) {
					return new PixmapInspectable(file);
				} else if (AssetType.sound.containsExtension(extension)) {
					return new AudioInspectable(file);
				} else if (AssetType.textureAtlas.containsExtension(extension)) {
					return new TextureAtlasInspectable(file);
				} else if (AssetType.bitmapFont.containsExtension(extension)) {
					return new BitmapFontInspectable(file);
				} else if (AssetType.model.containsExtension(extension)) {
					return new ModelInspectable(file);
				} else if (AssetType.polygonRegion.containsExtension(extension)) {
					return new PolygonRegionInspectable(file);
				} else if (AssetType.prefab.containsExtension(extension)) {
					return new PrefabInspectable(file);
				} else if (AssetType.material.containsExtension(extension)) {
					return new MaterialInspectable(file);
				}
			}
		}
		return null;
	}

	private IPath findAssetsRoot() throws CoreException {
		IPathEditorInput pathEditorInput = (IPathEditorInput) editor.getEditorInput();
		IPath projectPath = editor.getProject().getLocation().makeAbsolute();
		IPath scenePath = pathEditorInput.getPath().removeLastSegments(1).makeAbsolute();
		IPath temp = scenePath;
		while (projectPath.isPrefixOf(temp)) {
			IResource member = editor.getProject().findMember(temp);
			if (member instanceof IContainer && isProjectAssetsFolder((IContainer) member)) {
				return temp;
			}
			temp = temp.removeLastSegments(1);
		}

		return scenePath;
	}

	private static boolean isProjectAssetsFolder(IContainer container) throws CoreException {
		for (IResource member : container.members()) {
			if (member instanceof IFile && GURELLA_PROJECT_FILE_EXTENSION.equals(((IFile) member).getFileExtension())) {
				return true;
			}
		}
		return false;
	}

	private void createItems(TreeItem parentItem, IResource resource) throws CoreException {
		int index = parentItem == null ? tree.getItemCount() : parentItem.getItemCount();
		TreeItem nodeItem = createItem(parentItem, resource, index);
		if (parentItem == null) {
			nodeItem.setExpanded(true);
		}

		if (resource instanceof IContainer) {
			for (IResource member : ((IContainer) resource).members()) {
				createItems(nodeItem, member);
			}
		}
	}

	TreeItem createItem(TreeItem parentItem, IResource resource, int index) {
		TreeItem nodeItem = parentItem == null ? new TreeItem(tree, SWT.NONE, index)
				: new TreeItem(parentItem, SWT.NONE, index);
		nodeItem.setText(resource.getName());
		nodeItem.setData(resource);

		if (resource instanceof IFolder) {
			nodeItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
		} else if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			String extension = file.getFileExtension();
			if (AssetType.texture.containsExtension(extension) || AssetType.pixmap.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/picture.png"));
			} else if (AssetType.sound.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/music.png"));
			} else if (AssetType.textureAtlas.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/textureAtlas.gif"));
			} else if (AssetType.polygonRegion.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/textureAtlas.gif"));
			} else if (AssetType.bitmapFont.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/font.png"));
			} else if (AssetType.model.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/16-cube-green_16x16.png"));
			} else if (AssetType.prefab.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/ice_cube.png"));
			} else if (AssetType.material.containsExtension(extension)) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/material.png"));
			} else {
				nodeItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
			}
		}

		return nodeItem;
	}

	private final class AssetsDragSource extends DragSourceAdapter {
		@Override
		public void dragStart(DragSourceEvent event) {
			TreeItem[] selection = tree.getSelection();
			if (selection.length == 1 && selection[0].getData() instanceof IFile) {
				event.doit = true;
				event.data = selection[0].getData();
			} else {
				event.doit = false;
			}
		}

		@Override
		public void dragSetData(DragSourceEvent event) {
			TreeItem[] selection = tree.getSelection();
			IResource resource = (IResource) selection[0].getData();
			event.data = new IResource[] { resource };
		}
	}
}
