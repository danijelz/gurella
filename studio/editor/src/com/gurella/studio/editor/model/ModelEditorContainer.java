package com.gurella.studio.editor.model;

import static com.gurella.studio.editor.GurellaStudioPlugin.createFont;
import static com.gurella.studio.editor.GurellaStudioPlugin.getToolkit;
import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TITLE_BAR;
import static org.eclipse.ui.forms.widgets.ExpandableComposite.TWISTIE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.property.ModelEditorContext;
import com.gurella.studio.editor.model.property.PropertyEditor;
import com.gurella.studio.editor.model.property.PropertyEditorContext;
import com.gurella.studio.editor.model.property.SimplePropertyEditor;

public class ModelEditorContainer<T> extends ScrolledForm {
	private ModelEditorContext<T> context;
	private List<PropertyEditor<?>> editors = new ArrayList<>();

	private PropertyEditor<?> hoverEditor;

	public ModelEditorContainer(Composite parent, T modelInstance) {
		this(parent, new ModelEditorContext<>(modelInstance));
	}

	public ModelEditorContainer(Composite parent, ModelEditorContext<T> context) {
		super(parent, SWT.NONE);
		this.context = context;
		setExpandHorizontal(true);
		GurellaStudioPlugin.getToolkit().adapt(this);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		getBody().setLayout(layout);
		initEditors();
		layout(true, true);
		Listener mouseMoveListener = (e) -> mouseMoved(e);
		Display display = getDisplay();
		display.addFilter(SWT.MouseMove, mouseMoveListener);
		addListener(SWT.Dispose, (e) -> display.removeFilter(SWT.MouseMove, mouseMoveListener));
	}

	private void initEditors() {
		ImmutableArray<Property<?>> properties = context.model.getProperties();

		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			if (property.isEditable()) {

				addEditor(property);
			}
		}

		layout(true, true);
	}

	private <V> void addEditor(Property<V> property) {
		FormToolkit toolkit = getToolkit();
		Composite body = getBody();
		PropertyEditor<V> editor = createEditor(getBody(),
				new PropertyEditorContext<>(context, context.model, context.modelInstance, property));
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite composite = editor.getComposite();
		composite.setLayoutData(layoutData);
		composite.pack();
		editors.add(editor);

		if (editor instanceof SimplePropertyEditor) {
			Label label = toolkit.createLabel(body, editor.getDescriptiveName() + ":");
			label.setAlignment(SWT.RIGHT);
			label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
			label.setFont(createFont(FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD)));
			label.moveAbove(composite);
		} else {
			Section componentSection = toolkit.createSection(body, TWISTIE | TITLE_BAR);
			componentSection.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
			componentSection.setText(editor.getDescriptiveName());
			composite.setParent(componentSection);
			componentSection.setClient(composite);
			composite.layout(true, true);
			componentSection.setExpanded(true);
		}

		Label separator = toolkit.createSeparator(body, SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	protected void mouseMoved(Event event) {
		Point location = getDisplay().getCursorLocation();
		PropertyEditor<?> editor = getPropertyEditor(location.x, location.y);
		if (editor == hoverEditor) {
			return;
		}

		if (hoverEditor != null) {
			hoverEditor.setHover(false);
		}

		hoverEditor = editor;
		if (editor != null) {
			editor.setHover(true);
		}

	}

	private PropertyEditor<?> getPropertyEditor(int x, int y) {
		if (!getBody().getBounds().contains(getBody().toControl(x, y))) {
			return null;
		}

		return editors.stream().filter(e -> e.getComposite().getBounds().contains(e.getComposite().toControl(x, y)))
				.findFirst().orElse(null);
	}
}
