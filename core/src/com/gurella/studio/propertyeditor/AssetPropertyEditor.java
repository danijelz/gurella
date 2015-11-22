package com.gurella.studio.propertyeditor;

import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.AssetId;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

public class AssetPropertyEditor<T> extends SimpleResourcePropertyEditor<VisTable, AssetId> {
	private VisTable container;
	private VisTextField field;
	private AssetId assetId;
	private Class<T> assetType;

	public AssetPropertyEditor(ResourceModelProperty property, ModelResourceFactory<?> factory, Class<T> assetType) {
		super(property, factory);
		this.assetType = assetType;
	}

	@Override
	public void present(AssetId value) {
		if (value != null) {
			assetId = value;
			field.setText(assetId.getFileName());
		}
	}

	@Override
	protected VisTable createValueComponent() {
		container = new VisTable();
		field = new VisTextField();
		container.add(field);
		return container;
	}

	@Override
	protected AssetId getComponentValue() {
		String text = field.getText();
		if (text == null/* || !GdxEditor.scene.containsAsset(text) */) {
			assetId = null;
		} else {
			assetId = new AssetId(text, assetType.getName());
		}
		return assetId;
	}
}
