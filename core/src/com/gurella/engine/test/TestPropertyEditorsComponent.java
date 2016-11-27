package com.gurella.engine.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.model.ModelEditorContext;
import com.gurella.engine.editor.model.ModelEditorDescriptor;
import com.gurella.engine.editor.model.ModelEditorFactory;
import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorControlDecoration;
import com.gurella.engine.editor.ui.EditorControlDecoration.HorizontalAlignment;
import com.gurella.engine.editor.ui.EditorControlDecoration.VerticalAlignment;
import com.gurella.engine.editor.ui.EditorExpandableComposite;
import com.gurella.engine.editor.ui.EditorGraphicContex;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorLink;
import com.gurella.engine.editor.ui.EditorTable;
import com.gurella.engine.editor.ui.EditorTable.TableStyle;
import com.gurella.engine.editor.ui.EditorTableColumn;
import com.gurella.engine.editor.ui.EditorText;
import com.gurella.engine.editor.ui.EditorText.TextStyle;
import com.gurella.engine.editor.ui.EditorTree;
import com.gurella.engine.editor.ui.EditorTree.TreeContentProvider;
import com.gurella.engine.editor.ui.EditorTree.TreeStyle;
import com.gurella.engine.editor.ui.EditorTreeColumn;
import com.gurella.engine.editor.ui.EditorTreeItem;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.dialog.EditorDialog;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogActionListener;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogPart;
import com.gurella.engine.editor.ui.dialog.EditorDialog.EditorDialogProperties;
import com.gurella.engine.editor.ui.dialog.EditorTitleAreaDialog.EditorTitleAreaDialogProperties;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.viewer.EditorViewer.LabelProvider;
import com.gurella.engine.metatype.Model;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.scene.SceneNodeComponent2;
import com.gurella.engine.utils.GridRectangle;

public class TestPropertyEditorsComponent extends SceneNodeComponent2 {
	@PropertyEditorDescriptor(group = "assets")
	public Texture texture;
	@PropertyEditorDescriptor(group = "assets")
	public TextureAtlas textureAtlas;
	@PropertyEditorDescriptor(group = "assets")
	public Cubemap cubemap;

	@PropertyEditorDescriptor(group = "simple")
	public boolean bool;
	@PropertyEditorDescriptor(group = "simple")
	public Boolean objBool = Boolean.FALSE;
	@PropertyEditorDescriptor(group = "simple")
	public Boolean nullObjBool;
	@PropertyEditorDescriptor(group = "simple")
	public Color color = new Color();
	@PropertyEditorDescriptor(group = "simple")
	public final Color finalColor = new Color();
	@PropertyEditorDescriptor(group = "simple")
	public Color nullColor;
	@PropertyEditorDescriptor(group = "simple")
	public Date date = new Date();
	@PropertyEditorDescriptor(group = "simple")
	public Date nullDate;

	@PropertyEditorDescriptor(group = "gdx math")
	public Vector3 vector3;
	@PropertyEditorDescriptor(group = "gdx math")
	public Vector2 vector2;
	@PropertyEditorDescriptor(group = "gdx math")
	public GridPoint3 gridPoint3;
	@PropertyEditorDescriptor(group = "gdx math")
	public GridPoint2 gridPoint2;
	@PropertyEditorDescriptor(group = "gdx math")
	public Quaternion quaternion;

	@PropertyEditorDescriptor(group = "matrix")
	public Matrix3 matrix3 = new Matrix3();
	@PropertyEditorDescriptor(group = "matrix")
	public Matrix4 matrix4 = new Matrix4();

	@PropertyEditorDescriptor(group = "array")
	public String[] arrayString = new String[3];
	@PropertyEditorDescriptor(group = "array")
	public int[] arrayInt = new int[3];
	@PropertyEditorDescriptor(group = "array")
	public Integer[] arrayInteger = new Integer[3];
	@PropertyEditorDescriptor(group = "array")
	public Vector3[] arrayVector = new Vector3[3];

	@PropertyEditorDescriptor(group = "list")
	public List<String> listString = new ArrayList<String>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public final Array<Integer> garrayInteger = new Array<Integer>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Set<Integer> setInteger = new HashSet<Integer>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<int[]> arrayArrayInt = new Array<int[]>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<Integer[]> garrayArrayInteger = new Array<Integer[]>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<Integer[][]> garrayArray2dInteger = new Array<Integer[][]>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<Vector3> garrayVector3 = new Array<Vector3>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<Array<Integer>> garrayGarrayInteger = new Array<Array<Integer>>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<Array<? extends Vector<?>>> garrayGarrayVector = new Array<Array<? extends Vector<?>>>();
	@PropertyEditorDescriptor(group = "gdx Array")
	public Array<? extends Array<?>> garrayGarrayAny = new Array<Array<Object>>();

	@PropertyEditorDescriptor(group = "Custom Property Editor", factory = TestPropertyEditorFactory.class)
	public Object customPropertyEditor;

	@PropertyEditorDescriptor(group = "Custom Model Editor")
	public ModelEditorObject customModelEditor;

