package com.gurella.engine.test;

import java.util.ArrayList;
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
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLink;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.dialog.EditorDialog;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogActionListener;
import com.gurella.engine.editor.ui.dialog.EditorDialog.DialogContentFactory;
import com.gurella.engine.editor.ui.dialog.EditorDialog.EditorDialogProperties;
import com.gurella.engine.editor.ui.dialog.EditorTitleAreaDialog.EditorTitleAteaDialogProperties;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
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

	@PropertyEditorDescriptor(factory = TestPropertyEditorFactory.class, complex = false)
	public Object testCustomEditor;

	static class TestPropertyEditorFactory implements PropertyEditorFactory<Byte> {
		@Override
		public void buildUi(EditorComposite parent, PropertyEditorContext<Byte> context) {
			EditorUi uiFactory = parent.getUiFactory();

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
}
