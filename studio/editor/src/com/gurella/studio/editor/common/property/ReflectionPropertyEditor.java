package com.gurella.studio.editor.common.property;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.studio.GurellaStudioPlugin.showError;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.metatype.DefaultMetaType.SimpleMetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.common.bean.BeanEditor;
import com.gurella.studio.editor.common.bean.BeanEditorFactory;
import com.gurella.studio.editor.utils.TypeSelectionUtils;
import com.gurella.studio.editor.utils.UiUtils;

public class ReflectionPropertyEditor<P> extends CompositePropertyEditor<P> {
	public ReflectionPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Select type", () -> selectType());

			Class<P> type = context.getPropertyType();
			if (!Modifier.isAbstract(type.getModifiers()) && Reflection.getDeclaredConstructorSilently(type) != null) {
				addMenuItem("New " + type.getSimpleName(), () -> newTypeInstance());
			}

			if (context.isNullable()) {
				addMenuItem("Set null", () -> setNull());
			}
		}
	}

	private void newTypeInstance() {
		try {
			ClassLoader classLoader = context.sceneContext.classLoader;
			Class<?> valueClass = classLoader.loadClass(context.getPropertyType().getName());
			Constructor<?> constructor = valueClass.getDeclaredConstructor(new Class[0]);
			constructor.setAccessible(true);
			P value = Values.cast(constructor.newInstance(new Object[0]));
			setValue(value);
			rebuildUi();
		} catch (Exception e) {
			String message = "Error occurred while creating value";
			GurellaStudioPlugin.showError(e, message);
		}
	}

	private void buildUi() {
		FormToolkit toolkit = getToolkit();
		P value = getValue();
		if (value == null) {
			Label label = toolkit.createLabel(content, "null (" + context.getPropertyType().getSimpleName() + ")");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else if (MetaTypes.getMetaType(value.getClass()) instanceof SimpleMetaType) {
			PropertyEditorContext<Object, P> casted = cast(context);
			PropertyEditorContext<Object, P> child = new PropertyEditorContext<>(casted, casted.property);
			PropertyEditor<P> editor = PropertyEditorFactory.createEditor(content, child, cast(value.getClass()));
			GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			editor.getBody().setLayoutData(layoutData);
		} else {
			BeanEditor<P> beanEditor = BeanEditorFactory.createEditor(content, context, value);
			beanEditor.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		}
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true, true);
		content.redraw();
	}

	private void setNull() {
		setValue(null);
		rebuildUi();
	}

	private void selectType() {
		try {
			selectTypeSafely();
		} catch (Exception e) {
			showError(e, "Error occurred while creating value");
		}
	}

	private void selectTypeSafely() throws InstantiationException, IllegalAccessException {
		Class<P> propertyType = context.getPropertyType();
		Class<? extends P> selected = TypeSelectionUtils.selectType(context.sceneContext, propertyType);
		if (selected != null) {
			P value = selected.newInstance();
			setValue(value);
			rebuildUi();
		}
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}
}
