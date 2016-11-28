package com.gurella.studio.editor.inspector.node;

import static com.gurella.studio.editor.common.bean.BeanEditorFactory.createEditor;
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
import java.util.IdentityHashMap;
import java.util.Map;

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
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.event.EventService;
import com.gurella.engine.event.Signal1;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.plugin.Workbench;
import com.gurella.engine.scene.ComponentType;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.SceneNode;
import com.gurella.engine.scene.SceneNodeComponent;
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
import com.gurella.engine.test.TestArrayEditorComponent;
import com.gurella.engine.test.TestEditorComponent;
import com.gurella.engine.test.TestInputComponent;
import com.gurella.engine.test.TestPropertyEditorsComponent;
import com.gurella.engine.test.TestTypeSelectionComponnent;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.common.bean.BeanEditorContext.PropertyValueChangedEvent;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.operation.AddComponentOperation;
import com.gurella.studio.editor.operation.RenameNodeOperation;
import com.gurella.studio.editor.operation.SetNodeEnabledOperation;
import com.gurella.studio.editor.preferences.PreferencesExtension;
import com.gurella.studio.editor.preferences.PreferencesNode;
import com.gurella.studio.editor.preferences.PreferencesStore;
import com.gurella.studio.editor.subscription.EditorSceneActivityListener;
import com.gurella.studio.editor.subscription.NodeEnabledChangeListener;
import com.gurella.studio.editor.subscription.NodeNameChangeListener;
import com.gurella.studio.editor.utils.SceneChangedEvent;
import com.gurella.studio.editor.utils.UiUtils;

