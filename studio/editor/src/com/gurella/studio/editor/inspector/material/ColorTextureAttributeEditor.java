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
import com.gurella.studio.editor.ui.AssetSelectionWidget;
import com.gurella.studio.editor.ui.ColorSelectionWidget;
import com.gurella.studio.editor.utils.UiUtils;
import com.gurella.studio.gdx.GdxContext;

public class ColorTextureAttributeEditor extends Composite {
	private final int gdxContextId;

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

	public ColorTextureAttributeEditor(Composite parent, int gdxContextId, MaterialDescriptor materialDescriptor,
			Supplier<Color> colorGetter, Consumer<Color> colorSetter,
			Supplier<TextureAttributeProperties> textureGetter, Runnable updater) {
		super(parent, SWT.NONE);
		this.gdxContextId = gdxContextId;

		this.materialDescriptor = materialDescriptor;
		this.colorGetter = colorGetter;
		this.colorSetter = colorSetter;
		this.textureGetter = textureGetter;
		this.updater = updater;

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = 0;
		layout.marginHeight = 2;
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

		colorEnabledButton.addListener(SWT.Selection, e -> updateColor());
		colorSelector.setColorChangeListener(e -> updateColor());

		Label label = toolkit.createLabel(this, "", SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

		TextureAttributeProperties properties = textureGetter.get();
		toolkit.createLabel(this, "Texture:");
		textureSelector = new AssetSelectionWidget<Texture>(this, gdxContextId, Texture.class);
		toolkit.adapt(textureSelector);
		textureSelector.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		textureSelector.setAsset(properties.texture);
		textureSelector.setSelectionListener(s -> updateTexture());

		textureEnabledButton = toolkit.createButton(this, "Enabled", SWT.CHECK);
		textureEnabledButton.setSelection(properties.texture != null);
		textureEnabledButton.addListener(SWT.Selection, e -> updateTexture());

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
		
		UiUtils.paintBordersFor(this);
	}

	private void updateColor() {
		boolean selection = colorEnabledButton.getSelection();
		colorSelector.setEnabled(selection);
		if (selection) {
			colorSetter.accept(colorSelector.getColor());
		} else {
			colorSetter.accept(null);
		}

		valueChanged();
	}

	private void updateTexture() {
		boolean selection = textureEnabledButton.getSelection();
		textureSelector.setEnabled(selection);
		offsetU.setEnabled(selection);
		offsetV.setEnabled(selection);
		scaleU.setEnabled(selection);
		scaleV.setEnabled(selection);

		TextureAttributeProperties properties = textureGetter.get();
		if (selection) {
			Texture oldTexture = properties.texture;
			Texture newTexture = getSelectedTexture(oldTexture);

			if (oldTexture != newTexture) {
				properties.texture = newTexture;

				if (oldTexture != null && newTexture != null) {
					GdxContext.replaceDependency(gdxContextId, materialDescriptor, oldTexture, newTexture);
					GdxContext.unload(gdxContextId, oldTexture);
				} else if (oldTexture != null) {
					GdxContext.removeDependency(gdxContextId, materialDescriptor, oldTexture);
					GdxContext.unload(gdxContextId, oldTexture);
				} else if (newTexture != null) {
					GdxContext.addDependency(gdxContextId, materialDescriptor, newTexture);
				}
			}

			properties.offsetU = toFloatValue(offsetU.getText(), 0);
			properties.offsetV = toFloatValue(offsetV.getText(), 0);
			properties.scaleU = toFloatValue(scaleU.getText(), 1);
			properties.scaleV = toFloatValue(scaleV.getText(), 1);
		} else {
			Texture oldTexture = properties.texture;
			if (oldTexture != null) {
				GdxContext.removeDependency(gdxContextId, materialDescriptor, oldTexture);
				GdxContext.unload(gdxContextId, oldTexture);
			}

			properties.texture = null;
			properties.offsetU = 0;
			properties.offsetV = 0;
			properties.scaleU = 1;
			properties.scaleV = 1;
		}

		valueChanged();
	}

	private static float toFloatValue(String textValue, float defaultValue) {
		return Values.isBlank(textValue) ? defaultValue : Float.valueOf(textValue).floatValue();
	}

	private Texture getSelectedTexture(Texture oldTexture) {
		String selection = textureSelector.getSelection();
		if (Values.isBlank(selection)) {
			return null;
		} else if (selection.equals(GdxContext.getFileName(gdxContextId, oldTexture))) {
			return oldTexture;
		} else {
			return GdxContext.load(gdxContextId, selection, Texture.class);
		}
	}

	private void valueChanged() {
		updater.run();
	}
}
