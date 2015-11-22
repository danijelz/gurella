package com.gurella.studio.editor.swtgl;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pool;

public class SwtLwjglInput implements Input {
	static public float keyRepeatInitialTime = 0.4f;
	static public float keyRepeatTime = 0.1f;

	Array<KeyEvent> keyEvents = new Array<KeyEvent>();
	Array<TouchEvent> touchEvents = new Array<TouchEvent>();
	boolean mousePressed = false;
	int mouseX, mouseY;
	int deltaX, deltaY;
	IntSet pressedKeys = new IntSet();
	boolean keyJustPressed = false;
	boolean[] justPressedKeys = new boolean[256];
	boolean justTouched = false;
	IntSet pressedButtons = new IntSet();
	InputProcessor processor;
	char lastKeyCharPressed;
	float keyRepeatTimer;
	long currentEventTimeStamp;
	float deltaTime;
	long lastTime;

	private final GLCanvas glCanvas;

	private final MouseListener mouseListener = new MouseListener();
	private final KeyListener keyListener = new KeyListener();

	Pool<KeyEvent> usedKeyEvents = new Pool<KeyEvent>(16, 1000) {
		@Override
		protected KeyEvent newObject() {
			return new KeyEvent();
		}
	};

	Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {
		@Override
		protected TouchEvent newObject() {
			return new TouchEvent();
		}
	};

	public SwtLwjglInput(final GLCanvas glCanvas) {
		this.glCanvas = glCanvas;
		glCanvas.addListener(SWT.MouseWheel, mouseListener);
		glCanvas.addListener(SWT.MouseDown, mouseListener);
		glCanvas.addListener(SWT.MouseUp, mouseListener);
		glCanvas.addListener(SWT.MouseMove, mouseListener);
		glCanvas.addListener(SWT.KeyDown, keyListener);
		glCanvas.addListener(SWT.KeyUp, keyListener);
	}

	@Override
	public float getAccelerometerX() {
		return 0;
	}

	@Override
	public float getAccelerometerY() {
		return 0;
	}

	@Override
	public float getAccelerometerZ() {
		return 0;
	}

	@Override
	public void getTextInput(final TextInputListener listener, final String title, final String text, final String hint) {
		// SwingUtilities.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// JPanel panel = new JPanel(new FlowLayout());
		//
		// JPanel textPanel = new JPanel() {
		// public boolean isOptimizedDrawingEnabled() {
		// return false;
		// };
		// };
		//
		// textPanel.setLayout(new OverlayLayout(textPanel));
		// panel.add(textPanel);
		//
		// final JTextField textField = new JTextField(20);
		// textField.setText(text);
		// textField.setAlignmentX(0.0f);
		// textPanel.add(textField);
		//
		// final JLabel placeholderLabel = new JLabel(hint);
		// placeholderLabel.setForeground(Color.GRAY);
		// placeholderLabel.setAlignmentX(0.0f);
		// textPanel.add(placeholderLabel, 0);
		//
		// textField.getDocument().addDocumentListener(new DocumentListener() {
		//
		// @Override
		// public void removeUpdate(DocumentEvent arg0) {
		// this.updated();
		// }
		//
		// @Override
		// public void insertUpdate(DocumentEvent arg0) {
		// this.updated();
		// }
		//
		// @Override
		// public void changedUpdate(DocumentEvent arg0) {
		// this.updated();
		// }
		//
		// private void updated() {
		// if (textField.getText().length() == 0)
		// placeholderLabel.setVisible(true);
		// else
		// placeholderLabel.setVisible(false);
		// }
		// });
		//
		// JOptionPane pane = new JOptionPane(panel,
		// JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
		// null, null, null);
		//
		// pane.setInitialValue(null);
		// pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());
		//
		// Border border = textField.getBorder();
		// placeholderLabel.setBorder(new
		// EmptyBorder(border.getBorderInsets(textField)));
		//
		// JDialog dialog = pane.createDialog(null, title);
		// pane.selectInitialValue();
		//
		// dialog.addWindowFocusListener(new WindowFocusListener() {
		//
		// @Override
		// public void windowLostFocus(WindowEvent arg0) {
		// }
		//
		// @Override
		// public void windowGainedFocus(WindowEvent arg0) {
		// textField.requestFocusInWindow();
		// }
		// });
		//
		// dialog.setVisible(true);
		// dialog.dispose();
		//
		// Object selectedValue = pane.getValue();
		//
		// if (selectedValue != null && (selectedValue instanceof Integer)
		// && ((Integer) selectedValue).intValue() == JOptionPane.OK_OPTION) {
		// listener.input(textField.getText());
		// } else {
		// listener.canceled();
		// }
		//
		// }
		// });
	}

