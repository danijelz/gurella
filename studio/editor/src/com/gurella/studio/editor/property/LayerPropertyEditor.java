package com.gurella.studio.editor.property;

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

import com.gurella.engine.scene.layer.Layer;
import com.gurella.engine.utils.Values;

public class LayerPropertyEditor extends SimplePropertyEditor<Layer> {
	private Combo combo;
	private ComboViewer comboViewer;

	public LayerPropertyEditor(Composite parent, PropertyEditorContext<?, Layer> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		combo = new Combo(body, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Layer) element).name;
			}
		});

		Layer[] constants = { Layer.DEFAULT, Layer.GUI, Layer.DnD };
		comboViewer.setInput(constants);

		Layer value = getValue();
		if (value == null) {
			combo.clearSelection();
		} else {
			final ISelection selection = new StructuredSelection(value);
			comboViewer.setSelection(selection);
		}

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = comboViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					setValue(Values.cast(structuredSelection.getFirstElement()));
				} else {
					setValue(null);
				}
			}
		});
	}
}
