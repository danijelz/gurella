package com.gurella.studio.editor.common.bean;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.utils.UiUtils;

class ExpandablePropertyGroup extends Composite {
	public final String name;
	public final String qualifiedName;
	public final int level;
	public final ExpandablePropertyGroup parentGroup;

	private Twistie expandTwistie;
	private List<Control> controls = new ArrayList<>();

	public ExpandablePropertyGroup(Composite parent, String name, boolean expanded) {
		this(parent, null, name, expanded);
	}

	public ExpandablePropertyGroup(Composite parent, ExpandablePropertyGroup parentGroup, String name,
			boolean expanded) {
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
		nameLabel.addListener(SWT.MouseUp, e -> flipExpanded());
		Cursor cursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
		nameLabel.setCursor(cursor);
		nameLabel.addDisposeListener(e -> cursor.dispose());
	}

	private void flipExpanded() {
		boolean expanded = !isExpanded();
		expandTwistie.setExpanded(expanded);
		updateControls(expanded);
	}

	public boolean isExpanded() {
		return expandTwistie.isExpanded();
	}

	public void setExpanded(boolean expanded) {
		if (expanded != isEnabled()) {
			flipExpanded();
		}
	}

	private void updateControls(boolean visible) {
		controls.stream().peek(c -> c.setVisible(visible))
				.forEach(c -> ((GridData) c.getLayoutData()).exclude = !visible);
		UiUtils.reflow(this);
	}

	public void add(Control control) {
		boolean visible = isExpanded() && isVisible();
		control.setVisible(visible);

		GridData layoutData = (GridData) control.getLayoutData();
		if (layoutData == null) {
			layoutData = new GridData();
			control.setLayoutData(layoutData);
		}
		layoutData.exclude = !visible;

		int size = controls.size();
		if (control instanceof ExpandablePropertyGroup) {
			control.moveBelow(size == 0 ? this : controls.get(size - 1));
		} else {
			Optional<Control> childGroup = controls.stream().sequential()
					.filter(c -> (c instanceof ExpandablePropertyGroup)).findFirst();
			if (childGroup.isPresent()) {
				control.moveAbove(childGroup.get());
			} else {
				control.moveBelow(size == 0 ? this : controls.get(size - 1));
			}
		}
		controls.add(control);

		System.out.println(Arrays.toString(getParent().getChildren()));
	}

	public void clear() {
		controls.stream().forEach(c -> c.dispose());
		controls.clear();
		UiUtils.reflow(this);
	}

	@Override
	public String toString() {
		return "ExpandablePropertyGroup" + " {" + qualifiedName + "}";
	}

	private final class ExpandListener extends HyperlinkAdapter {
		@Override
		public void linkActivated(HyperlinkEvent e) {
			updateControls(expandTwistie.isExpanded());
		}
	}
}
