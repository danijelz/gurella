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
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.studio.editor.model.ModelPropertiesContainer;
import com.gurella.studio.editor.scene.InspectorView.PropertiesContainer;

public class NodePropertiesContainer extends PropertiesContainer<SceneNode2> {
	private Text nameText;
	private Button enabledCheck;
	private Button menuButton;
	private Composite componentsPropertiesComposite;
	private Array<ModelPropertiesContainer<?>> componentContainers = new Array<>();

	public NodePropertiesContainer(InspectorView parent, SceneNode2 target) {
		super(parent, target);
		setExpandHorizontal(true);
		setMinWidth(200);
		init(getToolkit(), target);
	}

	private void init(FormToolkit toolkit, final SceneNode2 node) {
		toolkit.adapt(this);
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		getBody().setLayout(layout);

		Label nameLabel = toolkit.createLabel(getBody(), "Name: ");
		nameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

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
		enabledCheck.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
		enabledCheck.setSelection(node.isEnabled());
		enabledCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				node.setEnabled(enabledCheck.getSelection());
				setDirty();
			}
		});

		menuButton = toolkit.createButton(getBody(), "", SWT.ARROW | SWT.DOWN);
		menuButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Menu menu = new Menu(getShell(), SWT.POP_UP);
				MenuItem item1 = new MenuItem(menu, SWT.PUSH);
				item1.setText("Add component");
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
}
