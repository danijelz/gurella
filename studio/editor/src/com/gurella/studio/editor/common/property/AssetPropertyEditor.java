package com.gurella.studio.editor.common.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.asset.AssetService;
import com.gurella.studio.editor.common.AssetSelectionWidget;
import com.gurella.studio.editor.utils.UiUtils;

public class AssetPropertyEditor<T> extends SimplePropertyEditor<T> {
	private AssetSelectionWidget<T> assetWidget;

	public AssetPropertyEditor(Composite parent, PropertyEditorContext<?, T> context, Class<T> assetType) {
		super(parent, context);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		assetWidget = new AssetSelectionWidget<>(content, assetType);
		assetWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		assetWidget.setAsset(getValue());
		assetWidget.setSelectionChangedListener(this::assetSelectionChanged);
		UiUtils.paintBordersFor(content);
	}

	private void assetSelectionChanged(T oldAsset, T newAsset) {
		if (oldAsset != null) {
			AssetService.unload(oldAsset); // TODO resource deprendencies
		}
		setValue(newAsset);
	}

	@Override
	protected void updateValue(T value) {
		assetWidget.setAsset(value);
	}
}
