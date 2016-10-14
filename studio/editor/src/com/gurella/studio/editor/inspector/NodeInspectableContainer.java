package com.gurella.studio.editor.inspector;

import static com.gurella.studio.editor.model.ModelEditorFactory.createEditor;
import static org.eclipse.jdt.core.search.SearchEngine.createHierarchyScope;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;
import static org.eclipse.swt.SWT.BEGINNING;
import static org.eclipse.swt.SWT.CENTER;
import static org.eclipse.swt.SWT.CHECK;
import static org.eclipse.swt.SWT.END;
import static org.eclipse.swt.SWT.FILL;
import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;
import static org.eclipse.swt.SWT.SEPARATOR;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.SHORT_TITLE_BAR;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Models;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.scene.ComponentType;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.light.SpotLightComponent;
import com.gurella.engine.scene.renderable.AtlasRegionComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.ShapeComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.TextureRegionComponent;
import com.gurella.engine.scene.renderable.skybox.SkyboxComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.test.TestPropertyEditorsComponnent;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.MetaModelEditor;
import com.gurella.studio.editor.model.ModelEditorContext.PropertyValueChangedEvent;
import com.gurella.studio.editor.scene.event.NodeEnabledChangedEvent;
import com.gurella.studio.editor.scene.event.NodeNameChangedEvent;
import com.gurella.studio.editor.scene.event.SceneChangedEvent;
import com.gurella.studio.editor.scene.operation.AddComponentOperation;
import com.gurella.studio.editor.subscription.EditorSceneListener;
import com.gurella.studio.editor.subscription.NodeEnabledChangedListener;
import com.gurella.studio.editor.subscription.NodeNameChangedListener;
import com.gurella.studio.editor.utils.UiUtils;

