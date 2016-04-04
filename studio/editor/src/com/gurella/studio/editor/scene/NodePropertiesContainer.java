package com.gurella.studio.editor.scene;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.scene.SceneNode2;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.scene.audio.AudioListenerComponent;
import com.gurella.engine.scene.audio.AudioSourceComponent;
import com.gurella.engine.scene.bullet.BulletPhysicsRigidBodyComponent;
import com.gurella.engine.scene.camera.OrtographicCameraComponent;
import com.gurella.engine.scene.camera.PerspectiveCameraComponent;
import com.gurella.engine.scene.light.DirectionalLightComponent;
import com.gurella.engine.scene.light.PointLightComponent;
import com.gurella.engine.scene.movement.TransformComponent;
import com.gurella.engine.scene.renderable.AtlasRegionComponent;
import com.gurella.engine.scene.renderable.ModelComponent;
import com.gurella.engine.scene.renderable.SolidComponent;
import com.gurella.engine.scene.renderable.TextureComponent;
import com.gurella.engine.scene.renderable.TextureRegionComponent;
import com.gurella.engine.scene.tag.TagComponent;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;
import com.gurella.studio.nodes.SceneNodePropertiesContainer.TestComponnent;
import com.gurella.studio.nodes.TestInputComponent;

public class NodePropertiesContainer extends PropertiesContainer<SceneNode2> {
	private Text nameText;
	private Button enabledCheck;
	private Button menuButton;
	private Composite componentsPropertiesComposite;
	private Array<ModelPropertiesContainer<?>> componentContainers = new Array<>();

	public NodePropertiesContainer(InspectorView parent, SceneNode2 target) {
		super(parent, target);
		init(getToolkit(), target);
	}

	private void init(FormToolkit toolkit, final SceneNode2 node) {
		toolkit.adapt(this);
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		getBody().setLayout(layout);

		Label nameLabel = toolkit.createLabel(getBody(), "Name: ");
		nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		nameText = toolkit.createText(getBody(), node.getName(), SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				node.setName(nameText.getText());
				setDirty();
				postMessage(new NodeNameChangedMessage(node));
			}
		});

		enabledCheck = toolkit.createButton(getBody(), "Enabled", SWT.CHECK);
		enabledCheck.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		enabledCheck.setSelection(node.isEnabled());
		enabledCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				node.setEnabled(enabledCheck.getSelection());
				setDirty();
			}
		});

		menuButton = toolkit.createButton(getBody(), "", SWT.ARROW | SWT.DOWN);
		menuButton.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		menuButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Menu menu = new Menu(getShell(), SWT.POP_UP);

				addMenuItem(menu, TransformComponent.class);
				//addMenuItem(menu, BulletPhysicsRigidBodyComponent.class);
				addMenuItem(menu, OrtographicCameraComponent.class);
				addMenuItem(menu, PerspectiveCameraComponent.class);
				addMenuItem(menu, PointLightComponent.class);
				addMenuItem(menu, DirectionalLightComponent.class);
				addMenuItem(menu, AudioListenerComponent.class);
				addMenuItem(menu, AudioSourceComponent.class);
				addMenuItem(menu, TagComponent.class);
				// addItem("Layer", LayerComponent.class);
				addMenuItem(menu, TextureComponent.class);
				addMenuItem(menu, TextureRegionComponent.class);
				addMenuItem(menu, AtlasRegionComponent.class);
				addMenuItem(menu, ModelComponent.class);
				addMenuItem(menu, SolidComponent.class);
				addMenuItem(menu, TestComponnent.class);
				addMenuItem(menu, TestInputComponent.class);

				Point loc = menuButton.getLocation();
				Rectangle rect = menuButton.getBounds();
				Point mLoc = new Point(loc.x - 1, loc.y + rect.height);
				menu.setLocation(getDisplay().map(menuButton.getParent(), null, mLoc));
				menu.setVisible(true);
			}
		});

		componentsPropertiesComposite = toolkit.createComposite(getBody());
		GridLayout componentsLayout = new GridLayout(1, false);
		componentsLayout.marginHeight = 0;
		componentsLayout.marginWidth = 0;
		componentsPropertiesComposite.setLayout(componentsLayout);
		componentsPropertiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		initComponentContainers(toolkit);
		layout(true, true);
	}

	private void initComponentContainers(FormToolkit toolkit) {
		ImmutableArray<SceneNodeComponent2> components = target.components;
		for (int i = 0; i < components.size(); i++) {
			SceneNodeComponent2 component = components.get(i);
			Section componentSection = toolkit.createSection(componentsPropertiesComposite,
					ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
			componentSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			componentSection.setExpanded(true);
			componentSection.setText(Models.getModel(component).getName());
			ModelPropertiesContainer<SceneNodeComponent2> propertiesContainer = new ModelPropertiesContainer<>(
					getGurellaEditor(), componentSection, component);
			componentSection.setClient(propertiesContainer);
			componentContainers.add(propertiesContainer);
		}
	}

	private void addComponent(SceneNodeComponent2 component) {
		Section componentSection = getGurellaEditor().getToolkit().createSection(componentsPropertiesComposite,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		componentSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		componentSection.setText(Models.getModel(component).getName());
		ModelPropertiesContainer<SceneNodeComponent2> propertiesContainer = new ModelPropertiesContainer<>(
				getGurellaEditor(), componentSection, component);
		componentSection.setClient(propertiesContainer);
		propertiesContainer.layout(true, true);
		componentSection.setExpanded(true);
		componentContainers.add(propertiesContainer);
		postMessage(new ComponentAddedMessage(component));
		getGurellaEditor().setDirty();
	}

	private void addMenuItem(Menu menu, final Class<? extends SceneNodeComponent2> componentType) {
		MenuItem item1 = new MenuItem(menu, SWT.PUSH);
		item1.setText(Models.getModel(componentType).getName());
		item1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addComponent(target.newComponent(Values.cast(componentType)));
			}
		});
		item1.setEnabled(target.getComponent(componentType) == null);
	}
}
