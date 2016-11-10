package com.gurella.studio.editor.common.bean;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.destroyFont;

import java.util.ArrayList;
import java.util.List;

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
	private Twistie expandTwistie;
	private List<Control> controls = new ArrayList<>();

	public ExpandablePropertyGroup(Composite parent, String name, boolean expanded) {
		this(parent, name, expanded, false);
	}

	public ExpandablePropertyGroup(Composite parent, String name, boolean expanded, boolean addSeparator) {
		super(parent, SWT.NONE);

		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(0, 0).spacing(0, 0).applyTo(this);

		expandTwistie = new Twistie(this, SWT.NONE);
		expandTwistie.setExpanded(expanded);
		expandTwistie.addHyperlinkListener(new ExpandListener());
		UiUtils.adapt(expandTwistie);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(expandTwistie);

		Label nameLabel = toolkit.createLabel(this, name);
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).indent(4, 0).applyTo(nameLabel);
		Font font = createFont(nameLabel, SWT.BOLD);
		nameLabel.addDisposeListener(e -> destroyFont(font));
		nameLabel.setFont(font);
		Color blue = getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		nameLabel.addListener(SWT.MouseEnter, e -> nameLabel.setForeground(blue));
		Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		nameLabel.addListener(SWT.MouseExit, e -> nameLabel.setForeground(black));
		nameLabel.addListener(SWT.MouseUp, e -> revertTwistie());
		Cursor cursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
		nameLabel.setCursor(cursor);
		nameLabel.addDisposeListener(e -> cursor.dispose());

		if (addSeparator) {
			Label separator = toolkit.createSeparator(this, SWT.HORIZONTAL);
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).applyTo(separator);
		}
	}

	private void revertTwistie() {
		expandTwistie.setExpanded(!expandTwistie.isExpanded());
		expand();
	}

	private void expand() {
		boolean visible = expandTwistie.isExpanded();
		controls.stream().forEach(c -> c.setVisible(visible));
		controls.stream().forEach(c -> ((GridData) c.getLayoutData()).exclude = !visible);
		getParent().layout(true, true);
		getParent().redraw();
		UiUtils.reflow(this);
	}

	void add(Control control) {
		boolean visible = expandTwistie.isExpanded();
		control.setVisible(visible);

		GridData layoutData = (GridData) control.getLayoutData();
		if (layoutData == null) {
			layoutData = new GridData();
			control.setLayoutData(layoutData);
		}
		layoutData.exclude = !visible;

		control.moveBelow(controls.size() == 0 ? this : controls.get(controls.size() - 1));
		controls.add(control);
	}

	void clear() {
		controls.stream().forEach(c -> c.dispose());
		controls.clear();
		getParent().layout(true, true);
		getParent().redraw();
		UiUtils.reflow(this);
	}

	private final class ExpandListener extends HyperlinkAdapter {
		@Override
		public void linkActivated(HyperlinkEvent e) {
			expand();
		}
	}
}
