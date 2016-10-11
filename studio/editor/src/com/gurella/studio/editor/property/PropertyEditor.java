package com.gurella.studio.editor.property;

import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

public abstract class PropertyEditor<P> {
	private Composite composite;
	protected Composite body;
	private Label menuButton;
	private Image menuImage;

	private Map<String, Runnable> menuItems = new HashMap<>();
	private List<String> menuItemsOrder = new ArrayList<>();

	protected PropertyEditorContext<?, P> context;

	public PropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		this.context = context;

		FormToolkit toolkit = getToolkit();
		composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setData(PropertyEditor.class.getName(), this);

		body = new BodyComposite(composite, SWT.NULL);
		body.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		body.addListener(SWT.MouseUp, e -> showMenuOnMouseUp(e));

		menuImage = GurellaStudioPlugin.createImage("icons/popup_menu.gif");
	}

	public Composite getComposite() {
		return composite;
	}

	public Composite getBody() {
		return body;
	}

	protected FormToolkit getToolkit() {
		return GurellaStudioPlugin.getToolkit();
	}

	public PropertyEditorContext<?, P> getContext() {
		return context;
	}

	public String getDescriptiveName() {
		return context.property.getDescriptiveName();
	}

	public Property<P> getProperty() {
		return context.property;
	}

	protected Object getModelInstance() {
		return context.modelInstance;
	}

	protected P getValue() {
		return context.getValue();
	}

	public void setValue(P value) {
		SetPropertyValueOperation<P> operation = new SetPropertyValueOperation<>(this, getValue(), value);
		try {
			context.sceneEditorContext.operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setHover(boolean hover) {
		if (menuButton == null || menuButton.isDisposed()) {
			return;
		}

		if (hover && !menuItems.isEmpty()) {
			menuButton.setImage(menuImage);
		} else {
			menuButton.setImage(null);
		}
	}

	public void addMenuItem(String text, Runnable action) {
		if (action == null) {
			return;
		}

		if (menuItems.put(text, action) != null) {
			menuItemsOrder.remove(text);
		}

		menuItemsOrder.add(text);
		updateMenu();
	}

	public void removeMenuItem(String text) {
		menuItems.remove(text);
		menuItemsOrder.remove(text);
		updateMenu();
	}

	private void updateMenu() {
		boolean empty = menuItems.isEmpty();
		if (empty && menuButton != null) {
			((GridLayout) composite.getLayout()).numColumns = 1;
			menuButton.dispose();
			menuButton = null;
			composite.layout(true, true);
		} else if (!empty && menuButton == null) {
			((GridLayout) composite.getLayout()).numColumns = 2;
			menuButton = getToolkit().createLabel(composite, "     ", NONE);
			menuButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
			menuButton.addListener(SWT.MouseUp, (e) -> showMenu());
			composite.layout(true, true);
		}
	}

	protected void showMenu() {
		if (menuItems.isEmpty()) {
			return;
		}

		Menu menu = new Menu(composite.getShell(), POP_UP);
		menuItemsOrder.forEach(text -> addMenuAction(menu, text, menuItems.get(text)));
		menu.setLocation(composite.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private static void addMenuAction(Menu menu, String text, Runnable action) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText(text);
		item1.addListener(SWT.Selection, e -> action.run());
	}

	public void showMenuOnMouseUp(Event e) {
		if (e.type == SWT.MouseUp && e.button == 3) {
			showMenu();
		}
	}

	protected abstract void updateValue(P value);

	private static final class BodyComposite extends Composite {
		private BodyComposite(Composite parent, int style) {
			super(parent, style);
			UiUtils.adapt(this);
		}

		@Override
		public void layout(boolean changed, boolean all) {
			super.layout(changed, all);
			reflow();
		}

		private void reflow() {
			Composite temp = this;
			while (temp != null) {
				temp = temp.getParent();
				if (temp instanceof ScrolledForm) {
					((ScrolledForm) temp).reflow(true);
					return;
				}
			}
		}
	}

	private static class SetPropertyValueOperation<P> extends AbstractOperation {
		final PropertyEditor<P> editor;
		final P oldValue;
		final P newValue;

		public SetPropertyValueOperation(PropertyEditor<P> editor, P oldValue, P newValue) {
			super("Property");
			this.editor = editor;
			this.oldValue = new CopyContext().copy(oldValue);
			this.newValue = newValue;
			addContext(editor.getContext().sceneEditorContext.undoContext);
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			editor.context.setValue(newValue);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			editor.context.setValue(newValue);
			if(!editor.composite.isDisposed()) {
				editor.updateValue(newValue);
			}
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			editor.context.setValue(oldValue);
			if(!editor.composite.isDisposed()) {
				editor.updateValue(oldValue);
			}
			return Status.OK_STATUS;
		}
	}
}
