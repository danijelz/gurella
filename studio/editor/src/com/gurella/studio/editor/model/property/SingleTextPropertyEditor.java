package com.gurella.studio.editor.model.property;

import java.util.function.BiConsumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.gurella.studio.editor.utils.UiUtils;

public abstract class SingleTextPropertyEditor<P> extends SimplePropertyEditor<P> {
	private Text text;

	public SingleTextPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		buildUi();

		if (!context.isFixedValue()) {
			addMenuItem("Set to default", () -> updateValue(getDefaultValue()));
			if (context.isNullable()) {
				addMenuItem("Set to null", () -> updateValue(null));
			}
		}
	}

	protected abstract P getDefaultValue();

	protected abstract P extractValue(String stringValue);

	protected abstract BiConsumer<VerifyEvent, String> getVerifyListener();

	private void buildUi() {
		P value = getValue();
		text = UiUtils.createText(body);
		text.addVerifyListener(e -> getVerifyListener().accept(e, text.getText()));
		GridData layoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		layoutData.widthHint = 60;
		layoutData.heightHint = 16;
		text.setLayoutData(layoutData);

		if (value == null) {
			text.setMessage("null");
		} else {
			text.setText(value.toString());
		}

		text.addModifyListener(e -> textModified());
		text.addListener(SWT.MouseVerticalWheel, e -> onMouseVerticalWheel(e));
		text.addListener(SWT.MouseDown, e -> onTrackerStart(e));

		UiUtils.paintBordersFor(body);
	}

	private void textModified() {
		String stringValue = text.getText();
		if (stringValue.length() > 0) {
			setValue(extractValue(stringValue));
			text.setMessage("");
		} else if (context.isNullable()) {
			setValue(null);
			text.setMessage("null");
		} else {
			P defaultValue = getDefaultValue();
			setValue(defaultValue);
			String defaultStringValue = defaultValue == null ? "null" : toStringNonNullValue(defaultValue);
			text.setMessage("default - " + defaultStringValue);
		}
	}

	protected void updateValue(P value) {
		setValue(value);
		if (value == null) {
			text.setText("");
			text.setMessage("null");
		} else {
			text.setText(toStringNonNullValue(value));
			text.setMessage("");
		}
	}

	protected String toStringNonNullValue(P value) {
		return value.toString();
	}

	private void onMouseVerticalWheel(Event e) {
		if (getValue() == null) {
			return;
		}

		float multiplier = -1;
		int stateMask = e.stateMask;
		if ((stateMask & SWT.SHIFT) != 0) {
			multiplier = 1;
		} else if ((stateMask & SWT.CONTROL) != 0) {
			multiplier = 10;
		} else if ((stateMask & SWT.ALT) != 0) {
			multiplier = 0.1f;
		}

		if (multiplier < 0) {
			return;
		}

		WheelEventListener listener = getWheelEventListener();
		if (listener == null) {
			return;
		}

		e.doit = false;
		int amount = e.count > 0 ? 1 : -1;
		listener.onWheelEvent(amount, multiplier);
	}

	protected WheelEventListener getWheelEventListener() {
		return null;
	}

	public void onTrackerStart(Event e) {
		int stateMask = e.stateMask;
		if (((stateMask & (SWT.SHIFT | SWT.CONTROL | SWT.ALT)) == 0) || e.button != 1) {
			return;
		}

		WheelEventListener listener = getWheelEventListener();
		if (listener == null) {
			return;
		}

		DragManager.manage(text, listener);
	}

	private static class DragManager implements Listener {
		private Text text;
		private WheelEventListener listener;

		private int startY;
		private int ratio;

		static void manage(Text text, WheelEventListener listener) {
			DragManager manager = new DragManager();
			manager.init(text, listener);
		}

		private void init(Text text, WheelEventListener listener) {
			this.text = text;
			this.listener = listener;
			Display display = text.getDisplay();
			startY = getCursorYLocation();
			ratio = display.getClientArea().height / 100;
			display.addFilter(SWT.MouseMove, this);
			display.addFilter(SWT.MouseUp, this);
			Cursor resizeCursor = display.getSystemCursor(SWT.CURSOR_SIZENS);
			text.getShell().setCursor(resizeCursor);
			text.setCursor(resizeCursor);
		}

		private int getCursorYLocation() {
			return text.getDisplay().getCursorLocation().y;
		}

		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.MouseMove:
				onMouseMove(event);
				return;
			case SWT.MouseUp:
				onMouseUp();
				return;
			default:
				return;
			}
		}

		private void onMouseMove(Event event) {
			int stateMask = event.stateMask;
			float multiplier = ((stateMask & SWT.CONTROL) != 0) ? 10 : ((stateMask & SWT.ALT) != 0) ? 0.1f : 1;
			int currentY = getCursorYLocation();
			int diffY = (startY - currentY) / ratio;
			if (diffY != 0) {
				startY = currentY;
				listener.onWheelEvent(diffY, multiplier);
			}
		}

		private void onMouseUp() {
			Display display = text.getDisplay();
			display.removeFilter(SWT.MouseMove, this);
			display.removeFilter(SWT.MouseUp, this);
			text.setCursor(null);
			text.getShell().setCursor(null);
		}
	}

	//TODO rename
	public interface WheelEventListener {
		void onWheelEvent(int amount, float multiplier);
	}
}
