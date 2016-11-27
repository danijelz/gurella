package com.gurella.studio.editor.inspector.prefab;

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

import org.eclipse.core.resources.IFile;
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
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.metatype.Models;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.rigidbody.BulletRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.renderable.AtlasRegionComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.ShapeComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.TextureRegionComponent;
import com.gurella.engine.scene.renderable.skybox.SkyboxComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.test.TestPropertyEditorsComponent;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.inspector.InspectableContainer;
import com.gurella.studio.editor.inspector.InspectorView;
import com.gurella.studio.editor.utils.UiUtils;

public class PrefabInspectableContainer extends InspectableContainer<IFile> {
	private Text nameText;
	private Button enabledCheck;
	private Label menuButton;
	private Composite componentsComposite;
	private Array<BeanEditor<?>> componentEditors = new Array<>();

	SceneNode2 prefab;

	public PrefabInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);
		setText(target.getName());

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		toolkit.decorateFormHeading(getForm());
		Composite body = getBody();
		GridLayoutFactory.fillDefaults().numColumns(4).extendedMargins(0, 10, 4, 0).applyTo(body);

		prefab = AssetService.load(target.getLocation().toString());

		Label nameLabel = toolkit.createLabel(getBody(), " Name: ");
		nameLabel.setLayoutData(new GridData(BEGINNING, CENTER, false, false));

		nameText = UiUtils.createText(body);
		nameText.setText(prefab.getName());
		nameText.setLayoutData(new GridData(FILL, BEGINNING, true, false));
		nameText.addListener(SWT.Modify, (e) -> nodeNameChanged());

		enabledCheck = toolkit.createButton(getBody(), "Enabled", CHECK);
		enabledCheck.setLayoutData(new GridData(END, CENTER, false, false));
		enabledCheck.setSelection(prefab.isEnabled());
		enabledCheck.addListener(SWT.Selection, (e) -> nodeEnabledChanged());

		menuButton = toolkit.createLabel(getBody(), " ", NONE);
		menuButton.setImage(GurellaStudioPlugin.createImage("icons/menu.png"));
		menuButton.setLayoutData(new GridData(END, CENTER, false, false));
		menuButton.addListener(SWT.MouseUp, (e) -> showMenu());

		componentsComposite = toolkit.createComposite(getBody());
		GridLayout componentsLayout = new GridLayout(1, false);
		componentsLayout.marginHeight = 0;
		componentsLayout.marginWidth = 0;
		componentsComposite.setLayout(componentsLayout);
		componentsComposite.setLayoutData(new GridData(FILL, FILL, true, true, 4, 1));

		UiUtils.paintBordersFor(body);
		initComponentContainers();
		layout(true, true);
		addDisposeListener(e -> AssetService.unload(prefab));
	}

	private void nodeNameChanged() {
		prefab.setName(nameText.getText());
		// postMessage(new NodeNameChangedMessage(prefab));
	}

	private void nodeEnabledChanged() {
		prefab.setEnabled(enabledCheck.getSelection());
		// postMessage(SceneChangedMessage.instance);
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
		addMenuItem(menu, PointLightComponent.class);
		addMenuItem(menu, DirectionalLightComponent.class);
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
		addMenuItem(menu, TestPropertyEditorsComponent.class);
		new MenuItem(menu, SEPARATOR);
		addScriptMenuItem(menu);

		Point buttonLocation = menuButton.getLocation();
		Rectangle rect = menuButton.getBounds();
		Point menuLocation = new Point(buttonLocation.x - 1, buttonLocation.y + rect.height);

		menu.setLocation(getDisplay().map(menuButton.getParent(), null, menuLocation));
		menu.setVisible(true);
	}

	private void initComponentContainers() {
		ImmutableArray<SceneNodeComponent2> components = prefab.components;
		for (int i = 0; i < components.size(); i++) {
			SceneNodeComponent2 component = components.get(i);
			componentEditors.add(createSection(component));
		}
	}

	private BeanEditor<SceneNodeComponent2> createSection(SceneNodeComponent2 component) {
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		Section section = toolkit.createSection(componentsComposite, TWISTIE | SHORT_TITLE_BAR | NO_TITLE_FOCUS_BOX);
		section.setText(Models.getModel(component).getName());
		section.setLayoutData(new GridData(FILL, FILL, true, false, 1, 1));

		BeanEditor<SceneNodeComponent2> componentEditor = createEditor(section, editorContext, component);
		/*
		 * componentEditor.getContext().propertyChangedSignal .addListener((event) ->
		 * postMessage(SceneChangedMessage.instance));
		 */
		section.setClient(componentEditor);
		section.setExpanded(true);

		return componentEditor;
	}

	private void addComponent(SceneNodeComponent2 component) {
		prefab.addComponent(component);
		componentEditors.add(createSection(component));
		// TODO postMessage(new ComponentAddedMessage(component));
		layout(true, true);
		reflow(true);
	}

	private void addMenuItem(Menu menu, final Class<? extends SceneNodeComponent2> componentType) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText(Models.getModel(componentType).getName());
		item1.addListener(SWT.Selection, (e) -> addComponent(Reflection.newInstance(componentType)));
		item1.setEnabled(prefab.getComponent(componentType, true) == null);
	}

	private void addScriptMenuItem(Menu menu) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText("Script");
		item1.addListener(SWT.Selection, (e) -> scriptMenuSeleted());
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
		IJavaSearchScope scope = createHierarchyScope(javaProject.findType(SceneNodeComponent2.class.getName()));
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
			SceneNodeComponent2 component = Values.cast(constructor.newInstance(new Object[0]));
			addComponent(component);
		}
	}
}
