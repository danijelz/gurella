package com.gurella.engine.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControlDecoration;
import com.gurella.engine.editor.ui.EditorControlDecoration.HorizontalAlignment;
import com.gurella.engine.editor.ui.EditorControlDecoration.VerticalAlignment;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorLink;
import com.gurella.engine.editor.ui.EditorTable;
import com.gurella.engine.editor.ui.EditorTable.TableStyle;
import com.gurella.engine.editor.ui.EditorTableColumn;
import com.gurella.engine.editor.ui.EditorText;
import com.gurella.engine.editor.ui.EditorTree;
import com.gurella.engine.editor.ui.EditorTree.TreeContentProvider;
import com.gurella.engine.editor.ui.EditorTree.TreeStyle;
import com.gurella.engine.editor.ui.EditorTreeColumn;
import com.gurella.engine.editor.ui.EditorTreeItem;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.dialog.EditorDialog;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogActionListener;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogContentFactory;
import com.gurella.engine.editor.ui.dialog.EditorDialog.EditorDialogProperties;
import com.gurella.engine.editor.ui.dialog.EditorTitleAreaDialog.EditorTitleAteaDialogProperties;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.editor.ui.viewer.EditorViewer.LabelProvider;
import com.gurella.engine.scene.SceneNodeComponent2;

public class TestPropertyEditorsComponnent extends SceneNodeComponent2 {
	public Date date;
	public List<String> list = new ArrayList<String>();
	public Array<Integer> arr = new Array<Integer>();
	public Set<Integer> set = new HashSet<Integer>();
	public Array<int[]> arrIntArr = new Array<int[]>();
	public Array<Integer[]> arrIntegerArr = new Array<Integer[]>();
	public Array<Integer[][]> arrIntegerArrArr = new Array<Integer[][]>();
	public Array<Vector3> arrVec = new Array<Vector3>();
	public Array<Array<Integer>> arrArrInt = new Array<Array<Integer>>();
	public Array<Array<? extends Vector<?>>> arrArrVec = new Array<Array<? extends Vector<?>>>();
	public Array<? extends Array<?>> arrAnyArr = new Array<Array<Object>>();

	public Matrix3 matrix3 = new Matrix3();
	public Matrix4 matrix4 = new Matrix4();
	public Vector3 testVector;
	public Texture texture;

	public String[] testStringArray = new String[3];
	public int[] testIntArray = new int[3];
	public Integer[] testIntegerArray = new Integer[3];
	public Vector3[] testVectorArray = new Vector3[3];

	@PropertyEditorDescriptor(factory = TestPropertyEditorFactory.class, complex = true)
	public Object testCustomEditor;

	static class TestPropertyEditorFactory implements PropertyEditorFactory<Byte> {
		@Override
		public void buildUi(EditorComposite parent, PropertyEditorContext<Byte> context) {
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
			dialogButton.addListener(EditorEventType.Selection, new OpenDialogListenerListener());

			EditorButton titleDialogButton = uiFactory.createButton(parent);
			titleDialogButton.setText("Title dialog");
			titleDialogButton.addListener(EditorEventType.Selection, new OpenTitleDialogListenerListener());

			context.addMenuItem("Test menu item", new TestMenuRunnable(uiFactory));

			EditorText text = uiFactory.createText(parent);
			text.setBackground(0, 0, 100, 255);
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
					.multiSelection(true).fullSelection(true);
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

	private static final class OpenDialogListenerListener implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			String s = new EditorDialogProperties(new DialogContentFactory() {
				@Override
				public void createContent(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(300, 100);
				}
			}).trayFactory(new DialogContentFactory() {
				@Override
				public void createContent(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(80, 100);
				}
			}).action("Test action 1", new ActListener()).action("Test action 2", true).show(event.getEditorUi());
			if (s != null) {
				event.getEditorUi().showInformationDialog("Info", "Info");
			}
		}
	}

	private static final class OpenTitleDialogListenerListener implements EditorEventListener {
		@Override
		public void handleEvent(EditorEvent event) {
			String s = new EditorTitleAteaDialogProperties(new DialogContentFactory() {
				@Override
				public void createContent(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(300, 100);
				}
			}).trayFactory(new DialogContentFactory() {
				@Override
				public void createContent(EditorDialog dialog, EditorComposite parent) {
					EditorComposite composite = parent.getUiFactory().createComposite(parent);
					composite.setSize(80, 100);
				}
			}).action("Test action 1", new ActListener()).action("Test action 2", true).title("Title")
					.message("Message").show(event.getEditorUi());
			if (s != null) {
				event.getEditorUi().showInformationDialog("Info", "Info");
			}
		}
	}

	private static class ActListener implements DialogActionListener<String> {
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
			implements LabelProvider<TestPropertyEditorsComponnent.ViewerContent> {
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
			implements LabelProvider<TestPropertyEditorsComponnent.ViewerContent> {
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
			implements LabelProvider<TestPropertyEditorsComponnent.ViewerContent> {
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
}
