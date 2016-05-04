package com.gurella.studio.editor.common;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.gurella.studio.GurellaStudioPlugin;

public class ErrorComposite extends Composite {
	private static final String NESTING_INDENT = "\t";

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
		this(parent, throwable, throwable.getMessage());
	}

	public ErrorComposite(Composite parent, Throwable throwable, String message) {
		super(parent, SWT.NONE);
		this.message = message;
		StringWriter writer = new StringWriter();
		throwable.printStackTrace(new PrintWriter(writer));
		status = new Status(IStatus.ERROR, GurellaStudioPlugin.PLUGIN_ID, message, new Throwable(writer.toString()));
		setLayout(new GridLayout(1, false));
		createDropDownList();
	}

	protected List createDropDownList() {
		list = new List(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		populateList(list);
		GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		list.setLayoutData(data);
		list.setFont(getFont());
		Menu copyMenu = new Menu(list);
		MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
		copyItem.addListener(SWT.Selection, e -> copyToClipboard());
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
			IntStream.range(0, nesting).forEach(i -> sb.append(NESTING_INDENT));
			sb.append(buildingStatus.getMessage());
			readLines(sb.toString()).forEach(line -> listToPopulate.add(line));
			incrementNesting = true;
		}

		if (!(t instanceof CoreException) && t != null) {
			// Include low-level exception message
			StringBuffer sb = new StringBuffer();
			IntStream.range(0, nesting).forEach(i -> sb.append(NESTING_INDENT));
			String message = t.getLocalizedMessage();
			sb.append(message == null ? t.toString() : message);
			readLines(sb.toString()).forEach(line -> listToPopulate.add(line));
			// listToPopulate.add(sb.toString());
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
		int temp = nesting;
		Arrays.stream(children).forEach(child -> populateList(listToPopulate, child, temp, true));
	}

	private void copyToClipboard() {
		StringBuffer statusBuffer = new StringBuffer();
		populateCopyBuffer(status, statusBuffer, 0);
		clipboard = new Clipboard(list.getDisplay());
		clipboard.setContents(new Object[] { statusBuffer.toString() }, new Transfer[] { TextTransfer.getInstance() });
		clipboard.dispose();
		clipboard = null;
	}

	private void populateCopyBuffer(IStatus buildingStatus, StringBuffer sb, int nesting) {
		IntStream.range(0, nesting).forEach(i -> sb.append(NESTING_INDENT));

		sb.append(buildingStatus.getMessage());
		sb.append("\n"); //$NON-NLS-1$

		// Look for a nested core exception
		Throwable t = buildingStatus.getException();
		if (t instanceof CoreException) {
			CoreException ce = (CoreException) t;
			populateCopyBuffer(ce.getStatus(), sb, nesting + 1);
		} else if (t != null) {
			IntStream.range(0, nesting).forEach(i -> sb.append(NESTING_INDENT));
			String message = t.getLocalizedMessage();
			sb.append(message == null ? t.toString() : message);
			sb.append("\n"); //$NON-NLS-1$
		}

		IStatus[] children = buildingStatus.getChildren();
		Arrays.stream(children).forEach(child -> populateCopyBuffer(child, sb, nesting + 1));
	}

	private static java.util.List<String> readLines(final String s) {
		BufferedReader reader = new BufferedReader(new StringReader(s));
		return reader.lines().filter(line -> line.length() > 0).collect(Collectors.toList());
	}
}
