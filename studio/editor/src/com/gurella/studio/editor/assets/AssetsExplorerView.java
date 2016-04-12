package com.gurella.studio.editor.assets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.asset.AssetType;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.inspector.InspectorView.Inspectable;
import com.gurella.studio.editor.inspector.AudioInspectableContainer;
import com.gurella.studio.editor.inspector.TextureAtlasInspectableContainer;
import com.gurella.studio.editor.inspector.TextureInspectableContainer;
import com.gurella.studio.editor.scene.SceneEditorView;
import com.gurella.studio.editor.scene.SelectionMessage;

public class AssetsExplorerView extends SceneEditorView {
	private static final String GURELLA_PROJECT_FILE_EXTENSION = "gprj";

	Tree tree;
	IResource rootResource;

	public AssetsExplorerView(GurellaEditor editor, int style) {
		super(editor, "Assets", GurellaStudioPlugin.createImage("icons/resource_persp.gif"), style);

		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		tree = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		initTree(editor);

		AssetsTreeChangedListener listener = new AssetsTreeChangedListener(this);
		IWorkspace workspace = getSceneEditor().getWorkspace();
		workspace.addResourceChangeListener(listener);
		addDisposeListener(e -> workspace.removeResourceChangeListener(listener));
	}

	private void initTree(GurellaEditor editor) {
		try {
			createItems(editor);
		} catch (CoreException e) {
			tree.dispose();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createItems(GurellaEditor editor) throws CoreException {
		IPath rootPath = findAssetsRoot().makeRelativeTo(editor.getProject().getLocation());
		rootResource = editor.getProject().findMember(rootPath);
		if (rootResource instanceof IContainer) {
			createItems(null, rootResource);
		}

		tree.addListener(SWT.Selection, (e) -> postMessage(new SelectionMessage(getInspectable())));
	}

	private Inspectable<?> getInspectable() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof IFile) {
				IFile file = (IFile) data;
				if (AssetType.texture.containsExtension(file.getFileExtension())) {
					return new TextureInspectable(file);
				} else if (AssetType.sound.containsExtension(file.getFileExtension())) {
					return new MusicInspectable(file);
				} else if (AssetType.textureAtlas.containsExtension(file.getFileExtension())) {
					return new TextureAtlasInspectable(file);
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
			if (AssetType.texture.containsExtension(file.getFileExtension())) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/picture.png"));
			} else if (AssetType.sound.containsExtension(file.getFileExtension())) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/music.png"));
			} else if (AssetType.textureAtlas.containsExtension(file.getFileExtension())) {
				nodeItem.setImage(GurellaStudioPlugin.createImage("icons/textureAtlas.gif"));
			} else {
				nodeItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
			}
		}

		return nodeItem;
	}

	private static class TextureInspectable implements Inspectable<IFile> {
		IFile target;

		public TextureInspectable(IFile file) {
			this.target = file;
		}

		@Override
		public IFile getTarget() {
			return target;
		}

		@Override
		public InspectableContainer<IFile> createEditContainer(InspectorView parent, IFile target) {
			return new TextureInspectableContainer(parent, target);
		}
	}

	private static class TextureAtlasInspectable implements Inspectable<IFile> {
		IFile target;

		public TextureAtlasInspectable(IFile file) {
			this.target = file;
		}

		@Override
		public IFile getTarget() {
			return target;
		}

		@Override
		public InspectableContainer<IFile> createEditContainer(InspectorView parent, IFile target) {
			return new TextureAtlasInspectableContainer(parent, target);
		}
	}

	private static class MusicInspectable implements Inspectable<IFile> {
		IFile target;

		public MusicInspectable(IFile file) {
			this.target = file;
		}

		@Override
		public IFile getTarget() {
			return target;
		}

		@Override
		public InspectableContainer<IFile> createEditContainer(InspectorView parent, IFile target) {
			return new AudioInspectableContainer(parent, target);
		}
	}
}
