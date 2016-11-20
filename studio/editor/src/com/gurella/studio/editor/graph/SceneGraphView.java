package com.gurella.studio.editor.graph;

import static com.gurella.engine.event.EventService.post;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_COPY;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_CUT;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_PASTE;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.swt.IFocusService;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.NodeContainer;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneElement2;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.inspector.Inspectable;
import com.gurella.studio.editor.inspector.component.ComponentInspectable;
import com.gurella.studio.editor.inspector.node.NodeInspectable;
import com.gurella.studio.editor.subscription.ComponentIndexListener;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.subscription.EditorSelectionListener;
import com.gurella.studio.editor.subscription.NodeIndexListener;
import com.gurella.studio.editor.subscription.NodeNameChangeListener;
import com.gurella.studio.editor.subscription.NodeParentListener;
import com.gurella.studio.editor.subscription.SceneLoadedListener;
import com.gurella.studio.editor.utils.ControlExpression;

public class SceneGraphView extends DockableView implements EditorSceneActivityListener, NodeNameChangeListener,
		SceneLoadedListener, ComponentIndexListener, NodeIndexListener, NodeParentListener {
	private static final Image image = GurellaStudioPlugin.getImage("icons/outline_co.png");

	final Tree graph;
	final Clipboard clipboard;
	private final SceneGraphPopupMenu menu;

	Scene scene;

	public SceneGraphView(SceneEditor editor, int style) {
		super(editor, "Scene", image, style);

		setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		clipboard = new Clipboard(getDisplay());
		addDisposeListener(e -> clipboard.dispose());

		graph = toolkit.createTree(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		graph.setHeaderVisible(false);
		graph.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		graph.addListener(SWT.Selection, e -> selectionChanged());
		graph.addListener(SWT.MouseUp, this::showMenu);

		menu = new SceneGraphPopupMenu(this);

		initDragManagers();
		initFocusHandlers();

		//TODO handle with plugin
		Optional.ofNullable(editorContext.getScene()).ifPresent(s -> sceneLoaded(scene));
		addDisposeListener(e -> EventService.unsubscribe(editor.id, this));
		EventService.subscribe(editor.id, this);
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
		Optional.of(event).filter(e -> e.button == 3).ifPresent(e -> menu.show());
	}

	private void addNodes(TreeItem parent, NodeContainer nodeContainer) {
		Arrays.stream(nodeContainer.getNodes().<SceneNode2> toArray(SceneNode2.class)).forEach(n -> addNode(parent, n));
	}

	private TreeItem addNode(TreeItem parent, SceneNode2 node) {
		TreeItem nodeItem = parent == null ? new TreeItem(graph, SWT.NONE) : new TreeItem(parent, SWT.NONE);
		nodeItem.setText(node.getName());
		nodeItem.setImage(GurellaStudioPlugin.getImage("icons/ice_cube.png"));
		nodeItem.setData(node);
		addComponents(nodeItem, node);
		addNodes(nodeItem, node);
		return nodeItem;
	}

	private static void addComponents(TreeItem parent, SceneNode2 node) {
		Arrays.stream(node.components.<SceneNodeComponent2> toArray(SceneNodeComponent2.class))
				.forEach(c -> createComponentItem(parent, c));
	}

	private static void createComponentItem(TreeItem parentItem, SceneNodeComponent2 component) {
		int index = countComponentItems(parentItem);
		TreeItem componentItem = new TreeItem(parentItem, SWT.NONE, index);
		componentItem.setText(Models.getModel(component).getName());
		componentItem.setData(component);
		//TODO create image to component type registry
		if (component instanceof TransformComponent) {
			componentItem.setImage(GurellaStudioPlugin.getImage("icons/transform.png"));
		} else {
			componentItem.setImage(GurellaStudioPlugin.getImage("icons/16-cube-green_16x16.png"));
		}
	}

	private static int countComponentItems(TreeItem item) {
		return (int) Arrays.stream(item.getItems()).filter(i -> i.getData() instanceof SceneNodeComponent2).count();
	}

	private TreeItem findItem(SceneElement2 element) {
		return Arrays.stream(graph.getItems()).map(i -> findItem(i, element)).filter(i -> i != null).findFirst()
				.orElse(null);
	}

	private TreeItem findItem(TreeItem item, SceneElement2 element) {
		if (item.getData() == element) {
			return item;
		}

		return Arrays.stream(item.getItems()).map(i -> findItem(i, element)).filter(i -> i != null).findFirst()
				.orElse(null);
	}

	Optional<SceneElement2> getFirstSelectedElement() {
		return Optional.ofNullable(graph.getSelection()).filter(s -> s.length > 0)
				.map(s -> (SceneElement2) s[0].getData());
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
		TreeItem parentItem = parentNode == null ? null : findItem(parentNode);
		TreeItem nodeItem = addNode(parentItem, node);
		Optional.ofNullable(parentItem).ifPresent(i -> i.setExpanded(true));
		graph.select(nodeItem);
		NodeInspectable inspectable = new NodeInspectable(node);
		EventService.post(editorId, EditorSelectionListener.class, l -> l.selectionChanged(inspectable));
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		Optional.ofNullable(findItem(node)).ifPresent(i -> i.dispose());
	}

	@Override
	public void componentAdded(SceneNode2 node, SceneNodeComponent2 component) {
		Optional.ofNullable(findItem(node)).ifPresent(i -> createComponentItem(i, component));
	}

	@Override
	public void componentRemoved(SceneNode2 node, SceneNodeComponent2 component) {
		Optional.ofNullable(findItem(component)).ifPresent(i -> i.dispose());
	}

	@Override
	public void nodeNameChanged(SceneNode2 node) {
		Optional.ofNullable(findItem(node)).ifPresent(i -> i.setText(node.getName()));
	}

	@Override
	public void componentIndexChanged(SceneNodeComponent2 component, int newIndex) {
		TreeItem item = findItem(component);
		if (item == null) {
			return;
		}

		TreeItem parent = item.getParentItem();
		String text = item.getText();
		Image image = item.getImage();
		item.dispose();

		TreeItem newItem = new TreeItem(parent, SWT.NONE, newIndex);
		newItem.setImage(image);
		newItem.setText(text);
		newItem.setData(component);
	}

	@Override
	public void nodeIndexChanged(SceneNode2 node, int newIndex) {
		TreeItem item = findItem(node);
		if (item == null) {
			return;
		}

		SceneNode2 parentNode = node.getParentNode();
		TreeItem parent = parentNode == null ? null : findItem(parentNode);
		int index = newIndex + (parent == null ? 0 : countComponentItems(parent));
		String text = item.getText();
		Image image = item.getImage();
		item.dispose();
		TreeItem newItem = parent == null ? new TreeItem(graph, SWT.NONE, index)
				: new TreeItem(parent, SWT.NONE, index);
		newItem.setImage(image);
		newItem.setText(text);
		newItem.setData(node);
		addComponents(newItem, node);
	}

	@Override
	public void nodeParentChanged(SceneNode2 node, SceneNode2 newParent) {
		TreeItem item = findItem(node);
		TreeItem parent = findItem(newParent);
		String text = item.getText();
		Image image = item.getImage();
		item.dispose();
		TreeItem newItem = parent == null ? new TreeItem(graph, SWT.NONE) : new TreeItem(parent, SWT.NONE);
		newItem.setImage(image);
		newItem.setText(text);
		newItem.setData(node);
		addComponents(newItem, node);
	}

	@Override
	public void sceneLoaded(Scene scene) {
		this.scene = scene;
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);
		addNodes(null, scene);
	}
}
