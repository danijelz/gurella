package com.gurella.studio.editor.assets;

import static com.gurella.engine.event.EventService.post;
import static com.gurella.studio.GurellaStudioPlugin.getImage;
import static com.gurella.studio.GurellaStudioPlugin.log;
import static org.eclipse.ltk.core.refactoring.CheckConditionsOperation.ALL_CONDITIONS;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourcesDescriptor;
import org.eclipse.ltk.core.refactoring.resource.MoveResourcesDescriptor;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ResourceTransfer;

import com.gurella.engine.asset.AssetType;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.common.ErrorComposite;
import com.gurella.studio.editor.control.DockableView;
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
import com.gurella.studio.editor.operation.RefractoringOperation;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.utils.DelegatingDropTargetListener;
import com.gurella.studio.editor.utils.Try;

public class AssetsView extends DockableView implements IResourceChangeListener {
	private final Tree tree;
	private final TreeViewer viewer;
	private final AssetsViewMenu menu;
	final Clipboard clipboard;

	IResource rootResource;

	private Object lastSelection;

	public AssetsView(SceneEditor editor, int style) {
		super(editor, "Assets", getImage("icons/resource_persp.gif"), style);

		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		rootResource = editorContext.project.getFolder("assets");

		clipboard = new Clipboard(getDisplay());
		addDisposeListener(e -> clipboard.dispose());
		menu = new AssetsViewMenu(this);

		tree = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.addListener(SWT.KeyDown, e -> onKeyDown());
		tree.addListener(SWT.KeyUp, e -> onKeyUp());
		tree.addListener(SWT.MouseUp, e -> presentInspectable());
		tree.addListener(SWT.MouseUp, this::showMenu);
		tree.addListener(SWT.MouseDoubleClick, this::flipExpansion);

		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new AssetsViewerContentProvider());
		viewer.setLabelProvider(new AssetsViewerLabelProvider());
		viewer.setComparator(new AssetsViewerComparator());
		viewer.setUseHashlookup(true);

		initDragManagers();

		IWorkspace workspace = editorContext.workspace;
		workspace.addResourceChangeListener(this);
		addDisposeListener(e -> workspace.removeResourceChangeListener(this));

