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

public class ModelEditorContainer<T> extends ScrolledForm {
	private ModelEditorContext<T> context;
	private List<PropertyEditor<?>> editors = new ArrayList<>();

	private List<PropertyEditor<?>> hoverEditors = new ArrayList<PropertyEditor<?>>();
	private List<PropertyEditor<?>> hoverEditorsTemp = new ArrayList<PropertyEditor<?>>();

	public ModelEditorContainer(Composite parent, SceneEditorContext sceneEditorContext, T modelInstance) {
		this(parent, new ModelEditorContext<>(sceneEditorContext, modelInstance));
	}

	public ModelEditorContainer(Composite parent, ModelEditorContext<T> context) {
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
		Arrays.stream(array).filter(p -> p.isEditable()).forEach(p -> addEditor(p));
	}

	private <V> void addEditor(Property<V> property) {
		FormToolkit toolkit = getToolkit();
		Composite body = getBody();
		PropertyEditor<V> editor = createEditor(getBody(), new PropertyEditorContext<>(context, property));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite composite = editor.getComposite();
		composite.setLayoutData(layoutData);
		editors.add(editor);

		if (editor instanceof SimplePropertyEditor) {
			String name = editor.getDescriptiveName();
			boolean shortened = name.length() > 15;
			String text = shortened ? name.substring(0, 12) + "..." : name;
			Label label = toolkit.createLabel(body, text + ":");
			if(shortened) {
				label.setToolTipText(name);
			}
			label.setAlignment(SWT.RIGHT);
			label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
			label.moveAbove(composite);
			GridData labelLayoutData = new GridData(SWT.END, SWT.CENTER, false, false);
			label.setLayoutData(labelLayoutData);
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

		Label separator = toolkit.createSeparator(body, SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
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
