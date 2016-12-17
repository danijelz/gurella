package com.gurella.studio.editor.ui.bean;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Twistie;

import com.gurella.engine.event.Listener1;
import com.gurella.engine.event.Signal1;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

class ExpandableGroup extends Composite {
	public final String name;
	public final String qualifiedName;
	public final int level;
	public final ExpandableGroup parentGroup;

	private Twistie expandTwistie;
	private List<Control> controls = new ArrayList<>();

	private Signal1<Boolean> expandSignal = new Signal1<>();

	public ExpandableGroup(Composite parent, String name, boolean expanded) {
		this(parent, null, name, expanded);
	}

	public ExpandableGroup(Composite parent, ExpandableGroup parentGroup, String name, boolean expanded) {
		super(parent, SWT.NONE);
		this.name = name;
		this.qualifiedName = parentGroup == null ? name : parentGroup.qualifiedName + "." + name;
		this.level = parentGroup == null ? 0 : parentGroup.level + 1;
		this.parentGroup = parentGroup;

		addListener(SWT.Hide, e -> updateControls(false));
		addListener(SWT.Show, e -> updateControls(isExpanded()));
		addListener(SWT.Dispose, e -> controls.stream().forEach(c -> c.dispose()));

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(0, 0).spacing(0, 0).applyTo(this);

		expandTwistie = new Twistie(this, SWT.NONE);
		expandTwistie.setExpanded(expanded);
		expandTwistie.addHyperlinkListener(new ExpandListener());
		UiUtils.adapt(expandTwistie);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(expandTwistie);

		Label nameLabel = toolkit.createLabel(this, name);
		nameLabel.setAlignment(SWT.LEFT);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(4, 0).applyTo(nameLabel);

		Font font = createFont(nameLabel, SWT.BOLD);
		nameLabel.addDisposeListener(e -> destroyFont(font));
		nameLabel.setFont(font);

		Color blue = getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		nameLabel.addListener(SWT.MouseEnter, e -> nameLabel.setForeground(blue));

		Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		nameLabel.addListener(SWT.MouseExit, e -> nameLabel.setForeground(black));
		nameLabel.addListener(SWT.MouseUp, e -> flip());

		Cursor cursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
		nameLabel.setCursor(cursor);
		nameLabel.addDisposeListener(e -> cursor.dispose());
	}

	private void flip() {
		boolean expanded = !isExpanded();
		expandTwistie.setExpanded(expanded);
		updateControls(expanded);
		expandSignal.dispatch(Boolean.valueOf(expanded));
	}

	public boolean isExpanded() {
		return expandTwistie.isExpanded();
	}

	public void setExpanded(boolean expanded) {
		if (expanded != isExpanded()) {
			flip();
		}
	}

	private void updateControls(boolean visible) {
		controls.stream().forEach(c -> updateControlVisibility(c, visible));
		UiUtils.reflow(this);
	}

	public void add(Control control) {
		boolean visible = isExpanded() && getVisible();
		updateControlVisibility(control, visible);
		positionNewControl(control);
		controls.add(control);
	}

	private static void updateControlVisibility(Control control, boolean visible) {
		control.setVisible(visible);
		ensureGridData(control).exclude = !visible;
	}

	private static GridData ensureGridData(Control control) {
		Object layoutData = control.getLayoutData();
		if (layoutData instanceof GridData) {
			return (GridData) layoutData;
		} else {
			GridData gridData = new GridData();
			control.setLayoutData(gridData);
			return gridData;
		}
	}

	private void positionNewControl(Control control) {
		if (control instanceof ExpandableGroup) {
			control.moveBelow(findLastControlInGroup(this));
		} else {
			Predicate<Control> groupsFilter = c -> (c instanceof ExpandableGroup);
			Optional<Control> childGroup = controls.stream().filter(groupsFilter).findFirst();
			if (childGroup.isPresent()) {
				control.moveAbove(childGroup.get());
			} else {
				int size = controls.size();
				control.moveBelow(size == 0 ? this : controls.get(size - 1));
			}
		}
	}

	private static Control findLastControlInGroup(ExpandableGroup group) {
		int size = group.controls.size();
		if (size == 0) {
			return group;
		}

		Control last = group.controls.get(size - 1);
		return last instanceof ExpandableGroup ? findLastControlInGroup((ExpandableGroup) last) : last;
	}

	public void clear() {
		controls.stream().forEach(c -> c.dispose());
		controls.clear();
		UiUtils.reflow(this);
	}

	public void addExpandListener(Listener1<Boolean> listener) {
		expandSignal.addListener(listener);
	}

	public void removeExpandListener(Listener1<Boolean> listener) {
		expandSignal.removeListener(listener);
	}

	@Override
	public String toString() {
		return "ExpandableGroup" + " {" + qualifiedName + "}";
	}

	private final class ExpandListener extends HyperlinkAdapter {
		@Override
		public void linkActivated(HyperlinkEvent e) {
			boolean expanded = expandTwistie.isExpanded();
			updateControls(expanded);
			expandSignal.dispatch(Boolean.valueOf(expanded));
		}
	}
}
