package com.gurella.studio.editor.assets;

import static com.gurella.studio.GurellaStudioPlugin.getImage;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
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
import com.gurella.engine.event.EventService;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.common.ErrorComposite;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.event.SelectionEvent;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.audio.AudioInspectable;
import com.gurella.studio.editor.inspector.bitmapfont.BitmapFontInspectable;
import com.gurella.studio.editor.inspector.material.MaterialInspectable;
import com.gurella.studio.editor.inspector.model.ModelInspectable;
import com.gurella.studio.editor.inspector.pixmap.PixmapInspectable;
import com.gurella.studio.editor.inspector.polygonregion.PolygonRegionInspectable;
import com.gurella.studio.editor.inspector.prefab.PrefabInspectable;
import com.gurella.studio.editor.inspector.texture.TextureInspectable;
import com.gurella.studio.editor.inspector.textureatlas.TextureAtlasInspectable;

public class AssetsView extends DockableView {
	private static final String GURELLA_PROJECT_FILE_EXTENSION = "gprj";

	Tree tree;
	IResource rootResource;

	private Object lastSelection;

	private final LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

	public AssetsView(SceneEditor editor, int style) {
		super(editor, "Assets", getImage("icons/resource_persp.gif"), style);

		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		tree = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.addListener(SWT.KeyDown, e -> onKeyDown());
		tree.addListener(SWT.KeyUp, e -> onKeyUp());
		tree.addListener(SWT.MouseUp, e -> presentInspectable());
		initTree();

		final DragSource source = new DragSource(tree, DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(new Transfer[] { ResourceTransfer.getInstance(), localTransfer });
		source.addDragListener(new AssetsDragSource());

		AssetsTreeChangedListener listener = new AssetsTreeChangedListener(this);
		IWorkspace workspace = editorContext.workspace;
		workspace.addResourceChangeListener(listener);
		addDisposeListener(e -> workspace.removeResourceChangeListener(listener));
	}

	private void initTree() {
		try {
			createItems();
		} catch (Exception e) {
			presentInitException(e);
		}
	}

	protected void presentInitException(Exception e) {
		tree.dispose();
		String message = "Error creating assets tree";
		IStatus status = GurellaStudioPlugin.log(e, message);
		ErrorComposite errorComposite = new ErrorComposite(this, status, message);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void createItems() throws CoreException {
		IProject project = editorContext.project;
		IPath rootPath = findAssetsRoot().makeRelativeTo(project.getLocation());
		rootResource = project.findMember(rootPath);
		if (rootResource instanceof IContainer) {
			createItems(null, rootResource);
		}
	}

	private void onKeyDown() {
		TreeItem[] selection = tree.getSelection();
		lastSelection = selection.length < 1 ? null : selection[0].getData();
	}

	private void onKeyUp() {
		TreeItem[] selection = tree.getSelection();
		Object currentSelection = selection.length < 1 ? null : selection[0].getData();
		if (currentSelection != null && lastSelection != currentSelection) {
			presentInspectable();
		}
	}

	private void presentInspectable() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length < 1) {
			return;
		}

		Object data = selection[0].getData();
		if (data instanceof IFile) {
			EventService.post(editorContext.editorId, new SelectionEvent(getInspectable()));
		}
	}

	// TODO create plugin extension
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
		IProject project = editorContext.project;
		IPathEditorInput editorInput = editorContext.editorInput;

		IPath projectPath = project.getLocation().makeAbsolute();
		IPath scenePath = editorInput.getPath().removeLastSegments(1).makeAbsolute();
		IPath temp = scenePath;
		while (projectPath.isPrefixOf(temp)) {
			IResource member = project.findMember(temp);
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
			IResource[] members = ((IContainer) resource).members();

			for (IResource member : members) {
				if (member instanceof IContainer) {
					createItems(nodeItem, member);
				}
			}

			for (IResource member : members) {
				if (!(member instanceof IContainer)) {
					createItems(nodeItem, member);
				}
			}
		}
	}

	TreeItem createItem(TreeItem parentItem, IResource resource, int index) {
		TreeItem nodeItem = parentItem == null ? new TreeItem(tree, SWT.NONE, index)
				: new TreeItem(parentItem, SWT.NONE, index);
		nodeItem.setText(resource.getName());
		nodeItem.setData(resource);

		if (resource instanceof IFolder) {
			nodeItem.setImage(getPlatformImage(ISharedImages.IMG_OBJ_FOLDER));
		} else if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			String extension = file.getFileExtension();
			if (Values.isBlank(extension)) {
				nodeItem.setImage(getPlatformImage(ISharedImages.IMG_OBJ_FILE));
			} else if (AssetType.texture.containsExtension(extension)
					|| AssetType.pixmap.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/picture.png"));
			} else if (AssetType.sound.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/music.png"));
			} else if (AssetType.textureAtlas.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/textureAtlas.gif"));
			} else if (AssetType.polygonRegion.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/textureAtlas.gif"));
			} else if (AssetType.bitmapFont.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/font.png"));
			} else if (AssetType.model.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/16-cube-green_16x16.png"));
			} else if (AssetType.prefab.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/ice_cube.png"));
			} else if (AssetType.material.containsExtension(extension)) {
				nodeItem.setImage(getImage("icons/material.png"));
			} else {
				nodeItem.setImage(getPlatformImage(ISharedImages.IMG_OBJ_FILE));
			}
		}

		return nodeItem;
	}

	private static Image getPlatformImage(String symbolicName) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(symbolicName);
	}

	private final class AssetsDragSource implements DragSourceListener {
		@Override
		public void dragStart(DragSourceEvent event) {
			TreeItem[] selection = tree.getSelection();
			if (selection.length == 1 && selection[0].getData() instanceof IFile) {
				event.doit = true;
				event.data = selection[0].getData();
				localTransfer.setSelection(new AssetSelection((IFile) event.data));
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

		@Override
		public void dragFinished(DragSourceEvent event) {
			localTransfer.setSelection(null);
		}
	}
}
