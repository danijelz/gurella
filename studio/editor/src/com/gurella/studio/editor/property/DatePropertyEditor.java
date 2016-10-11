package com.gurella.studio.editor.property;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.studio.editor.utils.UiUtils;

public class DatePropertyEditor extends SimplePropertyEditor<Date> {
	private DateTime date;
	private DateTime time;

	public DatePropertyEditor(Composite parent, PropertyEditorContext<?, Date> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("New Date", () -> newValue(new Date()));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> newValue(null));
			}
		}
	}

	private void buildUi() {
		Date value = getValue();
		if (value == null) {
			Label label = UiUtils.createLabel(body, "null");
			label.setAlignment(SWT.CENTER);
			label.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 2, 1));
			label.addListener(SWT.MouseUp, (e) -> showMenu());
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(value);

			date = new DateTime(body, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
			UiUtils.adapt(date);
			GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
			gridData.widthHint = 100;
			date.setLayoutData(gridData);
			date.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			date.addListener(SWT.Selection, e -> setValue(extractTimestamp()));
			date.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			UiUtils.paintBordersFor(date);

			time = new DateTime(body, SWT.TIME | SWT.SHORT | SWT.DROP_DOWN);
			UiUtils.adapt(time);
			gridData = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
			gridData.widthHint = 75;
			time.setLayoutData(gridData);
			time.setTime(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
			time.addListener(SWT.Selection, e -> setValue(extractTimestamp()));
			UiUtils.paintBordersFor(time);

			UiUtils.paintBordersFor(body);
		}

		body.layout();
	}

	private Date extractTimestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.set(Calendar.YEAR, date.getYear());
		calendar.set(Calendar.MONTH, date.getMonth());
		calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
		calendar.set(Calendar.MINUTE, time.getMinutes());
		calendar.set(Calendar.SECOND, time.getSeconds());
		return calendar.getTime();
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(body);
		buildUi();
	}

	private void newValue(Date value) {
		setValue(value);
		rebuildUi();
	}

	@Override
	protected void updateValue(Date value) {
		rebuildUi();
	}
}