	@Override
	public int getX() {
		return mouseX;
	}

	@Override
	public int getY() {
		return mouseY;
	}

	public boolean isAccelerometerAvailable() {
		return false;
	}

	@Override
	public boolean isKeyPressed(int key) {
		return pressedKeys.contains(key);
	}

	@Override
	public boolean isKeyJustPressed(int key) {
		if (key == Input.Keys.ANY_KEY) {
			return keyJustPressed;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return justPressedKeys[key];
	}

	@Override
	public boolean isTouched() {
		return pressedButtons.size > 0;
	}

	@Override
	public int getX(int pointer) {
		return pointer == 0
				? getX()
				: 0;
	}

	@Override
	public int getY(int pointer) {
		return pointer == 0
				? getY()
				: 0;
	}

	@Override
	public boolean isTouched(int pointer) {
		return pointer == 0
				? isTouched()
				: false;
	}

	public boolean supportsMultitouch() {
		return false;
	}

	@Override
	public void setOnscreenKeyboardVisible(boolean visible) {

	}

	@Override
	public void setCatchBackKey(boolean catchBack) {

	}

	@Override
	public boolean isCatchBackKey() {
		return false;
	}

	public void update() {
		synchronized (this) {
			if (processor != null) {
				int len = keyEvents.size;
				for (int i = 0; i < len; i++) {
					KeyEvent e = keyEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
					case KeyEvent.KEY_DOWN:
						processor.keyDown(e.keyCode);
						break;
					case KeyEvent.KEY_UP:
						processor.keyUp(e.keyCode);
						break;
					case KeyEvent.KEY_TYPED:
						processor.keyTyped(e.keyChar);
					}
					usedKeyEvents.free(e);
				}

				len = touchEvents.size;
				for (int i = 0; i < len; i++) {
					TouchEvent e = touchEvents.get(i);
					currentEventTimeStamp = e.timeStamp;
					switch (e.type) {
					case TouchEvent.TOUCH_DOWN:
						processor.touchDown(e.x, e.y, e.pointer, e.button);
						break;
					case TouchEvent.TOUCH_UP:
						processor.touchUp(e.x, e.y, e.pointer, e.button);
						break;
					case TouchEvent.TOUCH_DRAGGED:
						processor.touchDragged(e.x, e.y, e.pointer);
						break;
					case TouchEvent.TOUCH_MOVED:
						processor.mouseMoved(e.x, e.y);
						break;
					case TouchEvent.TOUCH_SCROLLED:
						processor.scrolled(e.scrollAmount);
					}
					usedTouchEvents.free(e);
				}
			} else {
				int len = touchEvents.size;
				for (int i = 0; i < len; i++) {
					usedTouchEvents.free(touchEvents.get(i));
				}

				len = keyEvents.size;
				for (int i = 0; i < len; i++) {
					usedKeyEvents.free(keyEvents.get(i));
				}
			}

			keyEvents.clear();
			touchEvents.clear();
		}
	}

	@Override
	public void setInputProcessor(InputProcessor processor) {
		this.processor = processor;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return this.processor;
	}

	@Override
	public void vibrate(int milliseconds) {
	}

	@Override
	public boolean justTouched() {
		return justTouched;
	}

	@Override
	public boolean isButtonPressed(int button) {
		return pressedButtons.contains(button);
	}

	@Override
	public void vibrate(long[] pattern, int repeat) {
	}

	@Override
	public void cancelVibrate() {
	}

	@Override
	public float getAzimuth() {
		return 0;
	}

	@Override
	public float getPitch() {
		return 0;
	}

	@Override
	public float getRoll() {
		return 0;
	}

	@Override
	public boolean isPeripheralAvailable(Peripheral peripheral) {
		if (peripheral == Peripheral.HardwareKeyboard)
			return true;
		return false;
	}

