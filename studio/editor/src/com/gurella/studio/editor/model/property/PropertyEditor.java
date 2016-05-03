package com.gurella.studio.editor.model.property;

import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;

import java.util.HashMap;
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

import com.gurella.engine.base.model.Property;
import com.gurella.studio.GurellaStudioPlugin;

public abstract class PropertyEditor<P> {
	private Composite composite;
	protected Composite body;
	private Label menuButton;
	private Image menuImage;

	private Map<String, Runnable> menuItems = new HashMap<>();

	protected PropertyEditorContext<?, P> context;

	public PropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		this.context = context;

		FormToolkit toolkit = getToolkit();
		composite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		body = toolkit.createComposite(composite);
		body.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		menuImage = GurellaStudioPlugin.createImage("icons/popup_menu.gif");

		if (context.property.isNullable() && getValue() != null) {
			addMenuItem("Set null", () -> setValue(null));
		}
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
		if (menuButton == null) {
			return;
		}

		if (hover && !menuItems.isEmpty()) {
			menuButton.setImage(menuImage);
		} else {
			menuButton.setImage(null);
		}
	}

	public void addMenuItem(String text, Runnable action) {
		menuItems.put(text, action);
		updateMenu();
	}

	public void removeMenuItem(String text) {
		menuItems.remove(text);
		updateMenu();
	}

	private void updateMenu() {
		boolean empty = menuItems.isEmpty();
		if (empty && menuButton != null) {
			((GridLayout) composite.getLayout()).numColumns = 1;
			menuButton.dispose();
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
		menuItems.forEach((text, action) -> addMenuAction(menu, text, action));
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
}
