package com.gurella.studio.editor.menu;

import static org.eclipse.swt.SWT.CHECK;
import static org.eclipse.swt.SWT.POP_UP;
import static org.eclipse.swt.SWT.PUSH;

import java.util.ArrayList;
import java.util.Collection;
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
		addGroup("", name, priority);
	}

	public void addGroup(String section, String name) {
		addGroup(section, name, 0);
	}

	public void addGroup(String section, String name, int priority) {
		MenuGroup parent = rootGroup;
		StringBuffer buffer = new StringBuffer();
		for (String part : name.split("\\.")) {
			buffer.append(buffer.length() == 0 ? part : "." + part);
			parent = getOrCreateGroup(buffer.toString(), part, section, priority, parent);
		}
	}

	private MenuGroup getOrCreateGroup(String id, String name, String section, int priority, MenuGroup parent) {
		return groups.computeIfAbsent(id, k -> parent.newChild(section, name, priority));
	}

	private MenuGroup getGroup(String name) {
		return groups.getOrDefault(name, rootGroup);
	}

	public void addSection(String name) {
		addSection("", name, 0);
	}

	public void addSection(String name, int priority) {
		addSection("", name, priority);
	}

	public void addSection(String group, String name) {
		addSection(group, name, 0);
	}

	public void addSection(String group, String name, int priority) {
		MenuGroup parent = getGroup(group);
		parent.addSection(name, priority);
	}

	public void addAction(String name, Runnable action) {
		addAction("", "", name, 0, true, action);
	}

	public void addAction(String group, String name, Runnable action) {
		addAction(group, "", name, 0, true, action);
	}

	public void addAction(String group, String section, String name, Runnable action) {
		addAction(group, section, name, 0, true, action);
	}

	public void addAction(String name, int priority, Runnable action) {
		addAction("", "", name, priority, true, action);
	}

	public void addAction(String group, String name, int priority, Runnable action) {
		addAction(group, "", name, priority, true, action);
	}

	public void addAction(String group, String section, String name, int priority, Runnable action) {
		addAction(group, section, name, priority, true, action);
	}

	public void addAction(String name, int priority, boolean enabled, Runnable action) {
		addAction("", "", name, priority, enabled, action);
	}

	public void addAction(String group, String name, int priority, boolean enabled, Runnable action) {
		addAction(group, "", name, priority, enabled, action);
	}

	public void addAction(String group, String section, String name, int priority, boolean enabled, Runnable action) {
		Try.ofFailable(() -> getGroup(group)).onSuccess(g -> g.addAction(section, name, priority, enabled, action))
				.orElseThrow(() -> new NullPointerException("Group not present."));
	}

	public void addCheckAction(String name, boolean checked, Runnable action) {
		addCheckAction("", "", name, 0, true, checked, action);
	}

	public void addCheckAction(String group, String name, boolean checked, Runnable action) {
		addCheckAction(group, "", name, 0, true, checked, action);
	}

	public void addCheckAction(String name, int priority, boolean checked, Runnable action) {
		addCheckAction("", "", name, priority, true, checked, action);
	}

	public void addCheckAction(String group, String name, int priority, boolean checked, Runnable action) {
		addCheckAction(group, "", name, priority, true, checked, action);
	}

	public void addCheckAction(String group, String name, int priority, boolean enabled, boolean checked,
			Runnable action) {
		addCheckAction(group, "", name, priority, enabled, checked, action);
	}

	public void addCheckAction(String group, String section, String name, int priority, boolean enabled,
			boolean checked, Runnable action) {
		Try.ofFailable(() -> getGroup(group))
				.onSuccess(g -> g.addCheckAction(section, name, priority, enabled, checked, action))
				.orElseThrow(() -> new NullPointerException("Group not present."));
	}

	void showMenu() {
		Display display = UiUtils.getDisplay();
		Menu menu = new Menu(display.getActiveShell(), POP_UP);
		rootGroup.sections.values().stream().filter(d -> !d.isEmpty()).sorted().forEach(s -> createSection(menu, s));
		menu.setLocation(display.getCursorLocation());
		menu.setVisible(true);
	}

	private void createMenuItem(Menu menu, MenuItemDescriptor descriptor) {
		if (descriptor instanceof MenuAction) {
			createActionItem(menu, (MenuAction) descriptor);
		} else if (descriptor instanceof MenuSection) {
			createSection(menu, (MenuSection) descriptor);
		} else {
			createGroupItem(menu, (MenuGroup) descriptor);
		}
	}

	private static void createActionItem(Menu menu, MenuAction action) {
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

	private void createSection(Menu menu, MenuSection section) {
		if (menu.getItems().length > 0) {
			newSection(menu);
		}

		section.actions.stream().filter(i -> !i.isEmpty()).sorted().forEach(d -> createMenuItem(menu, d));
	}

	private static MenuItem newSection(Menu menu) {
		return new MenuItem(menu, SWT.SEPARATOR);
	}

	private void createGroupItem(Menu menu, MenuGroup group) {
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		String name = group.name;
		item.setText(name);
		addAccelerator(item, name);
		Menu childMenu = new Menu(item);
		item.setMenu(childMenu);
		group.sections.values().stream().filter(s -> !s.isEmpty()).sorted().forEach(d -> createMenuItem(childMenu, d));
	}

	private static void addAccelerator(MenuItem item, String name) {
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
			return o instanceof MenuAction ? 0 : o instanceof MenuSection ? 1 : 2;
		}

		default boolean isEmpty() {
			return false;
		}
	}

	private interface CompositeMenuItemDescriptor extends MenuItemDescriptor {
		Collection<? extends MenuItemDescriptor> getChildren();

		@Override
		default boolean isEmpty() {
			return !getChildren().stream().filter(d -> !d.isEmpty()).findAny().isPresent();
		}
	}

	private static class MenuGroup implements CompositeMenuItemDescriptor {
		String name;
		int priority;
		Map<String, MenuSection> sections = new HashMap<>();

		MenuGroup(String name, int priority) {
			this.name = name;
			this.priority = priority;
			sections.put("", new MenuSection(Integer.MIN_VALUE));
		}

		public void addSection(String name, int priority) {
			sections.computeIfAbsent(name, n -> new MenuSection(priority));
		}

		void addAction(String section, String name, int priority, boolean enabled, Runnable action) {
			sections.get(section).addAction(name, priority, enabled, action);
		}

		void addCheckAction(String section, String name, int priority, boolean enabled, boolean checked,
				Runnable action) {
			sections.get(section).addCheckAction(name, priority, enabled, checked, action);
		}

		MenuGroup newChild(String section, String name, int priority) {
			MenuGroup child = new MenuGroup(name, priority);
			sections.get(section).actions.add(child);
			return child;
		}

		@Override
		public int getPriority() {
			return priority;
		}

		@Override
		public Collection<? extends MenuItemDescriptor> getChildren() {
			return sections.values();
		}
	}

	private static class MenuSection implements CompositeMenuItemDescriptor {
		int priority;
		List<MenuItemDescriptor> actions = new ArrayList<>();

		MenuSection(int priority) {
			this.priority = priority;
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
		public Collection<? extends MenuItemDescriptor> getChildren() {
			return actions;
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
