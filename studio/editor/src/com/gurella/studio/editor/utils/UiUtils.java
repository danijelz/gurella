package com.gurella.studio.editor.utils;

import static com.gurella.studio.GurellaStudioPlugin.createFont;
import static com.gurella.studio.GurellaStudioPlugin.getToolkit;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.utils.Values;

public class UiUtils {
	private static Font TEXT_FONT;

	public static Text createText(Composite parent) {
		FormToolkit toolkit = getToolkit();
		Text text = toolkit.createText(parent, "", SWT.SINGLE);
		toolkit.adapt(text, false, false);
		text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		text.setFont(getTextFont(text));
		return text;
	}

	public static Font getTextFont(Text text) {
		if (TEXT_FONT == null) {
			TEXT_FONT = createFont(FontDescriptor.createFrom(text.getFont()).increaseHeight(-1));
		}
		return TEXT_FONT;
	}

	public static Text createFloatWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyFloat(e, text.getText()));
		WheelEventListener listener = UiUtils::floatWidgetWheelEvent;
		text.addListener(SWT.MouseVerticalWheel, e -> onMouseVerticalWheel(text, listener, e));
		text.addListener(SWT.MouseDown, e -> onTrackerStart(text, listener, e));
		return text;
	}

	public static void verifyFloat(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				Float.parseFloat(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	private static void floatWidgetWheelEvent(Text text, int amount, float multiplier) {
		String str = text.getText();
		if (Values.isBlank(str)) {
			return;
		}
		float value = Float.parseFloat(str);
		text.setText(String.valueOf(value + (amount * multiplier)));
	}

	private static String getNewText(VerifyEvent e, String oldValue) {
		return oldValue.substring(0, e.start) + e.text + oldValue.substring(e.end);
	}

	public static Text createIntegerWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyInteger(e, text.getText()));
		return text;
	}

	public static void verifyInteger(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				Integer.parseInt(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createBigIntegerWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyBigInteger(e, text.getText()));
		return text;
	}

	@SuppressWarnings("unused")
	public static void verifyBigInteger(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				new BigInteger(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createBigDecimalWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyBigDecimal(e, text.getText()));
		return text;
	}

	@SuppressWarnings("unused")
	public static void verifyBigDecimal(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				new BigDecimal(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createLongWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyLong(e, text.getText()));
		return text;
	}

	public static void verifyLong(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				Long.parseLong(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createByteWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyByte(e, text.getText()));
		return text;
	}

	public static void verifyByte(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				Byte.parseByte(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createDoubleWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyDouble(e, text.getText()));
		return text;
	}

	public static void verifyDouble(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				Double.parseDouble(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createShortWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyShort(e, text.getText()));
		return text;
	}

	public static void verifyShort(VerifyEvent e, String oldValue) {
		try {
			String newText = getNewText(e, oldValue);
			if (newText.length() > 0) {
				Short.parseShort(newText);
			}
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static Text createCharacterWidget(Composite parent) {
		Text text = createText(parent);
		text.addVerifyListener(e -> verifyCharacter(e, text.getText()));
		return text;
	}

	public static void verifyCharacter(VerifyEvent e, String oldValue) {
		String newText = getNewText(e, oldValue);
		if (newText.length() > 1) {
			e.doit = false;
		}
	}

	public static <T extends Enum<T>> ComboViewer createEnumComboViewer(Composite parent, Class<T> enumType) {
		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

		ComboViewer comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(enumType.getEnumConstants());
		return comboViewer;
	}

	public static void paintBordersFor(Composite parent) {
		getToolkit().paintBordersFor(parent);
	}

	public static Composite createComposite(Composite parent) {
		return getToolkit().createComposite(parent);
	}

	public static Composite createComposite(Composite parent, int style) {
		return getToolkit().createComposite(parent, style);
	}

	public static Label createLabel(Composite parent, String text) {
		return getToolkit().createLabel(parent, text);
	}

	public static Label createLabel(Composite parent, String text, int style) {
		return getToolkit().createLabel(parent, text, style);
	}

	public static void adapt(Composite composite) {
		getToolkit().adapt(composite);
	}

	public static void verifyHexRgba(VerifyEvent e, String oldValue) {
		String newText = getNewText(e, oldValue);
		int length = newText.length();

		if (length == 0) {
			return;
		}

		String temp = newText.startsWith("#") ? newText.substring(1) : newText;
		if (temp.length() > 8) {
			e.doit = false;
			return;
		}

		try {
			Integer.parseUnsignedInt(temp, 16);
		} catch (Exception e2) {
			e.doit = false;
		}
	}

	public static void paintSimpleBorder(Composite composite, GC gc) {
		Rectangle area = composite.getClientArea();
		gc.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		gc.drawRectangle(0, 0, area.width - 1, area.height - 1);
	}

	private static void onMouseVerticalWheel(Text text, WheelEventListener listener, Event e) {
		if (Values.isBlank(text.getText())) {
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

		e.doit = false;
		int amount = e.count > 0 ? 1 : -1;
		listener.onWheelEvent(text, amount, multiplier);
	}

	private static void onTrackerStart(Text text, WheelEventListener listener, Event e) {
		int stateMask = e.stateMask;
		if (((stateMask & (SWT.SHIFT | SWT.CONTROL | SWT.ALT)) == 0) || e.button != 1) {
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
				listener.onWheelEvent(text, diffY, multiplier);
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

	public interface WheelEventListener {
		void onWheelEvent(Text text, int amount, float multiplier);
	}
}