	@Override
	public int getRotation() {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation() {
		return Orientation.Landscape;
	}

	@Override
	public void setCursorCatched(boolean catched) {
	}

	@Override
	public boolean isCursorCatched() {
		return false;
	}

	@Override
	public int getDeltaX() {
		return deltaX;
	}

	@Override
	public int getDeltaX(int pointer) {
		if (pointer == 0)
			return deltaX;
		else
			return 0;
	}

	@Override
	public int getDeltaY() {
		return -deltaY;
	}

	@Override
	public int getDeltaY(int pointer) {
		if (pointer == 0)
			return -deltaY;
		else
			return 0;
	}

	@Override
	public void setCursorPosition(int x, int y) {
		Display dis = glCanvas.getDisplay();
		Shell s = glCanvas.getShell();
		if (dis != null && s != null) {
			dis.setCursorLocation(s.getLocation().x + x, s.getLocation().y + y);
		}
	}

	@Override
	public void setCursorImage(Pixmap pixmap, int xHotspot, int yHotspot) {
		if (pixmap == null) {
			glCanvas.setCursor(null);
			return;
		}

		if (pixmap.getFormat() != Pixmap.Format.RGBA8888) {
			throw new GdxRuntimeException("Cursor image pixmap is not in RGBA8888 format.");
		}

		if ((pixmap.getWidth() & (pixmap.getWidth() - 1)) != 0) {
			throw new GdxRuntimeException("Cursor image pixmap width of " + pixmap.getWidth()
					+ " is not a power-of-two greater than zero.");
		}

		if ((pixmap.getHeight() & (pixmap.getHeight() - 1)) != 0) {
			throw new GdxRuntimeException("Cursor image pixmap height of " + pixmap.getHeight()
					+ " is not a power-of-two greater than zero.");
		}

		if (xHotspot < 0 || xHotspot >= pixmap.getWidth()) {
			throw new GdxRuntimeException("xHotspot coordinate of " + xHotspot
					+ " is not within image width bounds: [0, " + pixmap.getWidth() + ").");
		}

		if (yHotspot < 0 || yHotspot >= pixmap.getHeight()) {
			throw new GdxRuntimeException("yHotspot coordinate of " + yHotspot
					+ " is not within image height bounds: [0, " + pixmap.getHeight() + ").");
		}

		PaletteData palette = new PaletteData(0xFF00, 0xFF0000, 0xFF000000);
		ImageData imageData = new ImageData(pixmap.getWidth(), pixmap.getHeight(), 32, palette);
		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				int rgba = pixmap.getPixel(x, y);
				imageData.setPixel(x, y, rgba);
				imageData.setAlpha(x, y, rgba & 0x000000ff);
			}
		}

