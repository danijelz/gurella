package com.gurella.studio.resource;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ColapsableResourcePropertiesContainer extends VisTable {
	private VisTable header = new VisTable();
	private VisTextButton collapseButton = new VisTextButton(" + ");
	private VisLabel componentNameLabel = new VisLabel("");

	private CollapsibleWidget collapsibleWidget;
	private ResourcePropertiesContainer resourcePropertiesContainer;

	public ColapsableResourcePropertiesContainer(ModelResourceFactory<?> factory) {
		setBackground("border");
		setWidth(80);

		componentNameLabel.setText(factory.getModel().getDescriptiveName());
		componentNameLabel.setEllipsis(true);
		componentNameLabel.setWidth(80);

		collapseButton.addListener(new ColapeButtonClickListener());

		resourcePropertiesContainer = new ResourcePropertiesContainer(factory);
		collapsibleWidget = new CollapsibleWidget(resourcePropertiesContainer, true);

		header.setBackground("button-down");
		header.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		header.add(componentNameLabel).top().left().fillX().expandX();
		header.add(collapseButton).top().left().width(20);

		add(header).top().left().fillX().expandX();
		row();
		add(collapsibleWidget).top().left().expand().fill();
	}

	public void save() {
		resourcePropertiesContainer.save();
	}

	private final class ColapeButtonClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			if (collapsibleWidget.isCollapsed()) {
				collapseButton.setText(" - ");
				collapsibleWidget.setCollapsed(false);
			} else {
				collapseButton.setText(" + ");
				collapsibleWidget.setCollapsed(true);
			}
		}
	}
}
