package com.gurella.studio.wizard.project;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

class ConsoleGroup {
	private Text console;

	private final StringBuilder log = new StringBuilder();

	void createControl(Composite parent) {
		Group consoleGroup = new Group(parent, SWT.NONE);
		consoleGroup.setFont(parent.getFont());
		consoleGroup.setText("Log");
		consoleGroup.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).minSize(200, 200).grab(true, true)
				.applyTo(consoleGroup);

		console = new Text(consoleGroup, SWT.MULTI | SWT.LEFT | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		console.setEditable(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(console);
	}

	void log(String text) {
		log.append(text);
		console.getDisplay().asyncExec(() -> updateConsole());
	}

	private void updateConsole() {
		synchronized (console) {
			console.setText(log.toString());
			ScrollBar verticalBar = console.getVerticalBar();
			if (verticalBar != null) {
				verticalBar.setSelection(verticalBar.getMaximum());
			}
		}
	}

	String getLog() {
		return log.toString();
	}
}