		glCanvas.setCursor(new Cursor(glCanvas.getDisplay(), imageData, xHotspot, pixmap.getHeight() - yHotspot - 4));
	}

	@Override
	public void setCatchMenuKey(boolean catchMenu) {
	}

	@Override
	public long getCurrentEventTime() {
		return currentEventTimeStamp;
	}

	@Override
	public void getRotationMatrix(float[] matrix) {
	}

	class KeyEvent {
		static final int KEY_DOWN = 0;
		static final int KEY_UP = 1;
		static final int KEY_TYPED = 2;

		long timeStamp;
		int type;
		int keyCode;
		char keyChar;
	}

	class TouchEvent {
		static final int TOUCH_DOWN = 0;
		static final int TOUCH_UP = 1;
		static final int TOUCH_DRAGGED = 2;
		static final int TOUCH_SCROLLED = 3;
		static final int TOUCH_MOVED = 4;

		long timeStamp;
		int type;
		int x;
		int y;
		int scrollAmount;
		int button;
		int pointer;
	}

	private class MouseListener implements Listener {
		@Override
		public void handleEvent(Event e) {
			synchronized (SwtLwjglInput.this) {
				TouchEvent event = usedTouchEvents.obtain();
				event.x = e.x;
				event.y = e.y;// Gdx.graphics.getHeight() - e.y - 1;
				event.button = getGdxButton(e.button);
				event.pointer = 0;
				event.timeStamp = TimeUnit.MILLISECONDS.toNanos(e.time);

				switch (e.type) {
				case SWT.MouseDown:
					event.type = TouchEvent.TOUCH_DOWN;
					pressedButtons.add(event.button);
					justTouched = true;
					break;
				case SWT.MouseMove:
					if (pressedButtons.size > 0)
						event.type = TouchEvent.TOUCH_DRAGGED;
					else
						event.type = TouchEvent.TOUCH_MOVED;
					break;
				case SWT.MouseUp:
					event.type = TouchEvent.TOUCH_UP;
					pressedButtons.remove(event.button);
					break;
				case SWT.MouseWheel:
					event.type = TouchEvent.TOUCH_SCROLLED;
					event.scrollAmount = e.count;
					break;
				}

				touchEvents.add(event);
				mouseX = event.x;
				mouseY = event.y;
				deltaX = 0;
				deltaY = 0;
			}
		}

		private int getGdxButton(int button) {
			if (button == 1)
				return Buttons.LEFT;
			if (button == 3)
				return Buttons.RIGHT;
			if (button == 2)
				return Buttons.MIDDLE;
			return -1;
		}
	}

	private class KeyListener implements Listener {
		@Override
		public void handleEvent(Event e) {
			synchronized (SwtLwjglInput.this) {
				if (lastKeyCharPressed != 0) {
					keyRepeatTimer -= Gdx.graphics.getDeltaTime();
					if (keyRepeatTimer < 0) {
						keyRepeatTimer = 0.15f;
						KeyEvent event = usedKeyEvents.obtain();
						event.keyCode = 0;
						event.keyChar = lastKeyCharPressed;
						event.type = KeyEvent.KEY_TYPED;
						event.timeStamp = TimeUnit.MILLISECONDS.toNanos(e.time);
						keyEvents.add(event);
					}
				}

				int keyCode = getGdxKeyCode(e.keyCode, e.keyLocation, e.character);
				long timeStamp = TimeUnit.MILLISECONDS.toNanos(e.time);
				KeyEvent event = usedKeyEvents.obtain();

				switch (e.type) {
				case SWT.KeyDown:
					char keyChar = e.character;

					switch (keyCode) {
					case Keys.FORWARD_DEL:
						keyChar = 127;
						break;
					}

					event.keyCode = keyCode;
					event.keyChar = 0;
					event.type = KeyEvent.KEY_DOWN;
					event.timeStamp = timeStamp;
					keyEvents.add(event);

					event = usedKeyEvents.obtain();
					event.keyCode = 0;
					event.keyChar = keyChar;
					event.type = KeyEvent.KEY_TYPED;

					lastKeyCharPressed = keyChar;
					pressedKeys.add(keyCode);
					keyRepeatTimer = 0.4f;
					break;
				case SWT.KeyUp:
					event.keyCode = keyCode;
					event.keyChar = 0;
					event.type = KeyEvent.KEY_UP;

					lastKeyCharPressed = 0;
					if (pressedKeys.contains(keyCode))
						pressedKeys.remove(keyCode);
					break;
				}

				event.timeStamp = timeStamp;
				keyEvents.add(event);
			}
		}

		private int getGdxKeyCode(int swtKeyCode, int location, char character) {
			switch (swtKeyCode) {
			case SWT.KEYPAD_0:
				return Input.Keys.NUM_0;
			case SWT.KEYPAD_1:
				return Input.Keys.NUM_1;
			case SWT.KEYPAD_2:
				return Input.Keys.NUM_2;
			case SWT.KEYPAD_3:
				return Input.Keys.NUM_3;
			case SWT.KEYPAD_4:
				return Input.Keys.NUM_4;
			case SWT.KEYPAD_5:
				return Input.Keys.NUM_5;
			case SWT.KEYPAD_6:
				return Input.Keys.NUM_6;
			case SWT.KEYPAD_7:
				return Input.Keys.NUM_7;
			case SWT.KEYPAD_8:
				return Input.Keys.NUM_8;
			case SWT.KEYPAD_9:
				return Input.Keys.NUM_9;
			case SWT.ALPHA:
				switch (character) {
				case '0':
					return Input.Keys.NUM_0;
				case '1':
					return Input.Keys.NUM_1;
				case '2':
					return Input.Keys.NUM_2;
				case '3':
					return Input.Keys.NUM_3;
				case '4':
					return Input.Keys.NUM_4;
				case '5':
					return Input.Keys.NUM_5;
				case '6':
					return Input.Keys.NUM_6;
				case '7':
					return Input.Keys.NUM_7;
				case '8':
					return Input.Keys.NUM_8;
				case '9':
					return Input.Keys.NUM_9;
				case 'a':
					return Input.Keys.A;
				case 'b':
					return Input.Keys.B;
				case 'c':
					return Input.Keys.C;
				case 'd':
					return Input.Keys.D;
				case 'e':
					return Input.Keys.E;
				case 'f':
					return Input.Keys.F;
				case 'g':
					return Input.Keys.G;
				case 'h':
					return Input.Keys.H;
				case 'i':
					return Input.Keys.I;
				case 'j':
					return Input.Keys.J;
				case 'k':
					return Input.Keys.K;
				case 'l':
					return Input.Keys.L;
				case 'm':
					return Input.Keys.M;
				case 'n':
					return Input.Keys.N;
				case 'o':
					return Input.Keys.O;
				case 'p':
					return Input.Keys.P;
				case 'q':
					return Input.Keys.Q;
				case 'r':
					return Input.Keys.R;
				case 's':
					return Input.Keys.S;
				case 't':
					return Input.Keys.T;
				case 'u':
					return Input.Keys.U;
				case 'v':
					return Input.Keys.V;
				case 'w':
					return Input.Keys.W;
				case 'x':
					return Input.Keys.X;
				case 'y':
					return Input.Keys.Y;
				case 'z':
					return Input.Keys.Z;
				case '/':
					return Input.Keys.SLASH;
				case '\\':
					return Input.Keys.BACKSLASH;
				case ',':
					return Input.Keys.COMMA;
				case ';':
					return Input.Keys.SEMICOLON;
				case '+':
					return Input.Keys.PLUS;
				case '-':
					return Input.Keys.MINUS;
				case '.':
					return Input.Keys.PERIOD;
				case '\n':
					return Input.Keys.ENTER;
				case '\'':
					return Input.Keys.APOSTROPHE;
				case ':':
					return Input.Keys.COLON;
				case SWT.SPACE:
					return Input.Keys.SPACE;
				case SWT.DEL:
					if (location == SWT.LEFT)
						return Input.Keys.FORWARD_DEL;
					else
						return Input.Keys.DEL;
				case SWT.TAB:
					return Input.Keys.TAB;
				case SWT.ESC:
					return Input.Keys.ESCAPE;
				default:
					return Input.Keys.UNKNOWN;
				}
			case SWT.ALT:
				if (location == SWT.LEFT)
					return Input.Keys.ALT_LEFT;
				else
					return Input.Keys.ALT_RIGHT;
			case SWT.ARROW_LEFT:
				return Input.Keys.DPAD_LEFT;
			case SWT.ARROW_RIGHT:
				return Input.Keys.DPAD_RIGHT;
			case SWT.ARROW_UP:
				return Input.Keys.DPAD_UP;
			case SWT.ARROW_DOWN:
				return Input.Keys.DPAD_DOWN;
			case SWT.HOME:
				return Input.Keys.HOME;
			case SWT.SHIFT:
				if (location == SWT.LEFT)
					return Input.Keys.SHIFT_LEFT;
				else
					return Input.Keys.SHIFT_RIGHT;
			case SWT.CTRL:
				if (location == SWT.LEFT)
					return Input.Keys.CONTROL_LEFT;
				else
					return Input.Keys.CONTROL_RIGHT;
			case SWT.END:
				return Input.Keys.END;
			case SWT.INSERT:
				return Input.Keys.INSERT;
			case SWT.F1:
				return Input.Keys.F1;
			case SWT.F2:
				return Input.Keys.F2;
			case SWT.F3:
				return Input.Keys.F3;
			case SWT.F4:
				return Input.Keys.F4;
			case SWT.F5:
				return Input.Keys.F5;
			case SWT.F6:
				return Input.Keys.F6;
			case SWT.F7:
				return Input.Keys.F7;
			case SWT.F8:
				return Input.Keys.F8;
			case SWT.F9:
				return Input.Keys.F9;
			case SWT.F10:
				return Input.Keys.F10;
			case SWT.F11:
				return Input.Keys.F11;
			case SWT.F12:
				return Input.Keys.F12;
			default:
				return Input.Keys.UNKNOWN;
			}
		}
	}
}
