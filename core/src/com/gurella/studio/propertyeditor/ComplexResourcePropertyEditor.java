package com.gurella.studio.propertyeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModel;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ComplexResourcePropertyEditor<T> extends AbstractResourcePropertyEditor<ModelResourceFactory<T>> {
	private VisTable editor = new VisTable();
	
	private VisTable header = new VisTable();
	private VisLabel propertyNameLabel = new VisLabel("");
	private VisTextButton collapseButton = new VisTextButton(" + ");

	private VisTable content = new VisTable();
	private VisCheckBox nullCheck = new VisCheckBox("null");
	private Separator separator = new Separator();
	
	private CollapsibleWidget collapsibleWidget;
	
	private Array<ResourcePropertyEditor<?>> propertyEditors = new Array<ResourcePropertyEditor<?>>();
	
	private Array<Actor> uiComponents = new Array<Actor>();
	
	private ModelResourceFactory<T> propertyFactory;
	
	public ComplexResourcePropertyEditor(ResourceModelProperty property, ModelResourceFactory<T> factory) {
		super(property, factory);
		uiComponents.add(editor);
		
		propertyNameLabel.setText(property.getName() + ": ");
		propertyNameLabel.setEllipsis(true);
		propertyNameLabel.setWidth(80);

		collapseButton.addListener(new ColapeButtonClickListener());

		editor.setBackground("border");
		collapsibleWidget = new CollapsibleWidget(content, true);

		header.setColor(0.5f, 0.5f, 0.5f, 0.5f);
		header.add(propertyNameLabel).top().left().fillX().expandX();
		header.add(collapseButton).top().left().width(20);
		editor.add(header).top().left().fillX().expandX();
		editor.row();
		editor.add(collapsibleWidget).top().left().expand().fill();
		
		@SuppressWarnings("unchecked")
		ModelResourceFactory<T> casted = (ModelResourceFactory<T>) factory.getPropertyValue(property.getName());
		present(casted);
	}
	
	@Override
	public Array<Actor> getUiComponents() {
		return uiComponents;
	}

	@Override
	public int getCellspan(int componentIndex) {
		return 2;
	}

	@Override
	public int getRowspan(int componentIndex) {
		return 1;
	}

	@Override
	public void present(ModelResourceFactory<T> value) {
		propertyFactory = value;
		nullCheck.setChecked(propertyFactory == null);
		nullCheck.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(nullCheck.isChecked() && propertyFactory != null) {
					propertyFactory = null;
					present();
				} else if(!nullCheck.isChecked() && propertyFactory == null) {
					@SuppressWarnings("unchecked")
					Class<T> casted = (Class<T>) ComplexResourcePropertyEditor.this.property.getPropertyType();
					propertyFactory = new ModelResourceFactory<T>(casted);
					present();
				}
			}
		});
		
		present();
	}

	private void present() {
		content.clearChildren();
		content.add(nullCheck).colspan(2).fillX();
		content.row();
		content.add(separator).colspan(2).fillX();
		
		if(propertyFactory == null) {
			return;
		}
		
		content.row();
		
		ResourceModel<T> model = propertyFactory.getModel();
		for (ResourceModelProperty modelProperty : model.getProperties()) {
			ResourcePropertyEditor<?> propertyeditor = PropertyEditorFactory.createEditor(propertyFactory, modelProperty);
			propertyEditors.add(propertyeditor);
			Array<Actor> components = propertyeditor.getUiComponents();

			if (components.size == 1) {
				content.add(components.get(0)).right().top().colspan(2).expandX().fillX().pad(2);
			} else if (components.size == 2) {
				content.add(components.get(0)).right().top().pad(2);
				content.add(components.get(1)).left().top().expandX().fillX().pad(2);
			} else {
				VisTable eitorContent = new VisTable();
				// TODO
				content.add(eitorContent).right().top().colspan(2).expandX().fillX().pad(2);
			}

			content.row();
		}
	}

	@Override
	public void save() {
		factory.setPropertyValue(property.getName(), getValue());
	}
	
	@Override
	public Object getValue() {
		for (int i = 0; i < propertyEditors.size; i++) {
			ResourcePropertyEditor<?> propertyEditor = propertyEditors.get(i);
			propertyEditor.save();
		}
		return propertyFactory;
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
