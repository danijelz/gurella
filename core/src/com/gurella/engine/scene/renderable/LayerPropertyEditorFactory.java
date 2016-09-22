package com.gurella.engine.scene.renderable;

import static com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment.LEFT;
import static com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment.CENTER;

import java.util.Arrays;
import java.util.List;

import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorText;
import com.gurella.engine.editor.ui.EditorText.TextStyle;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.dialog.EditorDialog;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogActionListener;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogPart;
import com.gurella.engine.editor.ui.dialog.EditorDialog.EditorDialogProperties;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.viewer.EditorViewer.LabelProvider;
import com.gurella.engine.utils.Values;

public class LayerPropertyEditorFactory implements PropertyEditorFactory<Layer> {
	private static final LayerLabelProvider labelProvider = new LayerLabelProvider();

	@Override
	public void buildUi(EditorComposite parent, PropertyEditorContext<Layer> context) {
		parent.setLayout(2);

		final EditorUi uiFactory = context.getEditorUi();

		EditorCombo<Layer> combo = uiFactory.createCombo(parent);
		new EditorLayoutData().alignment(LEFT, CENTER).grab(false, false).applyTo(combo);
		combo.setLabelProvider(labelProvider);
		combo.addListener(EditorEventType.Selection, new LayerSelectionListener(context, combo));
		setComboInput(combo);

		EditorButton addButton = uiFactory.createButton(parent);
		new EditorLayoutData().alignment(LEFT, CENTER).horizontalIndent(5).applyTo(addButton);
		addButton.setText("Add");
		addButton.addListener(EditorEventType.Selection, new AddButtonSelectionListener(context, combo));

		Layer value = context.getPropertyValue();
		if (value == null) {
			combo.clearSelection();
		} else {
			combo.setSelection(value);
		}
	}

	private static void setComboInput(EditorCombo<Layer> combo) {
		Layer[] allLayers = Layer.values().toArray(Layer.class);
		Arrays.sort(allLayers);
		combo.setInput(allLayers);
	}

	private static final class LayerSelectionListener implements EditorEventListener {
		private PropertyEditorContext<Layer> context;
		private EditorCombo<Layer> combo;

		public LayerSelectionListener(PropertyEditorContext<Layer> context, EditorCombo<Layer> combo) {
			this.context = context;
			this.combo = combo;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			List<Layer> selection = combo.getSelection();
			context.setPropertyValue(Values.isEmpty(selection) ? null : selection.get(0));
		}
	}

	private static final class LayerLabelProvider implements LabelProvider<Layer> {
		@Override
		public String getText(Layer element) {
			return Integer.toString(element.ordinal) + " - " + element.name;
		}

		@Override
		public EditorImage getImage(Layer element) {
			return null;
		}
	}

	private static final class AddButtonSelectionListener implements EditorEventListener {
		private final PropertyEditorContext<Layer> context;
		private final EditorCombo<Layer> combo;

		public AddButtonSelectionListener(PropertyEditorContext<Layer> context, EditorCombo<Layer> combo) {
			this.context = context;
			this.combo = combo;
		}

		@Override
		public void handleEvent(EditorEvent event) {
			Layer layer = new EditorDialogProperties(new AddLayerContent()).action("Ok", new AddLayerConfirmListener())
					.action("Cancle").show(event.getEditorUi());
			if (layer != null) {
				setComboInput(combo);
				combo.setSelection(layer);
				context.setPropertyValue(layer);
			}
		}
	}

	private static class AddLayerConfirmListener implements DialogActionListener<Layer> {
		@Override
		public Layer handle(EditorDialog dialog) {
			AddLayerContent content = (AddLayerContent) dialog.getContent();
			return content.getLayer();
		}
	}

	private static class AddLayerContent implements DialogPart {
		private EditorUi uiFactory;
		private EditorText ordinalText;
		private EditorText nameText;

		@Override
		public void init(EditorDialog dialog, EditorComposite parent) {
			uiFactory = parent.getUiFactory();
			parent.setLayout(2);
			uiFactory.createLabel(parent, "Ordinal:");
			ordinalText = uiFactory.createText(parent, "", new TextStyle().formBorder(true));
			ordinalText.addListener(EditorEventType.Verify, new OrdinalValidator());
			uiFactory.createLabel(parent, "Name:");
			nameText = uiFactory.createText(parent, "", new TextStyle().formBorder(true));
			nameText.addListener(EditorEventType.Verify, new NameValidator());
		}

		public Layer getLayer() {
			try {
				int ordinal = Integer.parseInt(ordinalText.getText());
				if (Layer.valueOf(ordinal) != null) {
					uiFactory.showInformationDialog("Layer allready exists",
							"Layer with ordinal " + ordinal + " allready exists.");
				}
				String name = nameText.getText();
				if (Layer.valueOf(name) != null) {
					uiFactory.showInformationDialog("Layer allready exists",
							"Layer with name '" + name + "' allready exists.");
				}

				return Layer.valueOf(ordinal, name);
			} catch (Exception e) {
				uiFactory.showErrorDialog("Error", "Error while creating new Layer", e);
				return null;
			}
		}
	}

	private static class OrdinalValidator implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			try {
				String newText = getNewText(event, ((EditorText) event.getWidget()).getText());
				if (newText.length() > 0) {
					Integer.parseInt(newText);
				}
			} catch (Exception e2) {
				event.setDoit(false);
			}
		}
	}

	private static class NameValidator implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			String newText = getNewText(event, ((EditorText) event.getWidget()).getText());
			if (newText.length() < 1) {
				Integer.parseInt(newText);
				event.setDoit(false);
			}
		}
	}

	private static String getNewText(EditorEvent e, String oldValue) {
		return oldValue.substring(0, e.getStart()) + e.getText() + oldValue.substring(e.getEnd());
	}
}
