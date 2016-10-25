package com.gurella.studio.editor;

import static org.eclipse.swt.SWT.CHECK;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class ContextMenuActions {
	private Map<String, MenuGroup> groups = new HashMap<>();

	public ContextMenuActions() {
		addGroup("", Integer.MIN_VALUE);
	}

	public void addGroup(String name) {
		addGroup(name, 0);
	}

	public void addGroup(String name, int priority) {
		if (!groups.containsKey(name)) {
			groups.put(name, new MenuGroup(name, priority));
		}
	}

	public void addAction(String name, Runnable action) {
		addAction("", name, 0, true, action);
	}

	public void addAction(String group, String name, Runnable action) {
		addAction(group, name, 0, true, action);
	}

	public void addAction(String name, int priority, Runnable action) {
		addAction("", name, priority, true, action);
	}

	public void addAction(String group, String name, int priority, Runnable action) {
		addAction(group, name, priority, true, action);
	}

	public void addAction(String name, int priority, boolean enabled, Runnable action) {
		addAction("", name, priority, enabled, action);
	}

	public void addAction(String group, String name, int priority, boolean enabled, Runnable action) {
		Try.ofFailable(() -> groups.get(group)).onSuccess(g -> g.addAction(name, priority, enabled, action))
				.orElseThrow(() -> new NullPointerException("Group not present."));
	}

	public void addCheckAction(String name, boolean checked, Runnable action) {
		addCheckAction("", name, 0, checked, action);
	}

	public void addCheckAction(String group, String name, boolean checked, Runnable action) {
		addCheckAction(group, name, 0, checked, action);
	}

	public void addCheckAction(String name, int priority, boolean checked, Runnable action) {
		addCheckAction("", name, priority, checked, action);
	}

	public void addCheckAction(String group, String name, int priority, boolean checked, Runnable action) {
		addCheckAction(group, name, priority, true, checked, action);
	}

	public void addCheckAction(String group, String name, int priority, boolean enabled, boolean checked,
			Runnable action) {
		Try.ofFailable(() -> groups.get(group))
				.onSuccess(g -> g.addCheckAction(name, priority, enabled, checked, action))
				.orElseThrow(() -> new NullPointerException("Group not present."));
	}

	void showMenu() {
		Display display = UiUtils.getDisplay();
		Menu menu = new Menu(display.getActiveShell(), POP_UP);
		List<MenuItemDescriptor> descriptors = extractDescritors();
		descriptors.forEach(d -> createMenuItem(menu, d));
		menu.setLocation(display.getCursorLocation());
		menu.setVisible(true);
	}

	private List<MenuItemDescriptor> extractDescritors() {
		List<MenuItemDescriptor> descriptors = new ArrayList<>();
		groups.values().forEach(g -> appendGroup(descriptors, g));
		Collections.sort(descriptors);
		return descriptors;
	}

	private static void appendGroup(List<MenuItemDescriptor> descriptors, MenuGroup group) {
		if (Values.isBlank(group.name)) {
			descriptors.addAll(group.actions);
		} else {
			descriptors.add(group);
		}
	}

	private void createMenuItem(Menu menu, MenuItemDescriptor descriptor) {
		if (descriptor instanceof MenuAction) {
			createMenuItem(menu, (MenuAction) descriptor);
		} else {
			MenuGroup group = (MenuGroup) descriptor;
			MenuItem item = new MenuItem(menu, SWT.CASCADE);
			item.setText(group.name);
			Menu childMenu = new Menu(item);
			item.setMenu(childMenu);
			List<MenuAction> actions = group.actions;
			Collections.sort(actions);
			actions.forEach(d -> createMenuItem(childMenu, d));
		}
	}

	protected void createMenuItem(Menu menu, MenuAction action) {
		int style = action.style == ActionStyle.check ? CHECK : PUSH;
		MenuItem item = new MenuItem(menu, style);
		item.setText(action.name);
		item.addListener(SWT.Selection, e -> action.action.run());
		item.setEnabled(action.enabled);
		if (action.style == ActionStyle.check) {
			item.setSelection(action.checked);
		}
	}

	private interface MenuItemDescriptor extends Comparable<MenuItemDescriptor> {
		int getPriority();
	}

	private static class MenuGroup implements MenuItemDescriptor {
		String name;
		int priority;
		List<MenuAction> actions = new ArrayList<>();

		MenuGroup(String name, int priority) {
			this.priority = priority;
			this.name = name;
		}

		void addAction(String name, int priority, boolean enabled, Runnable action) {
			actions.add(new MenuAction(name, priority, enabled, action));
		}

		void addCheckAction(String name, int priority, boolean enabled, boolean checked, Runnable action) {
			actions.add(new MenuAction(name, priority, enabled, checked, action));
		}

		@Override
		public int getPriority() {
			return priority;
		}

		@Override
		public int compareTo(MenuItemDescriptor o) {
			return Integer.compare(priority, o.getPriority());
		}
	}

	private static class MenuAction implements MenuItemDescriptor {
		String name;
		int priority;
		boolean checked;
		boolean enabled;
		Runnable action;
		ActionStyle style;

		public MenuAction(String name, int priority, boolean enabled, boolean checked, Runnable action) {
			this.name = name;
			this.priority = priority;
			this.checked = checked;
			this.enabled = enabled;
			this.action = action;
			style = ActionStyle.check;
		}

		public MenuAction(String name, int priority, boolean enabled, Runnable action) {
			this.name = name;
			this.priority = priority;
			this.action = action;
			style = ActionStyle.push;
			this.enabled = enabled;
		}

		@Override
		public int getPriority() {
			return priority;
		}

		@Override
		public int compareTo(MenuItemDescriptor o) {
			return Integer.compare(priority, o.getPriority());
		}
	}

	enum ActionStyle {
		push, check, cascade;
	}
}
