package com.gurella.studio.editor.graph;

import static com.gurella.engine.event.EventService.post;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_COPY;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_CUT;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_PASTE;

import java.util.Optional;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
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
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.component.ComponentInspectable;
import com.gurella.studio.editor.inspector.node.NodeInspectable;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.subscription.NodeNameChangeListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.ControlExpression;
import com.gurella.studio.editor.utils.UiUtils;

public class SceneGraphView extends DockableView
		implements EditorSceneActivityListener, NodeNameChangeListener, SceneLoadedListener {
	private static final Image image = GurellaStudioPlugin.getImage("icons/outline_co.png");

	private final Label searchImageLabel;
	private final Text filterText;
	private final Label menuLabel;
	private final Tree graph;
	private final TreeViewer viewer;
	private final GraphMenu menu;

	final Clipboard clipboard;
	Scene scene;

	public SceneGraphView(SceneEditor editor, int style) {
		super(editor, "Scene", image, style);

		setLayout(new GridLayout(3, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		clipboard = new Clipboard(getDisplay());
		addDisposeListener(e -> clipboard.dispose());
		menu = new GraphMenu(this);

		searchImageLabel = UiUtils.createLabel(this, "");
		searchImageLabel.setImage(GurellaStudioPlugin.getImage("icons/search-16.png"));
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(searchImageLabel);

		filterText = UiUtils.createText(this);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).minSize(200, 18)
				.applyTo(filterText);
		filterText.setMessage("Filter");

		menuLabel = toolkit.createLabel(this, "");
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(menuLabel);
		menuLabel.setImage(GurellaStudioPlugin.getImage("icons/menu.png"));
		menuLabel.addListener(SWT.MouseUp, e -> menu.show(null));

		graph = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(3, 1).applyTo(graph);
		graph.setHeaderVisible(false);
		graph.addListener(SWT.Selection, e -> selectionChanged());
		graph.addListener(SWT.MouseUp, this::showMenu);

		viewer = new TreeViewer(graph);
		viewer.setContentProvider(new GraphViewerContentProvider());
		viewer.setLabelProvider(new GraphViewerLabelProvider());
		viewer.setComparator(new GraphViewerComparator());
		viewer.setComparer(IdentityElementComparer.instance);
		viewer.setUseHashlookup(true);

		initDragManagers();
		initFocusHandlers();

		// TODO handle with plugin
		Optional.ofNullable(editorContext.getScene()).ifPresent(s -> sceneLoaded(scene));
		addDisposeListener(e -> EventService.unsubscribe(editor.id, this));
		EventService.subscribe(editor.id, this);
		UiUtils.paintBordersFor(this);
	}

	private void initDragManagers() {
		LocalSelectionTransfer localTransfer = LocalSelectionTransfer.getTransfer();
		final DragSource source = new DragSource(graph, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(new Transfer[] { localTransfer });
		source.addDragListener(new SceneGraphDragSourceListener(graph));

		final DropTarget dropTarget = new DropTarget(graph, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { localTransfer });
		dropTarget.addDropListener(new SceneGraphDropTargetListener(graph, editorContext));
	}

	private void initFocusHandlers() {
		IWorkbench workbench = editorContext.editorSite.getWorkbenchWindow().getWorkbench();
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

	private void deactivateFocusHandlers(IHandlerActivation cut, IHandlerActivation copy, IHandlerActivation paste,
			IHandlerActivation delete) {
		IWorkbench workbench = editorContext.editorSite.getWorkbenchWindow().getWorkbench();
		IFocusService focusService = workbench.getService(IFocusService.class);
		IHandlerService handlerService = workbench.getService(IHandlerService.class);
		handlerService.deactivateHandler(cut);
		handlerService.deactivateHandler(copy);
		handlerService.deactivateHandler(paste);
		handlerService.deactivateHandler(delete);
		focusService.removeFocusTracker(graph);
	}

	private void selectionChanged() {
		getFirstSelectedElement().map(SceneGraphView::toInspectable)
				.ifPresent(s -> post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(s)));
	}

	private static Inspectable<? extends SceneElement2> toInspectable(SceneElement2 element) {
		return element instanceof SceneNode2 ? new NodeInspectable((SceneNode2) element)
				: new ComponentInspectable((SceneNodeComponent2) element);
	}

	private void showMenu(Event event) {
		Optional.of(event).filter(e -> e.button == 3).ifPresent(e -> menu.show(getElementAt(event.x, event.y)));
	}

	private SceneElement2 getElementAt(int x, int y) {
		return Optional.ofNullable(graph.getItem(new Point(x, y))).map(i -> i.getData())
				.filter(d -> d instanceof SceneElement2).map(d -> (SceneElement2) d).orElse(null);
	}

	Optional<SceneElement2> getFirstSelectedElement() {
		return Optional.ofNullable(((ITreeSelection) viewer.getSelection()).getFirstElement())
				.map(e -> (SceneElement2) e);
	}

	Optional<SceneNode2> getFirstSelectedNode() {
		return getFirstSelectedElement().filter(e -> e instanceof SceneNode2).map(e -> (SceneNode2) e);
	}

	Optional<SceneNodeComponent2> getFirstSelectedComponent() {
		return getFirstSelectedElement().filter(e -> e instanceof SceneNodeComponent2)
				.map(e -> (SceneNodeComponent2) e);
	}

	@Override
	public void nodeAdded(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		if (parentNode == null) {
			Object[] expandedElements = viewer.getExpandedElements();
			viewer.setInput(scene.nodes.toArray(SceneNode2.class));
			viewer.setExpandedElements(expandedElements);
		} else {
			viewer.add(parentNode, node);
			viewer.setExpandedState(parentNode, true);
		}
		viewer.setSelection(new StructuredSelection(node), true);
		NodeInspectable inspectable = new NodeInspectable(node);
		EventService.post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(inspectable));
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		viewer.remove(node);
	}

	@Override
	public void componentAdded(SceneNode2 node, SceneNodeComponent2 component) {
		viewer.add(node, component);
		viewer.setExpandedState(component, true);
		viewer.setSelection(new StructuredSelection(component), true);
	}

	@Override
	public void componentRemoved(SceneNode2 node, SceneNodeComponent2 component) {
		viewer.remove(component);
	}

	@Override
	public void nodeNameChanged(SceneNode2 node) {
		viewer.update(node, null);
	}

	@Override
	public void componentIndexChanged(SceneNodeComponent2 component, int newIndex) {
		viewer.refresh(component.getNode(), false);
	}

	@Override
	public void nodeIndexChanged(SceneNode2 node, int newIndex) {
		SceneNode2 parentNode = node.getParentNode();
		if (parentNode == null) {
			viewer.refresh(false);
		} else {
			viewer.refresh(parentNode, false);
		}
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);
		viewer.setInput(scene.nodes.toArray(SceneNode2.class));
	}
}
