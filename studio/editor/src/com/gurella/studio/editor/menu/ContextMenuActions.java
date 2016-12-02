package com.gurella.studio.editor.menu;

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

import com.gurella.studio.editor.utils.Try;
import com.gurella.studio.editor.utils.UiUtils;

public class ContextMenuActions {
	MenuGroup rootGroup = new MenuGroup("", Integer.MIN_VALUE);
	private Map<String, MenuGroup> groups = new HashMap<>();

	public ContextMenuActions() {
		groups.put("", rootGroup);
	}

	public void addGroup(String name) {
		addGroup(name, 0);
	}

	public void addGroup(String name, int priority) {
		MenuGroup parent = rootGroup;
		StringBuffer buffer = new StringBuffer();
		for (String part : name.split("\\.")) {
			buffer.append(buffer.length() == 0 ? part : "." + part);
			parent = getOrCreateGroup(buffer.toString(), part, priority, parent);
		}
	}

	private MenuGroup getOrCreateGroup(String id, String name, int priority, MenuGroup parent) {
		return groups.computeIfAbsent(id, k -> parent.newChild(name, priority));
	}

	private MenuGroup getGroup(String name) {
		return groups.getOrDefault(name, groups.get(""));
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
		Try.ofFailable(() -> getGroup(group)).onSuccess(g -> g.addAction(name, priority, enabled, action))
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
		Try.ofFailable(() -> getGroup(group)).onSuccess(g -> g.addCheckAction(name, priority, enabled, checked, action))
				.orElseThrow(() -> new NullPointerException("Group not present."));
	}

	void showMenu() {
		Display display = UiUtils.getDisplay();
		Menu menu = new Menu(display.getActiveShell(), POP_UP);
		rootGroup.actions.stream().sorted().forEachOrdered(d -> createMenuItem(menu, d));
		menu.setLocation(display.getCursorLocation());
		menu.setVisible(true);
	}

	private void createMenuItem(Menu menu, MenuItemDescriptor descriptor) {
		if (descriptor instanceof MenuAction) {
			createActionItem(menu, (MenuAction) descriptor);
		} else {
			createGroupItem(menu, descriptor);
		}
	}

	private void createGroupItem(Menu menu, MenuItemDescriptor descriptor) {
		MenuGroup group = (MenuGroup) descriptor;
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		String name = group.name;
		item.setText(name);
		addAccelerator(item, name);
		Menu childMenu = new Menu(item);
		item.setMenu(childMenu);
		List<MenuItemDescriptor> actions = group.actions;
		Collections.sort(actions);
		actions.forEach(d -> createMenuItem(childMenu, d));
	}

	protected void createActionItem(Menu menu, MenuAction action) {
		int style = action.style == ActionStyle.check ? CHECK : PUSH;
		MenuItem item = new MenuItem(menu, style);
		String name = action.name;
		item.setText(name);
		addAccelerator(item, name);
		item.addListener(SWT.Selection, e -> action.action.run());
		item.setEnabled(action.enabled);
		if (action.style == ActionStyle.check) {
			item.setSelection(action.checked);
		}
	}

	protected void addAccelerator(MenuItem item, String name) {
		int accIndex = name.indexOf('&');
		if (accIndex >= 0 && accIndex < name.length()) {
			char acc = name.charAt(accIndex + 1);
			item.setAccelerator(acc);
		}
	}

	private interface MenuItemDescriptor extends Comparable<MenuItemDescriptor> {
		int getPriority();

		@Override
		default int compareTo(MenuItemDescriptor o) {
			int result = Integer.compare(primaryComparisonValue(this), primaryComparisonValue(o));
			return result == 0 ? Integer.compare(getPriority(), o.getPriority()) : result;
		}

		default int primaryComparisonValue(MenuItemDescriptor o) {
			return o instanceof MenuAction ? 0 : 1;
		}
	}

	private static class MenuGroup implements MenuItemDescriptor {
		String name;
		int priority;
		List<MenuItemDescriptor> actions = new ArrayList<>();

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

		MenuGroup newChild(String name, int priority) {
			MenuGroup child = new MenuGroup(name, priority);
			actions.add(child);
			return child;
		}

		@Override
		public int getPriority() {
			return priority;
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
	}

	enum ActionStyle {
		push, check, cascade;
	}
}