		viewer.setInput(rootResource);
	}

	private void initDragManagers() {
		LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

		final DragSource source = new DragSource(tree, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { ResourceTransfer.getInstance(), localTransfer });
		source.addDragListener(new ResourceDragSourceListener(this));

		final DropTarget dropTarget = new DropTarget(tree, DND.DROP_DEFAULT | DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { localTransfer });
		dropTarget.addDropListener(new DelegatingDropTargetListener(new MoveAssetDropTargetListener(this),
				new ConvertToPrefabDropTargetListener(editorContext)));
	}

	protected void presentInitException(Throwable e) {
		tree.dispose();
		String message = "Error creating assets tree";
		IStatus status = GurellaStudioPlugin.log(e, message);
		ErrorComposite errorComposite = new ErrorComposite(this, status, message);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta mainDelta = event.getDelta();
		if (mainDelta == null) {
			return;
		}

		IResourceDelta assetsDelta = mainDelta.findMember(rootResource.getFullPath());
		if (assetsDelta == null) {
			return;
		}

		getDisplay().asyncExec(() -> resourceChanged(assetsDelta));
	}

	private void resourceChanged(IResourceDelta delta) {
		IResource resource = delta.getResource();
		Arrays.stream(delta.getAffectedChildren()).sequential()
				.forEach(((Consumer<IResourceDelta>) d -> updateResource(resource, d)).andThen(this::resourceChanged));
	}

	private void updateResource(IResource parent, IResourceDelta childDelta) {
		switch (childDelta.getKind()) {
		case IResourceDelta.ADDED:
			viewer.add(parent, childDelta.getResource());
			break;
		case IResourceDelta.REMOVED:
			viewer.remove(childDelta.getResource());
			break;
		case IResourceDelta.CHANGED:
			viewer.update(childDelta.getResource(), null);
			break;
		default:
			break;
		}
	}

	private IResource getResourceAt(int x, int y) {
		return Optional.ofNullable(viewer.getCell(new Point(x, y))).map(c -> c.getElement())
				.filter(IResource.class::isInstance).map(e -> (IResource) e).orElse(null);
	}

	Optional<IResource> getFirstSelectedResource() {
		return Optional.ofNullable(((ITreeSelection) viewer.getSelection()).getFirstElement())
				.map(IResource.class::cast);
	}

	Optional<IFile> getFirstSelectedFile() {
		return getFirstSelectedResource().filter(IFile.class::isInstance).map(IFile.class::cast);
	}

	private void onKeyDown() {
		lastSelection = getFirstSelectedResource().orElse(null);
	}

	private void onKeyUp() {
		getFirstSelectedResource().filter(s -> s != lastSelection).ifPresent(s -> presentInspectable());
	}

	private void presentInspectable() {
		int editorId = editorContext.editorId;
		getFirstSelectedFile().ifPresent(
				f -> post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(getInspectable(f))));
	}

	private void showMenu(Event event) {
		Optional.of(event).filter(e -> e.button == 3).ifPresent(e -> menu.show(getResourceAt(event.x, event.y)));
	}

	private void flipExpansion(Event event) {
		Optional.of(event).filter(e -> e.button == 1).map(e -> getResourceAt(e.x, e.y))
				.filter(SceneNode2.class::isInstance).map(SceneNode2.class::cast)
				.ifPresent(n -> viewer.setExpandedState(n, !viewer.getExpandedState(n)));
	}

	// TODO create plugin extension
	private static Inspectable<?> getInspectable(IFile file) {
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
		return null;
	}

	void cut() {
		getFirstSelectedResource().ifPresent(r -> cut(r));
	}

	void cut(IResource resource) {
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		ISelection selection = new CutAssetSelection(resource);
		transfer.setSelection(selection);
		clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
	}

	void copy() {
		getFirstSelectedResource().ifPresent(r -> copy(r));
	}

	void copy(IResource resource) {
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		CopyAssetSelection selection = new CopyAssetSelection(resource);
		transfer.setSelection(selection);
		clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
	}

	void paste() {
		getFirstSelectedResource().ifPresent(r -> paste(r));
	}

	void paste(IResource destination) {
		if (!(destination instanceof IFolder)) {
			return;
		}

		IFolder destinationFolder = (IFolder) destination;
		Object contents = clipboard.getContents(LocalSelectionTransfer.getTransfer());
		if (contents instanceof CutAssetSelection) {
			move(((CutAssetSelection) contents).getAssetResource(), destinationFolder);
		} else if (contents instanceof CopyAssetSelection) {
			duplicate(((CopyAssetSelection) contents).getAssetResource(), destinationFolder);
		}
	}

	void move(IResource resource, IFolder destination) {
		MoveResourcesDescriptor descriptor = new MoveResourcesDescriptor();
		descriptor.setResourcesToMove(new IResource[] { resource });
		descriptor.setDestination(destination);
		descriptor.setUpdateReferences(true);
		executeRefractoringOperation(descriptor, "Error while moving resource.");
	}

	private void duplicate(IResource resource, IFolder destinationFolder) {
		// TODO Auto-generated method stub
	}

	void rename(IResource resource, String newName) {
		RenameResourceDescriptor descriptor = new RenameResourceDescriptor();
		descriptor.setResourcePath(resource.getLocation());
		descriptor.setUpdateReferences(true);
		descriptor.setNewName(newName);
		executeRefractoringOperation(descriptor, "Error while renaming resource.");
	}

	private void executeRefractoringOperation(RefactoringDescriptor descriptor, String errMsg) {
		RefactoringStatus status = new RefactoringStatus();
		Try.successful(descriptor).map(d -> d.createRefactoring(status))
				.map(r -> new RefractoringOperation(editorContext, new PerformRefactoringOperation(r, ALL_CONDITIONS)))
				.onSuccess(o -> editorContext.executeOperation(o, errMsg)).onFailure(e -> log(e, errMsg));
	}

	void delete(IResource resource) {
		DeleteResourcesDescriptor descriptor = new DeleteResourcesDescriptor();
		descriptor.setResources(new IResource[] { resource });
		descriptor.setDeleteContents(true);
		executeRefractoringOperation(descriptor, "Error while deleting resource.");
	}
}
