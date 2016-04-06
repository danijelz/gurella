package com.gurella.studio.editor.model.property;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;

public class EnumPropertyEditor<T extends Enum<T>> extends SimplePropertyEditor<T> {
	private Combo combo;
	private ComboViewer comboViewer;

	public EnumPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property,
			Object modelInstance) {
		super(parent, propertiesContainer, property, modelInstance);
	}

	@Override
	protected void buildUi() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		combo = new Combo(this, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());

		Class<T> enumType = property.getType();
		T[] constants = enumType.getEnumConstants();
		if (constants == null) {
			@SuppressWarnings("unchecked")
			Class<T> casted = (Class<T>) enumType.getSuperclass();
			enumType = casted;
			constants = enumType.getEnumConstants();
		}
		comboViewer.setInput(constants);

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = comboViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					property.setValue(getModelInstance(), Values.cast(structuredSelection.getFirstElement()));
				} else {
					property.setValue(getModelInstance(), null);
				}
				setDirty();
			}
		});
	}

	@Override
	public void present(Object modelInstance) {
		T value = property.getValue(modelInstance);
		if (value == null) {
			combo.clearSelection();
		} else {
			final ISelection selection = new StructuredSelection(value);
			comboViewer.setSelection(selection);
		}
	}
}
