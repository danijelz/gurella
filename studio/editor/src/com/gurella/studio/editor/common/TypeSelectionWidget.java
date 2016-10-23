package com.gurella.studio.editor.common;

import static org.eclipse.swt.SWT.NONE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.event.Signal1;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.TypeSelectionUtils;
import com.gurella.studio.editor.utils.UiUtils;

//TODO unused
public class TypeSelectionWidget<T> extends Composite {
	private final SceneEditorContext context;
	private final Class<T> baseType;
	private final List<Class<? extends T>> knownTypes = new ArrayList<>();

	private Combo typesCombo;
	private ComboViewer viewer;
	private Label menuButton;
	private Image menuImage;

	private Signal1<Class<? extends T>> typeSelectionListener = new Signal1<>();

	private Class<? extends T> selectedType;

	public TypeSelectionWidget(Composite parent, SceneEditorContext context, Class<T> baseType,
			List<Class<? extends T>> knownTypes) {
		super(parent, SWT.NONE);
		this.context = context;
		this.baseType = baseType;
		this.knownTypes.addAll(knownTypes);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(this);
		addListener(SWT.MouseEnter, e -> showMenuImage(true));
		addListener(SWT.MouseExit, e -> showMenuImage(false));

		typesCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		typesCombo.addListener(SWT.MouseEnter, e -> showMenuImage(true));
		typesCombo.addListener(SWT.MouseExit, e -> showMenuImage(false));
		GridDataFactory.defaultsFor(typesCombo).align(SWT.BEGINNING, SWT.CENTER).hint(100, 14).applyTo(typesCombo);
		UiUtils.adapt(typesCombo);

		viewer = new ComboViewer(typesCombo);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new TypeLabelProvider());
		viewer.addSelectionChangedListener(e -> updateType(e.getSelection()));

		menuButton = GurellaStudioPlugin.getToolkit().createLabel(this, "     ", NONE);
		menuButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
		menuButton.addListener(SWT.MouseUp, e -> selectType());
		GridDataFactory.defaultsFor(menuButton).align(SWT.BEGINNING, SWT.BEGINNING).hint(100, 14).applyTo(menuButton);
		menuImage = GurellaStudioPlugin.createImage("icons/menu.png");

		UiUtils.paintBordersFor(this);
		UiUtils.adapt(this);
	}

	private void selectType() {
		Class<? extends T> selected = TypeSelectionUtils.selectType(context, baseType);
		if (selected != null) {
			updateType(selected);
		}
	}

	private void updateType(ISelection selection) {
		StructuredSelection structuredSelection = (StructuredSelection) selection;
		@SuppressWarnings("unchecked")
		Class<? extends T> selected = (Class<? extends T>) structuredSelection.getFirstElement();
		updateType(selected);
	}

	private void updateType(Class<? extends T> selected) {
		selectedType = selected;
		typeSelectionListener.dispatch(selectedType);
	}

	public void showMenuImage(boolean show) {
		if (show) {
			menuButton.setImage(menuImage);
		} else {
			menuButton.setImage(null);
		}
	}

	private static class TypeLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return element == null ? "" : ((Class<?>) element).getSimpleName();
		}
	}
}
