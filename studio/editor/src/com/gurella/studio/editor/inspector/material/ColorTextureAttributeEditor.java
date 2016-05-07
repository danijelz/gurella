package com.gurella.studio.editor.inspector.material;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.material.MaterialDescriptor.TextureAttributeProperties;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.AssetSelectionWidget;
import com.gurella.studio.editor.common.ColorSelectionWidget;
import com.gurella.studio.editor.utils.UiUtils;

public class ColorTextureAttributeEditor extends Composite {
	private ColorSelectionWidget colorSelector;
	private Button colorEnabledButton;

	private AssetSelectionWidget<Texture> textureSelector;
	private Button textureEnabledButton;
	private Text offsetU;
	private Text offsetV;
	private Text scaleU;
	private Text scaleV;

	MaterialDescriptor materialDescriptor;

	Supplier<Color> colorGetter;
	Consumer<Color> colorSetter;

	Supplier<TextureAttributeProperties> textureGetter;

	Runnable updater;

	public ColorTextureAttributeEditor(Composite parent, MaterialDescriptor materialDescriptor,
			Supplier<Color> colorGetter, Consumer<Color> colorSetter,
			Supplier<TextureAttributeProperties> textureGetter, Runnable updater) {
		super(parent, SWT.BORDER);

		this.materialDescriptor = materialDescriptor;
		this.colorGetter = colorGetter;
		this.colorSetter = colorSetter;
		this.textureGetter = textureGetter;
		this.updater = updater;

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		setLayout(layout);

		toolkit.createLabel(this, "Color:");
		colorSelector = new ColorSelectionWidget(this);
		colorSelector.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		toolkit.adapt(colorSelector);

		colorEnabledButton = toolkit.createButton(this, "Enabled", SWT.CHECK);

		Color color = colorGetter.get();
		if (color == null) {
			colorEnabledButton.setSelection(false);
		} else {
			colorSelector.setColor(color);
			colorEnabledButton.setSelection(true);
		}

		colorEnabledButton.addListener(SWT.Selection, e -> enableColor());
		colorSelector.setColorChangeListener(e -> valueChanged());

		Label label = toolkit.createLabel(this, "", SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

		toolkit.createLabel(this, "Texture:");
		textureSelector = new AssetSelectionWidget<Texture>(this, Texture.class);
		toolkit.adapt(textureSelector);
		textureSelector.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		textureSelector.setSelectionChangedListener(this::assetSelectionChanged);

		TextureAttributeProperties properties = textureGetter.get();
		textureEnabledButton = toolkit.createButton(this, "Enabled", SWT.CHECK);
		textureEnabledButton.setSelection(properties.texture != null);
		textureEnabledButton.addListener(SWT.Selection, e -> enableTexture());

		label = toolkit.createLabel(this, "Offset U:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		offsetU = UiUtils.createFloatWidget(this);
		offsetU.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		offsetU.addModifyListener(e -> valueChanged());

		label = toolkit.createLabel(this, " V:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		offsetV = UiUtils.createFloatWidget(this);
		offsetV.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		offsetV.addModifyListener(e -> valueChanged());
		label = toolkit.createLabel(this, " ");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		toolkit.createLabel(this, "Scale U:");
		scaleU = UiUtils.createFloatWidget(this);
		scaleU.addModifyListener(e -> valueChanged());

		toolkit.createLabel(this, " V:");
		scaleV = UiUtils.createFloatWidget(this);
		scaleV.addModifyListener(e -> valueChanged());
		label = toolkit.createLabel(this, " ");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		boolean selection = colorEnabledButton.getSelection();
		colorSelector.setEnabled(selection);

		selection = textureEnabledButton.getSelection();
		textureSelector.setEnabled(selection);
		offsetU.setEnabled(selection);
		offsetV.setEnabled(selection);
		scaleU.setEnabled(selection);
		scaleV.setEnabled(selection);
	}

	private void assetSelectionChanged(Texture oldAsset, Texture newAsset) {
		if (oldAsset != null) {
			materialDescriptor.unload(oldAsset);
		}
		if (newAsset != null) {
			materialDescriptor.bindAsset(newAsset);
		}
		valueChanged();
	}

	private void enableColor() {
		boolean selection = colorEnabledButton.getSelection();
		colorSelector.setEnabled(selection);
		valueChanged();
	}

	private void enableTexture() {
		boolean selection = textureEnabledButton.getSelection();
		textureSelector.setEnabled(selection);
		offsetU.setEnabled(selection);
		offsetV.setEnabled(selection);
		scaleU.setEnabled(selection);
		scaleV.setEnabled(selection);
		valueChanged();
	}

	private void valueChanged() {
		if (colorEnabledButton.getSelection()) {
			colorSetter.accept(colorSelector.getColor());
		} else {
			colorSetter.accept(null);
		}

		TextureAttributeProperties properties = textureGetter.get();
		if (textureEnabledButton.getSelection()) {
			properties.texture = textureSelector.getAsset();
			String textValue = offsetU.getText();
			properties.offsetU = Values.isBlank(textValue) ? 0 : Float.valueOf(textValue).floatValue();
			textValue = offsetV.getText();
			properties.offsetV = Values.isBlank(textValue) ? 0 : Float.valueOf(textValue).floatValue();
			textValue = scaleU.getText();
			properties.scaleU = Values.isBlank(textValue) ? 1 : Float.valueOf(textValue).floatValue();
			textValue = scaleV.getText();
			properties.scaleV = Values.isBlank(textValue) ? 1 : Float.valueOf(textValue).floatValue();
		} else {
			properties.texture = null;
		}

		updater.run();
	}
}