public class NodeInspectableContainer extends InspectableContainer<SceneNode2>
		implements EditorSceneListener, NodeNameChangedListener, NodeEnabledChangedListener {
	private Text nameText;
	private Listener nameChangedlLstener;

	private Button enabledCheck;
	private Listener nodeEnabledListener;

	private Label menuButton;

	private Composite componentsComposite;
	private Map<SceneNodeComponent2, Section> editors = new LinkedHashMap<>();

	public NodeInspectableContainer(InspectorView parent, SceneNode2 target) {
		super(parent, target);

		addDisposeListener(e -> EventService.unsubscribe(this));
		EventService.subscribe(parent.getSceneEditorContext().editorId, this);

		int editorId = getSceneEditorContext().editorId;
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		Composite body = getBody();
		GridLayoutFactory.fillDefaults().numColumns(4).extendedMargins(0, 10, 4, 0).applyTo(body);

		Label nameLabel = toolkit.createLabel(body, " Name: ");
		nameLabel.setLayoutData(new GridData(BEGINNING, CENTER, false, false));

		nameText = UiUtils.createText(body);
		nameText.setText(target.getName());
		nameText.setLayoutData(new GridData(FILL, BEGINNING, true, false));
		nameChangedlLstener = e -> nodeNameChanged();
		nameText.addListener(SWT.Modify, nameChangedlLstener);

		enabledCheck = toolkit.createButton(body, "Enabled", CHECK);
		enabledCheck.setLayoutData(new GridData(END, CENTER, false, false));
		enabledCheck.setSelection(target.isEnabled());
		nodeEnabledListener = e -> nodeEnabledChanged();
		enabledCheck.addListener(SWT.Selection, nodeEnabledListener);

		menuButton = toolkit.createLabel(body, " ", NONE);
		menuButton.setImage(GurellaStudioPlugin.createImage("icons/menu.png"));
		menuButton.setLayoutData(new GridData(END, CENTER, false, false));
		menuButton.addListener(SWT.MouseUp, e -> showMenu());

		componentsComposite = toolkit.createComposite(body);
		GridLayout componentsLayout = new GridLayout(1, false);
		componentsLayout.marginHeight = 0;
		componentsLayout.marginWidth = 0;
		componentsComposite.setLayout(componentsLayout);
		componentsComposite.setLayoutData(new GridData(FILL, FILL, true, true, 4, 1));

		UiUtils.paintBordersFor(body);
		initComponentContainers();
		layout(true, true);
	}

	private void nodeNameChanged() {
		SetNameOperation operation = new SetNameOperation(getSceneEditorContext().editorId, target, target.getName(),
				nameText.getText());
		getSceneEditorContext().executeOperation(operation, "Error while renaming node");
	}

	private void nodeEnabledChanged() {
		SetEnabledOperation operation = new SetEnabledOperation(getSceneEditorContext().editorId, target,
				target.isEnabled(), enabledCheck.getSelection());
		getSceneEditorContext().executeOperation(operation, "Error while enabling node");
	}

	@SuppressWarnings("unused")
	private void showMenu() {
		Menu menu = new Menu(getShell(), POP_UP);
		addMenuItem(menu, TransformComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, BulletRigidBodyComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, OrtographicCameraComponent.class);
		addMenuItem(menu, PerspectiveCameraComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, DirectionalLightComponent.class);
		addMenuItem(menu, PointLightComponent.class);
		addMenuItem(menu, SpotLightComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, AudioListenerComponent.class);
		addMenuItem(menu, AudioSourceComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, TagComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, TextureComponent.class);
		addMenuItem(menu, TextureRegionComponent.class);
		addMenuItem(menu, AtlasRegionComponent.class);
		addMenuItem(menu, SkyboxComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, ModelComponent.class);
		addMenuItem(menu, ShapeComponent.class);
		new MenuItem(menu, SEPARATOR);
		addMenuItem(menu, TestPropertyEditorsComponnent.class);
		new MenuItem(menu, SEPARATOR);
		addScriptMenuItem(menu);

		Point buttonLocation = menuButton.getLocation();
		Rectangle rect = menuButton.getBounds();
		Point menuLocation = new Point(buttonLocation.x - 1, buttonLocation.y + rect.height);

		menu.setLocation(getDisplay().map(menuButton.getParent(), null, menuLocation));
		menu.setVisible(true);
	}

	private void initComponentContainers() {
		ImmutableArray<SceneNodeComponent2> components = target.components;
		for (int i = 0; i < components.size(); i++) {
			createSection(components.get(i));
		}
	}

	private Section createSection(SceneNodeComponent2 component) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Section section = toolkit.createSection(componentsComposite, TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		section.setText(Models.getModel(component).getName());
		section.setLayoutData(new GridData(FILL, FILL, true, false, 1, 1));

		MetaModelEditor<SceneNodeComponent2> editor = createEditor(section, getSceneEditorContext(), component);
		Signal1<PropertyValueChangedEvent> signal = editor.getContext().propertyChangedSignal;
		signal.addListener(e -> notifySceneChanged());

		section.setClient(editor);
		section.setExpanded(true);
		editors.put(component, section);

		return section;
	}

	private void notifySceneChanged() {
		EventService.post(getSceneEditorContext().editorId, SceneChangedEvent.instance);
	}

	private void addComponent(SceneNodeComponent2 component) {
		int editorId = getSceneEditorContext().editorId;
		AddComponentOperation operation = new AddComponentOperation(editorId, target, component);
		getSceneEditorContext().executeOperation(operation, "Error while adding component");
	}

	private void addMenuItem(Menu menu, final Class<? extends SceneNodeComponent2> componentType) {
		MenuItem item = new MenuItem(menu, PUSH);
		item.setText(Models.getModel(componentType).getName());
		item.addListener(SWT.Selection, (e) -> addComponent(Reflection.newInstance(componentType)));
		item.setEnabled(target.getComponent(ComponentType.getBaseType(componentType), true) == null);
	}

	private void addScriptMenuItem(Menu menu) {
		MenuItem item = new MenuItem(menu, PUSH);
		item.setText("Script");
		item.addListener(SWT.Selection, (e) -> scriptMenuSeleted());
	}

	private void scriptMenuSeleted() {
		Thread current = Thread.currentThread();
		ClassLoader contextClassLoader = current.getContextClassLoader();
		try {
			current.setContextClassLoader(getSceneEditorContext().classLoader);
			addScriptComponent();
		} catch (Exception e) {
			String message = "Error occurred while adding script component";
			GurellaStudioPlugin.showError(e, message);
		} finally {
			current.setContextClassLoader(contextClassLoader);
		}
	}

	private void addScriptComponent() throws Exception {
		IJavaProject javaProject = getSceneEditorContext().javaProject;
		IJavaSearchScope scope = createHierarchyScope(javaProject.findType(SceneNodeComponent2.class.getName()));
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(getShell());
		SelectionDialog dialog = JavaUI.createTypeDialog(getShell(), monitor, scope, CONSIDER_CLASSES, false);
		if (dialog.open() != IDialogConstants.OK_ID) {
			return;
		}

		Object[] types = dialog.getResult();
		if (types != null && types.length > 0) {
			IType type = (IType) types[0];
			ClassLoader classLoader = getSceneEditorContext().classLoader;
			Class<?> resolvedClass = classLoader.loadClass(type.getFullyQualifiedName());
			Constructor<?> constructor = resolvedClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			SceneNodeComponent2 component = Values.cast(constructor.newInstance(new Object[0]));
			addComponent(component);
		}
	}

	@Override
	public void nodeAdded(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode2 parentNode, SceneNode2 node) {
		if (node == target) {
			dispose();
		}
	}

	@Override
	public void componentAdded(SceneNode2 node, SceneNodeComponent2 component) {
		if (component.getNode() == target) {
			createSection(component);
			reflow(true);
		}
	}

	@Override
	public void componentRemoved(SceneNode2 node, SceneNodeComponent2 component) {
		Section section = editors.get(component);
		if (section != null) {
			section.dispose();
			reflow(true);
		}
	}

	@Override
	public void nodeNameChanged(SceneNode2 node) {
		if (target == node && !nameText.isDisposed() && !nameText.getText().equals(node.getName())) {
			nameText.removeListener(SWT.Modify, nameChangedlLstener);
			nameText.setText(target.getName());
			nameText.addListener(SWT.Modify, nameChangedlLstener);
		}
	}

	@Override
	public void nodeEnabledChanged(SceneNode2 node) {
		if (target == node && !enabledCheck.isDisposed() && enabledCheck.getSelection() != node.isEnabled()) {
			enabledCheck.removeListener(SWT.Selection, nodeEnabledListener);
			enabledCheck.setSelection(node.isEnabled());
			enabledCheck.addListener(SWT.Selection, nodeEnabledListener);
		}
	}

	private static class SetNameOperation extends AbstractOperation {
		final int editorId;
		final SceneNode2 node;
		final String oldValue;
		final String newValue;

		public SetNameOperation(int editorId, SceneNode2 node, String oldValue, String newValue) {
			super("Name");
			this.editorId = editorId;
			this.node = node;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			node.setName(newValue);
			notifyNodeNameChanged();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			node.setName(oldValue);
			notifyNodeNameChanged();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			return execute(monitor, adaptable);
		}

		private void notifyNodeNameChanged() {
			EventService.post(editorId, new NodeNameChangedEvent(node));
			EventService.post(editorId, SceneChangedEvent.instance);
		}
	}

	private static class SetEnabledOperation extends AbstractOperation {
		final int editorId;
		final SceneNode2 node;
		final boolean oldValue;
		final boolean newValue;

		public SetEnabledOperation(int editorId, SceneNode2 node, boolean oldValue, boolean newValue) {
			super("Enabled");
			this.editorId = editorId;
			this.node = node;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			node.setEnabled(newValue);
			notifyNodeEnabledChanged();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			node.setEnabled(oldValue);
			notifyNodeEnabledChanged();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			return execute(monitor, adaptable);
		}

		private void notifyNodeEnabledChanged() {
			EventService.post(editorId, new NodeEnabledChangedEvent(node));
			EventService.post(editorId, SceneChangedEvent.instance);
		}
	}
}
