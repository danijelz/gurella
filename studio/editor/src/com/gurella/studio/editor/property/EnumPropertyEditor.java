package com.gurella.studio.editor.property;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

public class EnumPropertyEditor<P extends Enum<P>> extends SimplePropertyEditor<P> {
	private Combo combo;
	private ComboViewer comboViewer;

	public EnumPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		combo = new Combo(body, SWT.READ_ONLY);
		Font font = GurellaStudioPlugin.createFont(combo, 8, 0);
		combo.addDisposeListener(e -> GurellaStudioPlugin.destroyFont(font));
		combo.setFont(font);
		GridData comboLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		comboLayoutData.minimumWidth = 100;
		combo.setLayoutData(comboLayoutData);

		comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());

		Class<P> enumType = context.getPropertyType();
		P[] constants = enumType.getEnumConstants();
		if (constants == null) {
			@SuppressWarnings("unchecked")
			Class<P> casted = (Class<P>) enumType.getSuperclass();
			enumType = casted;
			constants = enumType.getEnumConstants();
		}
		comboViewer.setInput(constants);

		P value = getValue();
		final ISelection selection = new StructuredSelection(value);
		comboViewer.setSelection(selection);

		combo.addListener(SWT.Selection, e -> selectionChanged());
		getToolkit().adapt(body);
	}

	public void selectionChanged() {
		IStructuredSelection selection = comboViewer.getStructuredSelection();
		setValue(Values.cast(selection.getFirstElement()));
	}

	@Override
	protected void updateValue(P value) {
		final ISelection selection = new StructuredSelection(value);
		comboViewer.setSelection(selection);
	}
}
