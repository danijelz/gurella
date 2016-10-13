package com.gurella.studio.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Twistie;

import com.gurella.studio.GurellaStudioPlugin;

class ExpandablePropertyGroup extends Composite {
	private Twistie expandTwistie;
	private Label nameLabel;
	private List<Control> controls = new ArrayList<>();

	public ExpandablePropertyGroup(Composite parent, String name, boolean expanded) {
		super(parent, SWT.BORDER);

		// setBackground(GurellaStudioPlugin.getColor(221, 234, 255));
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).margins(0, 0).spacing(4, 0).applyTo(this);

		expandTwistie = new Twistie(this, SWT.NONE);
		expandTwistie.setExpanded(expanded);
		expandTwistie.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				expand();
			}
		});
		nameLabel = GurellaStudioPlugin.getToolkit().createLabel(this, name);
	}

	void add(Control control) {
		boolean visible = expandTwistie.isExpanded();
		control.setVisible(visible);
		((GridData) control.getLayoutData()).exclude = !visible;
		controls.add(control);
	}

	void expand() {
		boolean visible = expandTwistie.isExpanded();
		controls.stream().forEach(c -> c.setVisible(visible));
		controls.stream().forEach(c -> ((GridData) c.getLayoutData()).exclude = !visible);
		getParent().layout(true, true);
		getParent().redraw();
	}
}