	static class TestPropertyEditorFactory implements PropertyEditorFactory<Object> {
		@Override
		public void buildUi(EditorComposite parent, PropertyEditorContext<Object> context) {
			EditorUi uiFactory = parent.getUiFactory();
			parent.setLayout(1);

			EditorButton check = uiFactory.createCheckBox(parent);
			check.setText("check");

			uiFactory.createLabel(parent, "Label");

			uiFactory.createSeparator(parent, false);

			EditorLink link = uiFactory.createLink(parent, "Link");
			link.addListener(EditorEventType.Selection, new LinkSelectedListener());

			EditorButton button = uiFactory.createButton(parent);
			button.setText("Button");
			button.addListener(EditorEventType.Selection, new ButtonSelectedListener());

			EditorButton dialogButton = uiFactory.createButton(parent);
			dialogButton.setText("Dialog");
			dialogButton.addListener(EditorEventType.Selection, new OpenDialogListener());

			EditorButton titleDialogButton = uiFactory.createButton(parent);
			titleDialogButton.setText("Title dialog");
			titleDialogButton.addListener(EditorEventType.Selection, new OpenTitleDialogListener());

			context.addMenuItem("Test menu item", new TestMenuRunnable(uiFactory));

			EditorText text = uiFactory.createText(parent, "test", new TextStyle().formBorder(true));
			// text.setBackground(230, 230, 230, 255);
			EditorControlDecoration decoration = text.getOrCreateDecoration(HorizontalAlignment.RIGHT,
					VerticalAlignment.CENTER);
			decoration.setInfoImage();
			decoration.setDescriptionText("Test decoration");

			EditorCombo<ViewerContent> combo = uiFactory.createCombo(parent);
			combo.setInput(Arrays.asList(ViewerContent.values()));
			combo.add(ViewerContent.item1);
			combo.setLabelProvider(new ViewerContentNameAndOrdinalLabelProvider());

			TableStyle style = new TableStyle().vScroll(true).hScroll(true).multiSelection(true).fullSelection(true);
			EditorTable<ViewerContent> table = uiFactory.createTable(parent, style);
			table.setHeaderVisible(true);
			EditorTableColumn<ViewerContent> column = table.createColumn();
			column.setText("name");
			column.setWidth(50);
			column.setResizable(true);
			column.setMoveable(true);
			column.setLabelProvider(new ViewerContentNameLabelProvider());

			column = table.createColumn();
			column.setText("ordinal");
			column.setWidth(50);
			column.setResizable(true);
			column.setMoveable(true);
			column.setLabelProvider(new ViewerContentOrdinalLabelProvider());

			table.setSize(100, 80);
			table.setInput(Arrays.asList(ViewerContent.values()));

			TreeStyle<String> tStyle = new TreeStyle<String>(new TestTreeContentProvider()).vScroll(true).hScroll(true)
					.multiSelection(true).fullSelection(true).formBorder(true);
			EditorTree<String> tree = uiFactory.createTree(parent, tStyle);

			EditorTreeColumn<String> treeColumn = tree.createColumn();
			treeColumn.setText("name");
			treeColumn.setWidth(50);
			treeColumn.setResizable(true);
			treeColumn.setMoveable(true);
			treeColumn.setLabelProvider(new TreeLabelProvider());

			treeColumn = tree.createColumn();
			treeColumn.setText("name 2");
			treeColumn.setWidth(50);
			treeColumn.setResizable(true);
			treeColumn.setMoveable(true);
			treeColumn.setLabelProvider(new TreeLabelProvider());

			List<String> rootItems = new ArrayList<String>();
			rootItems.add("Item 1 - ");
			rootItems.add("Item 2 - ");
			rootItems.add("Item 3 - ");
			tree.setInput(rootItems);

			tree.addListener(EditorEventType.Selection, new TreeSelectionListener(tree));

			EditorExpandableComposite expandableComposite = uiFactory.createExpandableComposite(parent);
			expandableComposite.setText("expandableComposite");
			EditorComposite client = uiFactory.createComposite(expandableComposite);
			client.setLayout(1);
			client.setSize(300, 100);
			uiFactory.createLabel(client, "client");
			expandableComposite.setClient(client);
			OpenDialogListener listener = new OpenDialogListener();
			expandableComposite.addListener(EditorEventType.Expand, listener);
			expandableComposite.removeListener(EditorEventType.Expand, listener);

			EditorComposite canvas = uiFactory.createComposite(parent);
			canvas.addListener(EditorEventType.Paint, new PaintCanvasListener());
			new EditorLayoutData().minSize(155, 155).sizeHint(155, 155).applyTo(canvas);
		}

		private final class TreeSelectionListener implements EditorEventListener {
			EditorTree<String> tree;

			public TreeSelectionListener(EditorTree<String> tree) {
				this.tree = tree;
			}

			@Override
			public void handleEvent(EditorEvent event) {
				List<String> selection = tree.getSelection();
				EditorTreeItem item = tree.getItem(0);
				event.getEditorUi().showInformationDialog("Info", selection.toString() + item.getText());
			}
		}
	}

