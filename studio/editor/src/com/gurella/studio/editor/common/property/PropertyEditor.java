package com.gurella.studio.editor.common.property;

import static com.gurella.engine.event.EventService.post;
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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.event.EventService;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.subscription.PropertyChangeListener;
import com.gurella.studio.editor.utils.UiUtils;

public abstract class PropertyEditor<P> implements PropertyChangeListener {
	private Composite body;
	protected Composite content;
	private Label menuButton;
	private Image menuImage;

	private Map<String, Runnable> menuItems = new HashMap<>();
	private List<String> menuItemsOrder = new ArrayList<>();

	protected PropertyEditorContext<?, P> context;

	public PropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		this.context = context;

		FormToolkit toolkit = getToolkit();
		body = toolkit.createComposite(parent);
		GridLayoutFactory.swtDefaults().spacing(0, 0).margins(0, 0).applyTo(body);
		body.setData(PropertyEditor.class.getName(), this);

		content = new Composite(body, SWT.NULL);
		content.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		UiUtils.adapt(content);

		menuImage = GurellaStudioPlugin.getImage("icons/menu.png");

		int editorId = context.sceneContext.editorId;
		body.addDisposeListener(e -> EventService.unsubscribe(editorId, this));
		EventService.subscribe(editorId, this);
	}

	public Composite getBody() {
		return body;
	}

	public Composite getContent() {
		return content;
	}

	protected FormToolkit getToolkit() {
		return GurellaStudioPlugin.getToolkit();
	}

	public PropertyEditorContext<?, P> getContext() {
		return context;
	}

	public String getDescriptiveName() {
		return PropertyEditorData.getDescriptiveName(context);
	}

	public Property<P> getProperty() {
		return context.property;
	}

	protected Object getModelInstance() {
		return context.bean;
	}

	protected P getValue() {
		return context.getValue();
	}

	public void setValue(P value) {
		P oldValue = getValue();
		if (Values.isNotEqual(oldValue, value, true)) {
			SetPropertyValueOperation<P> operation = new SetPropertyValueOperation<>(context, oldValue, value);
			context.sceneContext.executeOperation(operation, "Error updating property.");
		}
	}

	@Override
	public void propertyChanged(Object instance, Property<?> property, Object newValue) {
		if (context.bean != instance || context.property != property || body.isDisposed()) {
			return;
		}
		updateValue(Values.cast(newValue));
	}

	public void setMenuVisible(boolean visible) {
		if (menuButton == null || menuButton.isDisposed()) {
			return;
		}

		if (visible && !menuItems.isEmpty()) {
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
			((GridLayout) body.getLayout()).numColumns = 1;
			menuButton.dispose();
			menuButton = null;
			body.layout(true, true);
		} else if (!empty && menuButton == null) {
			((GridLayout) body.getLayout()).numColumns = 2;
			menuButton = getToolkit().createLabel(body, "     ", NONE);
			menuButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
			menuButton.addListener(SWT.MouseUp, e -> showMenu());
			body.layout(true, true);
		}
	}

	public void showMenu() {
		if (menuItems.isEmpty()) {
			return;
		}

		Menu menu = new Menu(body.getShell(), POP_UP);
		menuItemsOrder.forEach(text -> addMenuAction(menu, text, menuItems.get(text)));
		menu.setLocation(body.getDisplay().getCursorLocation());
		menu.setVisible(true);
	}

	private static void addMenuAction(Menu menu, String text, Runnable action) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText(text);
		item1.addListener(SWT.Selection, e -> action.run());
	}

	protected abstract void updateValue(P value);

	private static class SetPropertyValueOperation<P> extends AbstractOperation {
		final PropertyEditorContext<?, P> context;
		final P oldValue;
		final P newValue;

		public SetPropertyValueOperation(PropertyEditorContext<?, P> context, P oldValue, P newValue) {
			super("Property");
			this.context = context;
			this.oldValue = Assets.isAsset(oldValue) ? oldValue : CopyContext.copyObject(oldValue);
			this.newValue = newValue;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			context.setValue(newValue);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			context.setValue(oldValue);
			int editorId = context.sceneContext.editorId;
			Object instance = context.bean;
			Property<?> property = context.property;
			post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(instance, property, oldValue));
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable adaptable) throws ExecutionException {
			context.setValue(newValue);
			int editorId = context.sceneContext.editorId;
			Object instance = context.bean;
			Property<?> property = context.property;
			post(editorId, PropertyChangeListener.class, l -> l.propertyChanged(instance, property, newValue));
			return Status.OK_STATUS;
		}
	}
}
