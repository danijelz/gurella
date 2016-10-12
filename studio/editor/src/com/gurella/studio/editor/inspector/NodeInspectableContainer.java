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
import java.net.URLClassLoader;

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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.ComponentType;
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
import com.gurella.studio.editor.SceneChangedMessage;
import com.gurella.studio.editor.model.MetaModelEditor;
import com.gurella.studio.editor.scene.ComponentAddedMessage;
import com.gurella.studio.editor.scene.NodeNameChangedMessage;
import com.gurella.studio.editor.utils.UiUtils;

public class NodeInspectableContainer extends InspectableContainer<SceneNode2> {
	private Text nameText;
	private Button enabledCheck;
	private Label menuButton;
	private Composite componentsComposite;
	private Array<MetaModelEditor<?>> componentEditors = new Array<>();

	public NodeInspectableContainer(InspectorView parent, SceneNode2 target) {
		super(parent, target);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		Composite body = getBody();
		GridLayoutFactory.fillDefaults().numColumns(4).extendedMargins(0, 10, 4, 0).applyTo(body);

		Label nameLabel = toolkit.createLabel(body, " Name: ");
		nameLabel.setLayoutData(new GridData(BEGINNING, CENTER, false, false));

		nameText = UiUtils.createText(body);
		nameText.setText(target.getName());
		nameText.setLayoutData(new GridData(FILL, BEGINNING, true, false));
		nameText.addListener(SWT.Modify, (e) -> nodeNameChanged());

		enabledCheck = toolkit.createButton(body, "Enabled", CHECK);
		enabledCheck.setLayoutData(new GridData(END, CENTER, false, false));
		enabledCheck.setSelection(target.isEnabled());
		enabledCheck.addListener(SWT.Selection, (e) -> nodeEnabledChanged());

		menuButton = toolkit.createLabel(body, " ", NONE);
		menuButton.setImage(GurellaStudioPlugin.createImage("icons/menu.png"));
		menuButton.setLayoutData(new GridData(END, CENTER, false, false));
		menuButton.addListener(SWT.MouseUp, (e) -> showMenu());

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
		SetNameOperation operation = new SetNameOperation(target.getName(), nameText.getText());
		getSceneEditorContext().executeOperation(operation, "Error while renaming node");
	}

	private void renameNode() {
		target.setName(nameText.getText());
		postMessage(new NodeNameChangedMessage(target));
	}

	private void nodeEnabledChanged() {
		target.setEnabled(enabledCheck.getSelection());
		postMessage(SceneChangedMessage.instance);
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
			SceneNodeComponent2 component = components.get(i);
			componentEditors.add(createSection(component));
		}
	}

	private MetaModelEditor<SceneNodeComponent2> createSection(SceneNodeComponent2 component) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Section section = toolkit.createSection(componentsComposite, TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		section.setText(Models.getModel(component).getName());
		section.setLayoutData(new GridData(FILL, FILL, true, false, 1, 1));

		MetaModelEditor<SceneNodeComponent2> componentEditor = createEditor(section, getSceneEditorContext(),
				component);
		componentEditor.getContext().propertyChangedSignal
				.addListener((event) -> postMessage(SceneChangedMessage.instance));
		section.setClient(componentEditor);
		section.setExpanded(true);

		return componentEditor;
	}

	private void addComponent(SceneNodeComponent2 component) {
		target.addComponent(component);
		componentEditors.add(createSection(component));
		postMessage(new ComponentAddedMessage(component));
		layout(true, true);
		reflow(true);
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
			URLClassLoader classLoader = getSceneEditorContext().classLoader;
			Class<?> resolvedClass = classLoader.loadClass(type.getFullyQualifiedName());
			Constructor<?> constructor = resolvedClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			SceneNodeComponent2 component = Values.cast(constructor.newInstance(new Object[0]));
			addComponent(component);
		}
	}

	private class SetNameOperation extends AbstractOperation {
		final String oldValue;
		final String newValue;

		public SetNameOperation(String oldValue, String newValue) {
			super("Name");
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			target.setName(newValue);
			renameNode();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			target.setName(oldValue);
			if (!nameText.isDisposed()) {
				nameText.setText(oldValue);
			}
			renameNode();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			target.setName(newValue);
			if (!nameText.isDisposed()) {
				nameText.setText(oldValue);
			}
			renameNode();
			return Status.OK_STATUS;
		}
	}
}