	private static final class LinkSelectedListener implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			event.getEditorUi().showInformationDialog("Link Info", "Link clicked");
		}
	}

	private static final class ButtonSelectedListener implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			event.getEditorUi().showErrorDialog("ErrorDialog Test", "ErrorDialog Test",
					new RuntimeException("ErrorDialog Test"));
		}
	}

	private static final class OpenDialogListener implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			String s = new EditorDialogProperties(new DialogPart() {
				@Override
				public void init(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(300, 100);
				}
			}).tray(new DialogPart() {
				@Override
				public void init(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(80, 100);
				}
			}).action("Test action 1", new ActionListener()).action("Test action 2", true).show(event.getEditorUi());
			if (s != null) {
				event.getEditorUi().showInformationDialog("Info", "Info");
			}
		}
	}

	private static final class OpenTitleDialogListener implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			String s = new EditorTitleAreaDialogProperties(new DialogPart() {
				@Override
				public void init(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(300, 100);
				}
			}).tray(new DialogPart() {
				@Override
				public void init(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(80, 100);
				}
			}).action("Test action 1", new ActionListener()).action("Test action 2", true).title("Title")
					.message("Message").show(event.getEditorUi());
			if (s != null) {
				event.getEditorUi().showInformationDialog("Info", "Info");
			}
		}
	}

	private static class ActionListener implements DialogActionListener<String> {
		@Override
		public String handle(EditorDialog dialog) {
			return "String";
		}
	}

	private static final class TestMenuRunnable implements Runnable {
		private EditorUi uiFactory;

		public TestMenuRunnable(EditorUi uiFactory) {
			this.uiFactory = uiFactory;
		}

		@Override
		public void run() {
			uiFactory.showInformationDialog("Info", "Test menu item");
		}
	}

	public enum ViewerContent {
		item1, item2, item3;
	}

	private static final class ViewerContentNameAndOrdinalLabelProvider
			implements LabelProvider<TestPropertyEditorsComponent.ViewerContent> {
		@Override
		public String getText(ViewerContent element) {
			return element.name() + " " + element.ordinal();
		}

		@Override
		public EditorImage getImage(ViewerContent element) {
			return null;
		}
	}

	private static final class ViewerContentNameLabelProvider
			implements LabelProvider<TestPropertyEditorsComponent.ViewerContent> {
		@Override
		public String getText(ViewerContent element) {
			return element.name();
		}

		@Override
		public EditorImage getImage(ViewerContent element) {
			return null;
		}
	}

	private static final class ViewerContentOrdinalLabelProvider
			implements LabelProvider<TestPropertyEditorsComponent.ViewerContent> {
		@Override
		public String getText(ViewerContent element) {
			return String.valueOf(element.ordinal());
		}

		@Override
		public EditorImage getImage(ViewerContent element) {
			return null;
		}
	}

	private static class TestTreeContentProvider extends TreeContentProvider<String> {
		@Override
		public List<String> getChildren(String item, int depth) {
			if (depth > 3) {
				return null;
			}

			List<String> children = new ArrayList<String>();
			children.add("Item 1 - " + depth);
			children.add("Item 2 - " + depth);
			children.add("Item 3 - " + depth);
			return children;
		}
	}

	private static final class TreeLabelProvider implements LabelProvider<String> {
		@Override
		public String getText(String element) {
			return String.valueOf(element);
		}

		@Override
		public EditorImage getImage(String element) {
			return null;
		}
	}

	@ModelEditorDescriptor(factory = TestPropertyEditorModelFactory.class)
	private static class ModelEditorObject {
		@SuppressWarnings("unused")
		int testInt;
	}

	static class TestPropertyEditorModelFactory implements ModelEditorFactory<Object> {
		@Override
		public void buildUi(EditorComposite parent, ModelEditorContext<Object> context) {
			parent.setLayout(2);
			Model<Object> model = context.getModel();
			Property<Integer> property = model.getProperty("testInt");
			context.createPropertyLabel(parent, property);
			context.createPropertyEditor(parent, property);
		}
	}

	private static final class PaintCanvasListener implements EditorEventListener {
		Affine2 transform = new Affine2();

		@Override
		public void handleEvent(EditorEvent event) {
			EditorGraphicContex gc = event.getGraphicContex();
			gc.setAdvanced(true);
			gc.setBackground(255, 255, 255, 0);
			gc.setForeground(255, 0, 0, 255);
			gc.drawRectangle(20, 20, 100, 100);

			String text = "TEXT";
			gc.drawText(text, 40, 40, true);
			gc.getTransform(transform);
			transform.translate(5, 5);
			gc.setTransform(transform);
			gc.setForeground(0, 255, 0, 255);
			gc.drawText(text, 40, 40, true);

			GridPoint2 p = gc.stringExtent(text);
			int w = event.getWidth();
			int h = event.getHeight();
			transform.idt().translate(w / 2, h / 2).rotate(90);
			gc.setTransform(transform);
			GridRectangle r = ((EditorControl) event.getWidget()).getBounds();
			gc.drawString(text, r.x - (p.x / 3) * 2, r.y - p.y, true);
		}
	}

	public enum TestEnum {
		item1, item2, item3;
	}
}
