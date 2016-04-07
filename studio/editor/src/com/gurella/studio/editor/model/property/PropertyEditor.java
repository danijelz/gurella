package com.gurella.studio.editor.model.property;

import static org.eclipse.swt.SWT.ARROW;
import static org.eclipse.swt.SWT.DOWN;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.GurellaStudioPlugin;

public abstract class PropertyEditor<P> {
	private Composite composite;
	protected Composite body;
	private Button menuButton;
	protected PropertyEditorContext<?, P> context;

	protected P cachedValue;

	public PropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		this.context = context;
		FormToolkit toolkit = getToolkit();
		composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		body = toolkit.createComposite(composite);
		body.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		menuButton = toolkit.createButton(composite, "", ARROW | DOWN);
		menuButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		menuButton.setVisible(false);

		cachedValue = getValue();
		composite.addListener(SWT.MouseEnter, (e) -> mouseEnter());
		composite.addListener(SWT.MouseExit, (e) -> mouseExit());
	}

	public Composite getComposite() {
		return composite;
	}

	public Composite getBody() {
		return body;
	}

	protected FormToolkit getToolkit() {
		return GurellaStudioPlugin.getToolkit();
	}

	private void mouseEnter() {
		menuButton.setVisible(true);
	}

	private void mouseExit() {
		menuButton.setVisible(false);
	}

	public String getDescriptiveName() {
		return context.property.getDescriptiveName();
	}

	public Property<P> getProperty() {
		return context.property;
	}

	public P getCachedValue() {
		return cachedValue;
	}

	protected Object getModelInstance() {
		return context.modelInstance;
	}

	protected P getValue() {
		return context.property.getValue(context.modelInstance);
	}

	protected void setValue(P value) {
		if (!Values.isEqual(cachedValue, value)) {
			context.property.setValue(context.modelInstance, value);
			context.propertyValueChanged(cachedValue, value);
			cachedValue = value;
		}
	}
}
