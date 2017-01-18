package com.gurella.studio.editor.graph;

import static com.gurella.engine.event.EventService.post;
import static com.gurella.engine.utils.Values.cast;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_COPY;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_CUT;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_PASTE;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.swt.IFocusService;

import com.gurella.engine.event.EventService;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneConsumer;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.history.HistoryContributor;
import com.gurella.studio.editor.history.HistoryService;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.component.ComponentInspectable;
import com.gurella.studio.editor.inspector.node.NodeInspectable;
import com.gurella.studio.editor.operation.AddComponentOperation;
import com.gurella.studio.editor.operation.AddNodeOperation;
import com.gurella.studio.editor.operation.RemoveComponentOperation;
import com.gurella.studio.editor.operation.RemoveNodeOperation;
import com.gurella.studio.editor.operation.RenameNodeOperation;
import com.gurella.studio.editor.operation.ReparentComponentOperation;
import com.gurella.studio.editor.operation.ReparentNodeOperation;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.subscription.NodeNameChangeListener;
import com.gurella.studio.editor.utils.ControlExpression;
import com.gurella.studio.editor.utils.DelegatingDragSourceListener;
import com.gurella.studio.editor.utils.DelegatingDropTargetListener;
import com.gurella.studio.editor.utils.UiUtils;

