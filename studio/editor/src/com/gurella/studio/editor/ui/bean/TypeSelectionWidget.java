package com.gurella.studio.editor.ui.bean;

import static org.eclipse.swt.SWT.NONE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.event.Signal1;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.SceneEditorContext;
import com.gurella.studio.editor.utils.TypeSelectionUtils;
import com.gurella.studio.editor.utils.UiUtils;

class TypeSelectionWidget<T> extends Composite {
	private final SceneEditorContext context;
	private final Class<T> baseType;
	private final List<Class<? extends T>> knownTypes = new ArrayList<>();

	private Label infoLabel;
	private Image menuImage;
	private Label menuButton;

	private Signal1<Class<? extends T>> selectionSignal = new Signal1<>();

	private Class<? extends T> selectedType;

	TypeSelectionWidget(Composite parent, SceneEditorContext context, Class<T> baseType, Class<? extends T> selected) {
		this(parent, context, baseType, selected, Collections.emptyList());
	}

	TypeSelectionWidget(Composite parent, SceneEditorContext context, Class<T> baseType, Class<? extends T> selected,
			List<Class<? extends T>> knownTypes) {
		super(parent, SWT.NONE);

		this.context = context;
		this.baseType = baseType;
		this.knownTypes.addAll(knownTypes);
		GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).spacing(0, 2).applyTo(this);
		addListener(SWT.MouseEnter, e -> showMenuImage(true));
		addListener(SWT.MouseExit, e -> showMenuImage(false));

		infoLabel = UiUtils.createLabel(this, "");
		infoLabel.setAlignment(SWT.LEFT);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(100, 18).applyTo(infoLabel);
		infoLabel.addListener(SWT.MouseEnter, e -> showMenuImage(true));
		infoLabel.addListener(SWT.MouseExit, e -> showMenuImage(false));
		infoLabel.setText(selected == null ? "null" : selected.getSimpleName());

		menuImage = GurellaStudioPlugin.createImage("icons/menu.png");

		menuButton = GurellaStudioPlugin.getToolkit().createLabel(this, "     ", NONE);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).indent(10, 0).hint(25, 14).applyTo(menuButton);
		menuButton.addListener(SWT.MouseEnter, e -> showMenuImage(true));
		menuButton.addListener(SWT.MouseUp, e -> selectType());
		menuButton.addListener(SWT.MouseExit, e -> showMenuImage(false));

		UiUtils.paintBordersFor(this);
		UiUtils.adapt(this);
	}

	private void selectType() {
		IJavaProject javaProject = context.javaProject;
		Optional.ofNullable(TypeSelectionUtils.<T> selectType(javaProject, baseType)).ifPresent(t -> updateType(t));
	}

	private void updateType(Class<? extends T> selected) {
		selectedType = selected;
		infoLabel.setText(selected == null ? "null" : selected.getSimpleName());
		selectionSignal.dispatch(selectedType);
	}

	public void addSelectionListener(Listener1<Class<? extends T>> listener) {
		selectionSignal.addListener(listener);
	}

	public void removeSelectionListener(Listener1<Class<? extends T>> listener) {
		selectionSignal.removeListener(listener);
	}

	public void showMenuImage(boolean show) {
		if (show) {
			menuButton.setImage(menuImage);
		} else {
			menuButton.setImage(null);
		}
	}
}