public class NodeInspectableContainer extends InspectableContainer<SceneNode> implements EditorSceneActivityListener,
		NodeNameChangeListener, NodeEnabledChangeListener, PreferencesExtension {
	private Text nameText;
	private Listener nameChangedlLstener;

	private Button enabledCheck;
	private Listener nodeEnabledListener;

	private Label menuButton;

	private Composite componentsComposite;
	private Map<SceneNodeComponent, Section> editors = new IdentityHashMap<>();

	private PreferencesStore preferencesStore;

	public NodeInspectableContainer(InspectorView parent, SceneNode target) {
		super(parent, target);

		int editorId = editorContext.editorId;
		addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);

		addDisposeListener(e -> Workbench.deactivate(this));
		Workbench.activate(this);

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
		RenameNodeOperation operation = new RenameNodeOperation(editorContext.editorId, target, nameText.getText());
		editorContext.executeOperation(operation, "Error while renaming node");
	}

	private void nodeEnabledChanged() {
		SetNodeEnabledOperation operation = new SetNodeEnabledOperation(editorContext.editorId, target,
				target.isEnabled(), enabledCheck.getSelection());
		editorContext.executeOperation(operation, "Error while enabling node");
	}

	private void showMenu() {
		Menu menu = new Menu(getShell(), POP_UP);
		addMenuItem(menu, TransformComponent.class);
		addSeparator(menu);
		addMenuItem(menu, BulletRigidBodyComponent.class);
		addSeparator(menu);
		addMenuItem(menu, OrtographicCameraComponent.class);
		addMenuItem(menu, PerspectiveCameraComponent.class);
		addSeparator(menu);
		addMenuItem(menu, DirectionalLightComponent.class);
		addMenuItem(menu, PointLightComponent.class);
		addMenuItem(menu, SpotLightComponent.class);
		addSeparator(menu);
		addMenuItem(menu, AudioListenerComponent.class);
		addMenuItem(menu, AudioSourceComponent.class);
		addSeparator(menu);
		addMenuItem(menu, TagComponent.class);
		addSeparator(menu);
		addMenuItem(menu, TextureComponent.class);
		addMenuItem(menu, TextureRegionComponent.class);
		addMenuItem(menu, AtlasRegionComponent.class);
		addMenuItem(menu, SkyboxComponent.class);
		addSeparator(menu);
		addMenuItem(menu, ModelComponent.class);
		addMenuItem(menu, ShapeComponent.class);
		addSeparator(menu);
		addMenuItem(menu, TestPropertyEditorsComponent.class);
		addMenuItem(menu, TestEditorComponent.class);
		addMenuItem(menu, TestInputComponent.class);
		addMenuItem(menu, TestTypeSelectionComponnent.class);
		addMenuItem(menu, TestArrayEditorComponent.class);
		addSeparator(menu);
		addScriptMenuItem(menu);

		Point buttonLocation = menuButton.getLocation();
		Rectangle rect = menuButton.getBounds();
		Point menuLocation = new Point(buttonLocation.x - 1, buttonLocation.y + rect.height);

		menu.setLocation(getDisplay().map(menuButton.getParent(), null, menuLocation));
		menu.setVisible(true);
	}

	private static MenuItem addSeparator(Menu menu) {
		return new MenuItem(menu, SEPARATOR);
	}

	private void initComponentContainers() {
		ImmutableArray<SceneNodeComponent> components = target.components;
		for (int i = 0; i < components.size(); i++) {
			createSection(components.get(i));
		}
	}

	private Section createSection(SceneNodeComponent component) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();

		Section section = toolkit.createSection(componentsComposite, TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		section.setText(MetaTypes.getMetaType(component).getName());
		section.setLayoutData(new GridData(FILL, FILL, true, false, 1, 1));
		section.addExpansionListener(new ExpansionListener(component));

		BeanEditor<SceneNodeComponent> editor = createEditor(section, editorContext, component);
		Signal1<PropertyValueChangedEvent> signal = editor.getContext().propertiesSignal;
		signal.addListener(e -> notifySceneChanged());

		section.setClient(editor);
		section.setExpanded(getPreferences().getBoolean(component.ensureUuid(), true));
		editors.put(component, section);

		return section;
	}

	private PreferencesNode getPreferences() {
		return preferencesStore.sceneNode().node(NodeInspectableContainer.class);
	}

	private void notifySceneChanged() {
		EventService.post(editorContext.editorId, SceneChangedEvent.instance);
	}

	private void addComponent(SceneNodeComponent component) {
		int editorId = editorContext.editorId;
		AddComponentOperation operation = new AddComponentOperation(editorId, target, component);
		editorContext.executeOperation(operation, "Error while adding component");
	}

	private void addMenuItem(Menu menu, final Class<? extends SceneNodeComponent> componentType) {
		MenuItem item = new MenuItem(menu, PUSH);
		item.setText(MetaTypes.getMetaType(componentType).getName());
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
			current.setContextClassLoader(editorContext.classLoader);
			addScriptComponent();
		} catch (Exception e) {
			String message = "Error occurred while adding script component";
			GurellaStudioPlugin.showError(e, message);
		} finally {
			current.setContextClassLoader(contextClassLoader);
		}
	}

	private void addScriptComponent() throws Exception {
		IJavaProject javaProject = editorContext.javaProject;
		IJavaSearchScope scope = createHierarchyScope(javaProject.findType(SceneNodeComponent.class.getName()));
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(getShell());
		SelectionDialog dialog = JavaUI.createTypeDialog(getShell(), monitor, scope, CONSIDER_CLASSES, false);
		if (dialog.open() != IDialogConstants.OK_ID) {
			return;
		}

		Object[] types = dialog.getResult();
		if (types != null && types.length > 0) {
			IType type = (IType) types[0];
			ClassLoader classLoader = editorContext.classLoader;
			Class<?> resolvedClass = classLoader.loadClass(type.getFullyQualifiedName());
			Constructor<?> constructor = resolvedClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			SceneNodeComponent component = Values.cast(constructor.newInstance(new Object[0]));
			addComponent(component);
		}
	}

	@Override
	public void nodeAdded(Scene scene, SceneNode parentNode, SceneNode node) {
	}

	@Override
	public void nodeRemoved(Scene scene, SceneNode parentNode, SceneNode node) {
		if (node == target) {
			dispose();
		}
	}

	@Override
	public void componentAdded(SceneNode node, SceneNodeComponent component) {
		if (node == target) {
			createSection(component);
			reflow(true);
		}
	}

	@Override
	public void componentRemoved(SceneNode node, SceneNodeComponent component) {
		Section section = editors.remove(component);
		if (section != null) {
			section.dispose();
			reflow(true);
		}
	}

	@Override
	public void nodeIndexChanged(SceneNode node, int newIndex) {
	}

	@Override
	public void componentIndexChanged(SceneNodeComponent component, int newIndex) {
		Section section = editors.get(component);
		if (section == null) {
			return;
		}
		ImmutableArray<SceneNodeComponent> components = component.getNode().components;
		int size = components.size();
		if (size < 2) {
			return;
		}

		if (newIndex == 0) {
			Section nextSection = editors.get(components.get(1));
			section.moveBelow(nextSection);
		} else {
			Section nextSection = editors.get(components.get(newIndex - 1));
			section.moveAbove(nextSection);
		}
	}

	@Override
	public void nodeNameChanged(SceneNode node) {
		if (target == node && !nameText.isDisposed() && !nameText.getText().equals(node.getName())) {
			nameText.removeListener(SWT.Modify, nameChangedlLstener);
			nameText.setText(target.getName());
			nameText.addListener(SWT.Modify, nameChangedlLstener);
		}
	}

	@Override
	public void nodeEnabledChanged(SceneNode node) {
		if (target == node && !enabledCheck.isDisposed() && enabledCheck.getSelection() != node.isEnabled()) {
			enabledCheck.removeListener(SWT.Selection, nodeEnabledListener);
			enabledCheck.setSelection(node.isEnabled());
			enabledCheck.addListener(SWT.Selection, nodeEnabledListener);
		}
	}

	@Override
	public void setPreferencesStore(PreferencesStore preferencesStore) {
		this.preferencesStore = preferencesStore;
	}

	private class ExpansionListener extends ExpansionAdapter {
		final SceneNodeComponent component;

		ExpansionListener(SceneNodeComponent component) {
			this.component = component;
		}

		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			getPreferences().putBoolean(component.ensureUuid(), e.getState());
		}
	}
}
