package com.gurella.studio.editor.ui.property;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.asset.AssetService;
import com.gurella.studio.common.AssetsFolderLocator;
import com.gurella.studio.editor.ui.AssetSelectionWidget;
import com.gurella.studio.editor.ui.bean.BeanEditorContext;
import com.gurella.studio.editor.utils.UiUtils;

public class AssetPropertyEditor<T> extends SimplePropertyEditor<T> {
	private AssetSelectionWidget<T> assetWidget;
	private Object rootAsset;

	public AssetPropertyEditor(Composite parent, PropertyEditorContext<?, T> context, Class<T> assetType) {
		super(parent, context);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		rootAsset = getManagedAsset();

		IFolder assetsFolder = AssetsFolderLocator.getAssetsFolder(context.javaProject);
		assetWidget = new AssetSelectionWidget<>(content, assetType, assetsFolder);
		assetWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		assetWidget.setAsset(getValue());
		assetWidget.setSelectionListener(this::assetSelectionChanged);
		assetWidget.setEnabled(rootAsset != null);

		UiUtils.paintBordersFor(content);
	}

	private Object getManagedAsset() {
		BeanEditorContext<?> temp = context;
		while (temp != null) {
			Object bean = temp.bean;
			if (AssetService.isManaged(bean)) {
				return bean;
			}
			temp = temp.parent;
		}
		return null;
	}

	private void assetSelectionChanged(T oldAsset, T newAsset) {
		if (oldAsset != null && newAsset != null) {
			AssetService.replaceDependency(rootAsset, oldAsset, newAsset);
		} else if (oldAsset != null) {
			AssetService.removeDependency(rootAsset, oldAsset);
		} else if (newAsset != null) {
			AssetService.addDependency(rootAsset, newAsset);
		}

		setValue(newAsset);
	}

	@Override
	protected void updateValue(T value) {
		assetWidget.setAsset(value);
	}
}
