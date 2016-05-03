package com.gurella.studio.editor.inspector.material;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.material.MaterialDescriptor.BlendFunction;
import com.gurella.engine.graphics.material.MaterialDescriptor.BlendingAttributeProperties;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.UiUtils;

public class BlendAttributeEditor extends Composite {
	private ComboViewer sourceFunctionCombo;
	private ComboViewer destFunctionCombo;
	private Text opacityText;

	private Button enabledButton;

	MaterialDescriptor materialDescriptor;

	Supplier<BlendingAttributeProperties> propertiesGetter;
	Runnable updater;

	public BlendAttributeEditor(Composite parent, MaterialDescriptor materialDescriptor,
			Supplier<BlendingAttributeProperties> propertiesGetter, Runnable updater) {
		super(parent, SWT.BORDER);

		this.materialDescriptor = materialDescriptor;
		this.propertiesGetter = propertiesGetter;
		this.updater = updater;

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		setLayout(layout);

		Label label = toolkit.createLabel(this, "Source:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		sourceFunctionCombo = UiUtils.createEnumComboViewer(this, BlendFunction.class);
		sourceFunctionCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		sourceFunctionCombo.getControl().addListener(SWT.Selection, e -> valueChanged());
		enabledButton = toolkit.createButton(this, "Enabled", SWT.CHECK);
		enabledButton.addListener(SWT.Selection, e -> valueChanged());

		label = toolkit.createLabel(this, "Destination:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		destFunctionCombo = UiUtils.createEnumComboViewer(this, BlendFunction.class);
		destFunctionCombo.getControl().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		destFunctionCombo.getControl().addListener(SWT.Selection, e -> valueChanged());
		Composite composite = toolkit.createComposite(this);
		composite.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		label = toolkit.createLabel(this, "Opacity:");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		opacityText = UiUtils.createFloatWidget(this);
		opacityText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		opacityText.addModifyListener(e -> valueChanged());
	}

	private void valueChanged() {
		BlendingAttributeProperties properties = propertiesGetter.get();
		if (enabledButton.getSelection()) {
			properties.blended = true;
			properties.sourceFunction = (BlendFunction) sourceFunctionCombo.getStructuredSelection().getFirstElement();
			properties.destFunction = (BlendFunction) destFunctionCombo.getStructuredSelection().getFirstElement();
			String opacity = opacityText.getText();
			properties.opacity = Values.isBlank(opacity) ? 0 : Float.valueOf(opacity).floatValue();
		} else {
			properties.blended = false;
		}

		updater.run();
	}
}
