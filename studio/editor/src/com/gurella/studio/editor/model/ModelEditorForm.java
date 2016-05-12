package com.gurella.studio.editor.model;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.CLIENT_INDENT;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.NO_TITLE_FOCUS_BOX;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.model.property.ComplexPropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditorContext;
import com.gurella.studio.editor.model.property.SimplePropertyEditor;

public class ModelEditorForm<T> extends ScrolledForm {
	private ModelEditorContext<T> context;
	private List<PropertyEditor<?>> editors = new ArrayList<>();

	private List<PropertyEditor<?>> hoverEditors = new ArrayList<PropertyEditor<?>>();
	private List<PropertyEditor<?>> hoverEditorsTemp = new ArrayList<PropertyEditor<?>>();

	public ModelEditorForm(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new ModelEditorContext<>(sceneEditorContext, modelInstance));
	}

	public ModelEditorForm(Composite parent, ModelEditorContext<T> context) {
		super(parent, SWT.NONE);
		this.context = context;
		setExpandHorizontal(true);
		GurellaStudioPlugin.getToolkit().adapt(this);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		layout.verticalSpacing = 2;
		getBody().setLayout(layout);
		initEditors();
		Listener mouseMoveListener = e -> mouseMoved();
		Display display = getDisplay();
		display.addFilter(SWT.MouseMove, mouseMoveListener);
		addListener(SWT.Dispose, (e) -> display.removeFilter(SWT.MouseMove, mouseMoveListener));
		reflow(true);
	}

	private void initEditors() {
		Property<?>[] array = context.model.getProperties().toArray(Property.class);
		int length = array.length;
		if (length == 0) {
			return;
		}

		Property<?> last = array[array.length - 1];
		Arrays.stream(array).filter(p -> p.isEditable()).forEach(p -> addEditor(p, p != last));
	}

	private <V> void addEditor(Property<V> property, boolean addSeperator) {
		FormToolkit toolkit = getToolkit();
		Composite body = getBody();
		PropertyEditor<V> editor = createEditor(getBody(), new PropertyEditorContext<>(context, property));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite composite = editor.getComposite();
		composite.setLayoutData(layoutData);
		editors.add(editor);

		if (editor instanceof SimplePropertyEditor) {
			String name = editor.getDescriptiveName();
			boolean longName = name.length() > 20;

			Label label = toolkit.createLabel(body, name + ":");
			label.setAlignment(SWT.RIGHT);
			label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
			GridData labelLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
			label.setLayoutData(labelLayoutData);
			label.moveAbove(composite);

			if (longName) {
				labelLayoutData.horizontalAlignment = SWT.BEGINNING;
				labelLayoutData.horizontalSpan = 2;
				layoutData.horizontalSpan = 2;
			}
		} else if (editor instanceof ComplexPropertyEditor) {
			Section componentSection = toolkit.createSection(body, TWISTIE | NO_TITLE_FOCUS_BOX | CLIENT_INDENT);
			componentSection.setSize(100, 100);
			GridData sectionLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
			sectionLayoutData.widthHint = 100;
			componentSection.setLayoutData(sectionLayoutData);
			componentSection.setText(editor.getDescriptiveName());
			composite.setParent(componentSection);
			componentSection.setClient(composite);
			componentSection.setExpanded(true);
			componentSection.layout(true, true);
		} else {
			layoutData.horizontalSpan = 2;
		}

		if (addSeperator) {
			Label separator = toolkit.createSeparator(body, SWT.HORIZONTAL);
			separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		}
	}

	protected void mouseMoved() {
		extractHoveredEditors();

		hoverEditors.stream().filter(e -> !hoverEditorsTemp.contains(e)).forEach(e -> e.setHover(false));
		hoverEditorsTemp.stream().filter(e -> !hoverEditors.contains(e)).forEach(e -> e.setHover(true));

		hoverEditors.clear();
		List<PropertyEditor<?>> temp = hoverEditorsTemp;
		hoverEditorsTemp = hoverEditors;
		hoverEditors = temp;
	}

	private void extractHoveredEditors() {
		Control cursorControl = getDisplay().getCursorControl();
		if (cursorControl == null) {
			return;
		}

		Composite parent = cursorControl.getParent();

		Composite body = getBody();
		while (parent != null && parent != body) {
			PropertyEditor<?> editor = (PropertyEditor<?>) parent.getData(PropertyEditor.class.getName());
			if (editor != null) {
				hoverEditorsTemp.add(editor);
			}

			parent = parent.getParent();
		}

		if (body != parent) {
			hoverEditorsTemp.clear();
		}
	}
}
