package com.gurella.studio.editor.ui.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.ui.AssetSelectionWidget;
import com.gurella.studio.editor.ui.bean.BeanEditorContext;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class AssetPropertyEditor<T> extends SimplePropertyEditor<T> {
	private Class<T> assetType;

	private AssetSelectionWidget<T> assetSelector;
	private Object rootAsset;

	public AssetPropertyEditor(Composite parent, PropertyEditorContext<?, T> context, Class<T> assetType) {
		super(parent, context);
		this.assetType = assetType;

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		rootAsset = getManagedAsset();

		assetSelector = new AssetSelectionWidget<>(content, context.gdxContextId, assetType);
		assetSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		assetSelector.setAsset(getValue());
		assetSelector.setSelectionListener(this::assetSelectionChanged);
		assetSelector.setEnabled(rootAsset != null);

		UiUtils.paintBordersFor(content);
	}

	private Object getManagedAsset() {
		int gdxContextId = context.gdxContextId;
		BeanEditorContext<?> temp = context;
		while (temp != null) {
			Object bean = temp.bean;
			if (GdxContext.isManaged(gdxContextId, bean)) {
				return bean;
			}
			temp = temp.parent;
		}
		return null;
	}

	private void assetSelectionChanged(String selection) {
		int gdxContextId = context.gdxContextId;
		T oldAsset = getValue();
		T newAsset = Values.isBlank(selection) ? null : GdxContext.load(gdxContextId, selection, assetType);

		if (oldAsset != null && newAsset != null) {
			GdxContext.replaceDependency(gdxContextId, rootAsset, oldAsset, newAsset);
			GdxContext.unload(gdxContextId, oldAsset);
		} else if (oldAsset != null) {
			GdxContext.removeDependency(gdxContextId, rootAsset, oldAsset);
			GdxContext.unload(gdxContextId, oldAsset);
		} else if (newAsset != null) {
			GdxContext.addDependency(gdxContextId, rootAsset, newAsset);
		}

		setValue(newAsset);
	}

	@Override
	protected void updateValue(T value) {
		assetSelector.setAsset(value);
	}
}
