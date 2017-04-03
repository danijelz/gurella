package com.gurella.studio.editor.ui.property;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.Files.FileType;
import com.gurella.engine.asset.AssetReference;
import com.gurella.engine.utils.DefaultInstances;
import com.gurella.engine.utils.Reflection;
import com.gurella.studio.common.ReferencedTypeResolver;
import com.gurella.studio.editor.ui.AssetSelectionWidget;
import com.gurella.studio.editor.ui.bean.BeanEditorContext;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class AssetReferencePropertyEditor<T> extends SimplePropertyEditor<AssetReference<T>> {
	private AssetSelectionWidget<T> assetWidget;
	private Object rootAsset;
	private Class<T> assetType;

	public AssetReferencePropertyEditor(Composite parent, PropertyEditorContext<?, AssetReference<T>> context) {
		super(parent, context);
		this.assetType = Try.ofFailable(() -> resolveAssetType()).orElse(resolveDefaultAssetType());

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);

		rootAsset = getManagedAsset();

		assetWidget = new AssetSelectionWidget<>(content, context.gdxContextId, assetType);
		assetWidget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		AssetReference<T> reference = getValue();
		assetWidget.setSelection(reference == null ? null : reference.getFileName());
		assetWidget.setSelectionListener(this::assetSelectionChanged);
		assetWidget.setEnabled(assetType != null && rootAsset != null);

		UiUtils.paintBordersFor(content);
	}

	private Class<T> resolveAssetType() {
		Optional<Class<T>> genericType = PropertyEditorData.getGenericType(context, 0);
		if (genericType.isPresent()) {
			return genericType.get();
		}

		Optional<String> referencedType = ReferencedTypeResolver.resolveReferencedType(context);
		return referencedType.map(n -> Reflection.<T> forName(n)).orElse(resolveDefaultAssetType());
	}

	private Class<T> resolveDefaultAssetType() {
		AssetReference<T> defaultValue = getDefaultValue();
		return defaultValue == null ? null : defaultValue.getAssetType();
	}

	private AssetReference<T> getDefaultValue() {
		return DefaultInstances.getDefault(context.metaType.getType(), context.property);
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

	private void assetSelectionChanged(@SuppressWarnings("unused") T oldAsset, T newAsset) {
		if (newAsset == null) {
			AssetReference<T> defaultValue = getDefaultValue();
			if (context.isFixedValue() && defaultValue != null) {
				setValue(new AssetReference<>(assetType, null, defaultValue.getFileType()));
			} else {
				setValue(null);
			}
		} else {
			String fileName = GdxContext.getFileName(context.gdxContextId, newAsset);
			setValue(new AssetReference<>(assetType, fileName, FileType.Internal));
		}
	}

	@Override
	protected void updateValue(AssetReference<T> value) {
		assetWidget.setSelection(value == null ? null : value.getFileName());
	}
}
