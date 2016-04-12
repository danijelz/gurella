package com.gurella.studio.editor.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.studio.editor.GurellaStudioPlugin;

public class ErrorComposite extends Composite {
	private static final String NESTING_INDENT = "  ";

	private List list;
	private Clipboard clipboard;

	private IStatus status;
	private String message;

	public ErrorComposite(Composite parent, IStatus status, String message) {
		super(parent, SWT.NONE);
		this.status = status;
		this.message = message;
		setLayout(new GridLayout(1, false));
		createDropDownList();
	}

	public ErrorComposite(Composite parent, Throwable throwable) {
		super(parent, SWT.NONE);
		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		message = throwable.getMessage();
		status = new Status(IStatus.ERROR, GurellaStudioPlugin.PLUGIN_ID, message, new Throwable(writer.toString()));

		setLayout(new GridLayout(1, false));

		createDropDownList();
	}

	protected List createDropDownList() {
		list = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		populateList(list);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		list.setLayoutData(data);
		list.setFont(getFont());
		Menu copyMenu = new Menu(list);
		MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
		copyItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				copyToClipboard();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				copyToClipboard();
			}
		});
		copyItem.setText(JFaceResources.getString("copy")); //$NON-NLS-1$
		list.setMenu(copyMenu);
		return list;
	}

	private void populateList(List listToPopulate) {
		populateList(listToPopulate, status, 0, false);
	}

	private void populateList(List listToPopulate, IStatus buildingStatus, int nesting, boolean includeStatus) {
		Throwable t = buildingStatus.getException();
		boolean incrementNesting = false;

		if (includeStatus) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nesting; i++) {
				sb.append(NESTING_INDENT);
			}
			String message = buildingStatus.getMessage();
			sb.append(message);
			java.util.List<String> lines = readLines(sb.toString());
			for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();) {
				String line = iterator.next();
				listToPopulate.add(line);
			}
			incrementNesting = true;
		}

		if (!(t instanceof CoreException) && t != null) {
			// Include low-level exception message
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nesting; i++) {
				sb.append(NESTING_INDENT);
			}
			String message = t.getLocalizedMessage();
			if (message == null) {
				message = t.toString();
			}

			sb.append(message);
			
			java.util.List<String> lines = readLines(sb.toString());
			for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();) {
				String line = iterator.next();
				listToPopulate.add(line);
			}
			//listToPopulate.add(sb.toString());
			incrementNesting = true;
		}

		if (incrementNesting) {
			nesting++;
		}

		// Look for a nested core exception
		if (t instanceof CoreException) {
			CoreException ce = (CoreException) t;
			IStatus eStatus = ce.getStatus();
			// Only print the exception message if it is not contained in the
			// parent message
			if (message == null || message.indexOf(eStatus.getMessage()) == -1) {
				populateList(listToPopulate, eStatus, nesting, true);
			}
		}

		// Look for child status
		IStatus[] children = buildingStatus.getChildren();
		for (int i = 0; i < children.length; i++) {
			populateList(listToPopulate, children[i], nesting, true);
		}
	}

	private void copyToClipboard() {
		if (clipboard != null) {
			clipboard.dispose();
		}
		StringBuffer statusBuffer = new StringBuffer();
		populateCopyBuffer(status, statusBuffer, 0);
		clipboard = new Clipboard(list.getDisplay());
		clipboard.setContents(new Object[] { statusBuffer.toString() }, new Transfer[] { TextTransfer.getInstance() });
	}

	private void populateCopyBuffer(IStatus buildingStatus, StringBuffer buffer, int nesting) {
		for (int i = 0; i < nesting; i++) {
			buffer.append(NESTING_INDENT);
		}
		buffer.append(buildingStatus.getMessage());
		buffer.append("\n"); //$NON-NLS-1$

		// Look for a nested core exception
		Throwable t = buildingStatus.getException();
		if (t instanceof CoreException) {
			CoreException ce = (CoreException) t;
			populateCopyBuffer(ce.getStatus(), buffer, nesting + 1);
		} else if (t != null) {
			// Include low-level exception message
			for (int i = 0; i < nesting; i++) {
				buffer.append(NESTING_INDENT);
			}
			String message = t.getLocalizedMessage();
			if (message == null) {
				message = t.toString();
			}
			buffer.append(message);
			buffer.append("\n"); //$NON-NLS-1$
		}

		IStatus[] children = buildingStatus.getChildren();
		for (int i = 0; i < children.length; i++) {
			populateCopyBuffer(children[i], buffer, nesting + 1);
		}
	}

	private static java.util.List<String> readLines(final String s) {
		java.util.List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new StringReader(s));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0)
					lines.add(line);
			}
		} catch (IOException e) {
			// shouldn't get this
		}
		return lines;
	}
}
