package com.gurella.studio.editor.model.property;

import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

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

	protected void setValue(P value) {
		context.setValue(value);
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

	private void showMenu() {
		if (menuItems.isEmpty()) {
			return;
		}

		Menu menu = new Menu(composite.getShell(), POP_UP);
		menuItemsOrder.forEach(text -> addMenuAction(menu, text, menuItems.get(text)));
		Point loc = menuButton.getLocation();
		Rectangle rect = menuButton.getBounds();
		Point mLoc = new Point(loc.x - 1, loc.y + rect.height);
		menu.setLocation(composite.getDisplay().map(menuButton.getParent(), null, mLoc));
		menu.setVisible(true);
	}

	private static void addMenuAction(Menu menu, String text, Runnable action) {
		MenuItem item1 = new MenuItem(menu, PUSH);
		item1.setText(text);
		item1.addListener(SWT.Selection, e -> action.run());
	}

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
}
