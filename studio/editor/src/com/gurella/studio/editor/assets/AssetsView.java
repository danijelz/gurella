package com.gurella.studio.editor.assets;

import static com.gurella.engine.event.EventService.post;
import static com.gurella.studio.GurellaStudioPlugin.log;
import static org.eclipse.ltk.core.refactoring.CheckConditionsOperation.ALL_CONDITIONS;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_COPY;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_CUT;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_PASTE;

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
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.swt.IFocusService;

import com.gurella.engine.asset.descriptor.DefaultAssetDescriptors;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.SceneNode;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.common.AssetsFolderLocator;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.assetproperties.AssetPropertiesInspectable;
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
import com.gurella.studio.editor.preferences.PreferencesExtension;
import com.gurella.studio.editor.preferences.PreferencesStore;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.ui.ErrorComposite;
import com.gurella.studio.editor.utils.ControlExpression;
import com.gurella.studio.editor.utils.DelegatingDropTargetListener;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.refractoring.CopyAssetsDescriptor;

public class AssetsView extends DockableView
		implements IResourceChangeListener, PreferencesExtension, HistoryContributor {
	private Tree tree;
	private TreeViewer viewer;
	private AssetsMenu menu;
	Clipboard clipboard;

	IFolder rootAssetsFolder;
	PreferencesStore preferencesStore;
	HistoryService historyService;

	private Object lastSelection;

	public AssetsView(SceneEditorContext context) {
		super(context);
	}

	@Override
	protected void initControl(Composite control) {
		control.setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(control);

		rootAssetsFolder = AssetsFolderLocator.getAssetsFolder(context.project);

		clipboard = new Clipboard(control.getDisplay());
		control.addDisposeListener(e -> clipboard.dispose());
		menu = new AssetsMenu(this);

		tree = toolkit.createTree(control, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(false);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.addListener(SWT.KeyDown, e -> onKeyDown());
		tree.addListener(SWT.KeyUp, e -> onKeyUp());
		tree.addListener(SWT.MouseUp, this::onMouseUp);
		tree.addListener(SWT.MouseDoubleClick, this::flipExpansion);

		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new AssetsViewerContentProvider());
		viewer.setLabelProvider(new AssetsViewerLabelProvider());
		viewer.setComparator(new AssetsViewerComparator());
		viewer.setUseHashlookup(true);

		initDragManagers();
		initFocusHandlers();

		IWorkspace workspace = context.workspace;
		workspace.addResourceChangeListener(this);
		control.addDisposeListener(e -> workspace.removeResourceChangeListener(this));

		viewer.setInput(rootAssetsFolder);

		control.addDisposeListener(e -> Workbench.deactivate(editorId, this));
		Workbench.activate(editorId, this);
	}

	@Override
	protected String getTitle() {
		return "Assets";
	}

	@Override
	protected Image getImage() {
		return GurellaStudioPlugin.getImage("icons/resource_persp.gif");
	}

	private void initDragManagers() {
		LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

		final DragSource source = new DragSource(tree, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { ResourceTransfer.getInstance(), localTransfer });
		source.addDragListener(new ResourceDragSourceListener(this));

		final DropTarget dropTarget = new DropTarget(tree, DND.DROP_DEFAULT | DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { localTransfer });
		dropTarget.addDropListener(new DelegatingDropTargetListener(new MoveAssetDropTargetListener(this),
				createConvertToPrefabDropTargetListener()));
	}

	private ConvertToPrefabDropTargetListener createConvertToPrefabDropTargetListener() {
		ConvertToPrefabDropTargetListener listener = new ConvertToPrefabDropTargetListener(context);
		tree.addDisposeListener(e -> Workbench.deactivate(editorId, listener));
		Workbench.activate(editorId, listener);
		return listener;
	}

	private void initFocusHandlers() {
		IWorkbench workbench = context.editorSite.getWorkbenchWindow().getWorkbench();
		IFocusService focusService = workbench.getService(IFocusService.class);
		focusService.addFocusTracker(tree, "com.gurella.studio.editor.graph.SceneGraphView.graph");
		IHandlerService handlerService = workbench.getService(IHandlerService.class);
		ControlExpression exp = new ControlExpression(tree);
		IHandlerActivation cut = handlerService.activateHandler(EDIT_CUT, new CutAssetHandler(this), exp);
		IHandlerActivation copy = handlerService.activateHandler(EDIT_COPY, new CopyAssetHandler(this), exp);
		IHandlerActivation paste = handlerService.activateHandler(EDIT_PASTE, new PasteAssetHandler(this), exp);
		IHandlerActivation delete = handlerService.activateHandler(EDIT_DELETE, new DeleteAssetHandler(this), exp);
		tree.addDisposeListener(e -> deactivateFocusHandlers(cut, copy, paste, delete));
	}

	private void deactivateFocusHandlers(IHandlerActivation... handlers) {
		IWorkbench workbench = context.editorSite.getWorkbenchWindow().getWorkbench();
		IFocusService focusService = workbench.getService(IFocusService.class);
		IHandlerService handlerService = workbench.getService(IHandlerService.class);
		Arrays.stream(handlers).forEach(h -> handlerService.deactivateHandler(h));
		focusService.removeFocusTracker(tree);
	}

	protected void presentInitException(Throwable e) {
		tree.dispose();
		String message = "Error creating assets tree";
		IStatus status = GurellaStudioPlugin.log(e, message);
		ErrorComposite errorComposite = new ErrorComposite(getContent(), status, message);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta mainDelta = event.getDelta();
		if (mainDelta == null) {
			return;
		}

		IResourceDelta assetsDelta = mainDelta.findMember(rootAssetsFolder.getFullPath());
		if (assetsDelta == null) {
			return;
		}

		getContent().getDisplay().asyncExec(() -> resourceChanged(assetsDelta));
	}

	private void resourceChanged(IResourceDelta delta) {
		IResource resource = delta.getResource();
		Arrays.stream(delta.getAffectedChildren()).forEachOrdered(
				((Consumer<IResourceDelta>) d -> updateResource(resource, d)).andThen(this::resourceChanged));
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

	Optional<IResource> getSelectedResource() {
		return Optional.ofNullable(((ITreeSelection) viewer.getSelection()).getFirstElement())
				.map(IResource.class::cast);
	}

	Optional<IFile> getSelectedFile() {
		return getSelectedResource().filter(IFile.class::isInstance).map(IFile.class::cast);
	}

	private void onKeyDown() {
		lastSelection = getSelectedResource().orElse(null);
	}

	private void onKeyUp() {
		getSelectedResource().filter(s -> s != lastSelection).ifPresent(s -> presentInspectable());
	}

	private void onMouseUp(Event event) {
		Optional.of(event).filter(e -> e.button == 1).flatMap(e -> getSelectedFile()).ifPresent(
				f -> post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(getInspectable(f))));
		Optional.of(event).filter(e -> e.button == 3).ifPresent(e -> menu.show(getResourceAt(event.x, event.y)));
	}

	private void presentInspectable() {
		int editorId = context.editorId;
		getSelectedFile().ifPresent(
				f -> post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(getInspectable(f))));
	}

	private void flipExpansion(Event event) {
		Optional.of(event).filter(e -> e.button == 1).map(e -> getResourceAt(e.x, e.y))
				.filter(SceneNode.class::isInstance).map(SceneNode.class::cast)
				.ifPresent(n -> viewer.setExpandedState(n, !viewer.getExpandedState(n)));
	}

	// TODO create plugin extension
	private static Inspectable<?> getInspectable(IFile file) {
		String extension = file.getFileExtension();
		if (DefaultAssetDescriptors.texture.isValidExtension(extension)) {
			return new TextureInspectable(file);
		} else if (DefaultAssetDescriptors.pixmap.isValidExtension(extension)) {
			return new PixmapInspectable(file);
		} else if (DefaultAssetDescriptors.sound.isValidExtension(extension)) {
			return new AudioInspectable(file);
		} else if (DefaultAssetDescriptors.textureAtlas.isValidExtension(extension)) {
			return new TextureAtlasInspectable(file);
		} else if (DefaultAssetDescriptors.bitmapFont.isValidExtension(extension)) {
			return new BitmapFontInspectable(file);
		} else if (DefaultAssetDescriptors.model.isValidExtension(extension)) {
			return new ModelInspectable(file);
		} else if (DefaultAssetDescriptors.polygonRegion.isValidExtension(extension)) {
			return new PolygonRegionInspectable(file);
		} else if (DefaultAssetDescriptors.prefab.isValidExtension(extension)) {
			return new PrefabInspectable(file);
		} else if (DefaultAssetDescriptors.material.isValidExtension(extension)) {
			return new MaterialInspectable(file);
		} else if (DefaultAssetDescriptors.assetProps.isValidExtension(extension)) {
			return new AssetPropertiesInspectable(file);
		}
		return null;
	}

	void cut() {
		getSelectedResource().ifPresent(r -> cut(r));
	}

	void cut(IResource resource) {
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		ISelection selection = new CutAssetSelection(resource);
		transfer.setSelection(selection);
		clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
	}

	void copy() {
		getSelectedResource().ifPresent(r -> copy(r));
	}

	void copy(IResource resource) {
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		CopyAssetSelection selection = new CopyAssetSelection(resource);
		transfer.setSelection(selection);
		clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
	}

	void paste() {
		getSelectedResource().ifPresent(r -> paste(r));
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
		executeRefractoringOperation(descriptor, "Move asset", "Error while moving resource.");
	}

	private void duplicate(IResource resource, IFolder destination) {
		CopyAssetsDescriptor descriptor = new CopyAssetsDescriptor();
		descriptor.setResourcesToCopy(new IResource[] { resource });
		descriptor.setDestination(destination);
		executeRefractoringOperation(descriptor, "Copy asset", "Error while copying resource.");
	}

	void rename(IResource resource, String newName) {
		RenameResourceDescriptor descriptor = new RenameResourceDescriptor();
		IPath path = resource.getLocation().makeRelativeTo(resource.getWorkspace().getRoot().getLocation());
		descriptor.setResourcePath(path);
		descriptor.setUpdateReferences(true);
		descriptor.setNewName(newName);
		executeRefractoringOperation(descriptor, "Rename asset", "Error while renaming resource.");
	}

	private void executeRefractoringOperation(RefactoringDescriptor descriptor, String label, String errMsg) {
		RefactoringStatus status = new RefactoringStatus();
		Try.successful(descriptor).map(d -> d.createRefactoring(status))
				.map(r -> new RefractoringOperation(context, label, new PerformRefactoringOperation(r, ALL_CONDITIONS)))
				.onSuccess(o -> historyService.executeOperation(o, errMsg)).onFailure(e -> log(e, errMsg));
	}

	void delete() {
		getSelectedResource().ifPresent(e -> delete(e));
	}

	void delete(IResource resource) {
		if (context.sceneFile.equals(resource)) {
			// TODO error?
			return;
		}

		DeleteResourcesDescriptor descriptor = new DeleteResourcesDescriptor();
		descriptor.setResources(new IResource[] { resource });
		descriptor.setDeleteContents(true);
		executeRefractoringOperation(descriptor, "Delete asset", "Error while deleting resource.");
	}

	@Override
	public void setPreferencesStore(PreferencesStore preferencesStore) {
		this.preferencesStore = preferencesStore;
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
}