public class SceneGraphView extends DockableView
		implements EditorSceneActivityListener, NodeNameChangeListener, SceneConsumer, HistoryContributor {
	private Label searchImageLabel;
	private Text filterText;
	private Label menuLabel;
	private Tree graph;
	private TreeViewer viewer;
	private GraphViewerFilter filter;
	private GraphMenu menu;

	Clipboard clipboard;

	Scene scene;
	HistoryService historyService;

	private SceneElement lastSelection;

	public SceneGraphView(SceneEditorContext context) {
		super(context);
	}

	@Override
	protected void initControl(Composite control) {
		control.setLayout(new GridLayout(3, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(control);

		clipboard = new Clipboard(control.getDisplay());
		control.addDisposeListener(e -> clipboard.dispose());
		menu = new GraphMenu(this);

		searchImageLabel = UiUtils.createLabel(control, "");
		searchImageLabel.setImage(GurellaStudioPlugin.getImage("icons/search-16.png"));
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(searchImageLabel);

		filterText = UiUtils.createText(control);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).minSize(180, 18)
				.applyTo(filterText);
		filterText.setMessage("Name");
		filterText.addModifyListener(e -> filterChanged());

		menuLabel = toolkit.createLabel(control, "");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(menuLabel);
		menuLabel.setImage(GurellaStudioPlugin.getImage("icons/menu.png"));
		menuLabel.addListener(SWT.MouseUp, e -> menu.show(null));

		graph = toolkit.createTree(control, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(3, 1).applyTo(graph);
		graph.setHeaderVisible(false);
		graph.addListener(SWT.KeyDown, e -> onKeyDown());
		graph.addListener(SWT.KeyUp, e -> onKeyUp());
		graph.addListener(SWT.MouseUp, this::onMouseUp);
		graph.addListener(SWT.MouseDoubleClick, this::flipExpansion);

		viewer = new TreeViewer(graph);
		viewer.setContentProvider(new GraphViewerContentProvider());
		viewer.setLabelProvider(new GraphViewerLabelProvider());
		viewer.setComparator(new GraphViewerComparator());
		viewer.setComparer(IdentityElementComparer.instance);
		viewer.setUseHashlookup(true);
		filter = new GraphViewerFilter();

		initDragManagers();
		initFocusHandlers();

		UiUtils.paintBordersFor(control);

		graph.addDisposeListener(e -> onDispose(editorId));
		EventService.subscribe(editorId, this);
		Workbench.activate(editorId, this);
	}

	@Override
	protected String getTitle() {
		return "Scene";
	}

	@Override
	protected Image getImage() {
		return GurellaStudioPlugin.getImage("icons/outline_co.png");
	}

	private void filterChanged() {
		viewer.removeFilter(filter);
		String text = filterText.getText();
		Optional.ofNullable(text).filter(Values::isNotBlank)
				.ifPresent(((Consumer<String>) t -> filter.setFilter(t.trim())).andThen(t -> viewer.addFilter(filter)));
	}

	private void initDragManagers() {
		LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();

		final DragSource source = new DragSource(graph, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(new Transfer[] { localTransfer });
		source.addDragListener(new DelegatingDragSourceListener(new ComponentDragSourceListener(this),
				new NodeDragSourceListener(this)));

		final DropTarget dropTarget = new DropTarget(graph, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { localTransfer });

		DelegatingDropTargetListener listener = new DelegatingDropTargetListener(createComponentDropTargetListener(),
				createNodeDropTargetListener(), createAssetDropTargetListener());
		dropTarget.addDropListener(listener);
	}

	private ComponentDropTargetListener createComponentDropTargetListener() {
		ComponentDropTargetListener listener = new ComponentDropTargetListener(graph, editorId);
		graph.addDisposeListener(e -> Workbench.deactivate(editorId, listener));
		Workbench.activate(editorId, listener);
		return listener;
	}

	private NodeDropTargetListener createNodeDropTargetListener() {
		NodeDropTargetListener listener = new NodeDropTargetListener(graph, editorId);
		graph.addDisposeListener(e -> Workbench.deactivate(editorId, listener));
		Workbench.activate(editorId, listener);
		return listener;
	}

	private AssetDropTargetListener createAssetDropTargetListener() {
		AssetDropTargetListener listener = new AssetDropTargetListener(editorId);
		graph.addDisposeListener(e -> Workbench.deactivate(editorId, listener));
		Workbench.activate(editorId, listener);
		return listener;
	}

	private void initFocusHandlers() {
		IWorkbench workbench = context.editorSite.getWorkbenchWindow().getWorkbench();
		IFocusService focusService = workbench.getService(IFocusService.class);
		focusService.addFocusTracker(graph, "com.gurella.studio.editor.graph.SceneGraphView.graph");
		IHandlerService handlerService = workbench.getService(IHandlerService.class);
		ControlExpression exp = new ControlExpression(graph);
		IHandlerActivation cut = handlerService.activateHandler(EDIT_CUT, new CutElementHandler(this), exp);
		IHandlerActivation copy = handlerService.activateHandler(EDIT_COPY, new CopyElementHandler(this), exp);
		IHandlerActivation paste = handlerService.activateHandler(EDIT_PASTE, new PasteElementHandler(this), exp);
		IHandlerActivation delete = handlerService.activateHandler(EDIT_DELETE, new DeleteElementHandler(this), exp);
		graph.addDisposeListener(e -> deactivateFocusHandlers(cut, copy, paste, delete));
	}

	private void deactivateFocusHandlers(IHandlerActivation... handlers) {
		IWorkbench workbench = context.editorSite.getWorkbenchWindow().getWorkbench();
		IFocusService focusService = workbench.getService(IFocusService.class);
		IHandlerService handlerService = workbench.getService(IHandlerService.class);
		Arrays.stream(handlers).forEach(h -> handlerService.deactivateHandler(h));
		focusService.removeFocusTracker(graph);
	}

	private void onKeyDown() {
		lastSelection = getSelectedElement().orElse(null);
	}

	private void onKeyUp() {
		getSelectedElement().filter(s -> s != lastSelection).ifPresent(s -> presentInspectable());
	}

	private void onMouseUp(Event event) {
		Optional.of(event).filter(e -> e.button == 1).flatMap(e -> getSelectedElement())
				.ifPresent(f -> presentInspectable());
		Optional.of(event).filter(e -> e.button == 3).ifPresent(e -> menu.show(getElementAt(event.x, event.y)));
	}

	private void presentInspectable() {
		getSelectedElement().map(SceneGraphView::toInspectable)
				.ifPresent(s -> post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(s)));
	}

	private static Inspectable<? extends SceneElement> toInspectable(SceneElement element) {
		return element instanceof SceneNode ? new NodeInspectable((SceneNode) element)
				: new ComponentInspectable((SceneNodeComponent) element);
	}

	private void flipExpansion(Event event) {
		Optional.of(event).filter(e -> e.button == 1).map(e -> getElementAt(e.x, e.y))
				.filter(SceneNode.class::isInstance).map(SceneNode.class::cast)
				.ifPresent(n -> viewer.setExpandedState(n, !viewer.getExpandedState(n)));
	}

	private SceneElement getElementAt(int x, int y) {
		return Optional.ofNullable(viewer.getCell(new Point(x, y))).map(c -> c.getElement())
				.filter(SceneNode.class::isInstance).map(c -> (SceneElement) c).orElse(null);
	}

	Optional<SceneElement> getSelectedElement() {
		return Optional.ofNullable(((ITreeSelection) viewer.getSelection()).getFirstElement())
				.map(SceneElement.class::cast);
	}

	Optional<SceneNode> getSelectedNode() {
		return getSelectedElement().filter(SceneNode.class::isInstance).map(SceneNode.class::cast);
	}

	Optional<SceneNodeComponent> getSelectedComponent() {
		return getSelectedElement().filter(SceneNodeComponent.class::isInstance).map(SceneNodeComponent.class::cast);
	}

	private void onDispose(int editorId) {
		EventService.unsubscribe(editorId, this);
		Workbench.deactivate(editorId, this);
	}

	@Override
	public void nodeAdded(Scene scene, SceneNode parentNode, SceneNode node) {
		if (parentNode == null) {
			viewer.add(scene, node);
		} else {
			viewer.add(parentNode, node);
			viewer.setExpandedState(parentNode, true);
		}
		viewer.setSelection(new StructuredSelection(node), true);
		NodeInspectable inspectable = new NodeInspectable(node);
		EventService.post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(inspectable));
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode parentNode, SceneNode node) {
		viewer.remove(node);
	}

	@Override
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
		viewer.add(node, component);
		viewer.setExpandedState(component, true);
		viewer.setSelection(new StructuredSelection(component), true);
	}

	@Override
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
		viewer.remove(component);
	}

	@Override
	public void nodeNameChanged(SceneNode node) {
		viewer.update(node, null);
	}

	@Override
	public void componentIndexChanged(SceneNodeComponent component, int newIndex) {
		viewer.refresh(component.getNode(), false);
	}

	@Override
	public void nodeIndexChanged(SceneNode node, int newIndex) {
		SceneNode parentNode = node.getParentNode();
		if (parentNode == null) {
			viewer.refresh(false);
		} else {
			viewer.refresh(parentNode, false);
		}
	}

	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
		viewer.setInput(scene);
	}

	@Override
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	void cut() {
		getSelectedElement().ifPresent(e -> cut(e));
	}

	void cut(SceneElement element) {
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		ISelection selection = new CutElementSelection(element);
		transfer.setSelection(selection);
		clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
	}

	void copy() {
		getSelectedElement().ifPresent(e -> copy(e));
	}

	void copy(SceneElement element) {
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		CopyElementSelection selection = new CopyElementSelection(element);
		transfer.setSelection(selection);
		clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
	}

	void paste() {
		getSelectedNode().ifPresent(e -> paste(e));
	}

	void paste(SceneNode destination) {
		Object contents = clipboard.getContents(LocalSelectionTransfer.getTransfer());
		if (contents instanceof CutElementSelection) {
			move(((CutElementSelection) contents).getElement(), destination);
		} else if (contents instanceof CopyElementSelection) {
			duplicate(((CopyElementSelection) contents).getElement(), destination);
		}
	}

	private void move(SceneElement source, SceneNode destination) {
		if (source == destination) {
			return;
		}

		if (source instanceof SceneNodeComponent) {
			SceneNodeComponent component = (SceneNodeComponent) source;
			if (destination.hasComponent(component.getClass(), true)) {
				return;
			}

			String errorMsg = "Error while repositioning component";
			int newIndex = destination.components.size();
			IUndoableOperation operation = new ReparentComponentOperation(editorId, component, destination, newIndex);
			historyService.executeOperation(operation, errorMsg);
		} else {
			if (source.getParent() == destination) {
				return;
			}

			SceneNode node = (SceneNode) source;
			String errorMsg = "Error while repositioning node";
			int newIndex = destination.childNodes.size();
			historyService.executeOperation(new ReparentNodeOperation(editorId, node, destination, newIndex), errorMsg);
		}

		clipboard.clearContents();
	}

	private void duplicate(SceneElement source, SceneNode destination) {
		if (source instanceof SceneNodeComponent && destination.getComponent(cast(source.getClass()), true) != null) {
			return;
		}

		SceneElement copy = CopyContext.copyObject(source);
		if (copy instanceof SceneNode) {
			SceneNode node = (SceneNode) copy;
			AddNodeOperation operation = new AddNodeOperation(editorId, scene, destination, node);
			historyService.executeOperation(operation, "Error while adding component");
		} else {
			SceneNodeComponent component = (SceneNodeComponent) copy;
			AddComponentOperation operation = new AddComponentOperation(editorId, destination, component);
			historyService.executeOperation(operation, "Error while adding component");
		}
	}

	void duplicate(SceneNode source) {
		duplicate(source, source.getParentNode());
	}

	void delete() {
		getSelectedElement().ifPresent(e -> delete(e));
	}

	void delete(SceneElement element) {
		if (element instanceof SceneNode) {
			SceneNode node = (SceneNode) element;
			SceneNode parentNode = node.getParentNode();
			RemoveNodeOperation operation = new RemoveNodeOperation(editorId, scene, parentNode, node);
			historyService.executeOperation(operation, "Error while removing node");
		} else if (element instanceof SceneNodeComponent) {
			SceneNodeComponent component = (SceneNodeComponent) element;
			SceneNode node = component.getNode();
			RemoveComponentOperation operation = new RemoveComponentOperation(editorId, node, component);
			historyService.executeOperation(operation, "Error while removing component");
		}
	}

	void rename() {
		getSelectedNode().ifPresent(n -> rename(n));
	}

	void rename(SceneNode node) {
		IInputValidator v = i -> i.length() < 3 ? "Too short" : null;
		InputDialog dlg = new InputDialog(getContent().getShell(), "Add Node", "Enter node name", node.getName(), v);
		if (dlg.open() == Window.OK) {
			RenameNodeOperation operation = new RenameNodeOperation(editorId, node, dlg.getValue());
			historyService.executeOperation(operation, "Error while renaming node");
		}
	}
}
