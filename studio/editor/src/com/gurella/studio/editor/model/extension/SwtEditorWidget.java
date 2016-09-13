package com.gurella.studio.editor.model.extension;

import static com.gurella.studio.editor.model.extension.event.SwtEditorEventType.toSwtConstant;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IdentityMap;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorWidget;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.extension.event.SwtListenerBridge;

public abstract class SwtEditorWidget<T extends Widget> implements EditorWidget {
	static final IdentityMap<Widget, EditorWidget> instances = new IdentityMap<>();

	T widget;

	SwtEditorWidget() {
	}

	public SwtEditorWidget(SwtEditorBaseComposite<?> parent, int style) {
		init(createWidget(parent.widget, style));
	}

	void init(T widget) {
		this.widget = widget;
		widget.addListener(SWT.Dispose, e -> instances.remove(widget));
		instances.put(widget, this);
	}

	abstract T createWidget(Composite parent, int style);

	public T getWidget() {
		return widget;
	}

	public static <T extends EditorWidget> T getEditorWidget(Widget widget) {
		return Values.cast(instances.get(widget));
	}

	@Override
	public void addListener(EditorEventType eventType, EditorEventListener listener) {
		widget.addListener(toSwtConstant(eventType), new SwtListenerBridge(listener));
	}

	@Override
	public EditorEventListener[] getListeners(EditorEventType eventType) {
		Listener[] listeners = widget.getListeners(toSwtConstant(eventType));
		return Arrays.stream(listeners).sequential().filter(l -> l instanceof SwtListenerBridge)
				.map(l -> ((SwtListenerBridge) l).listener).toArray(i -> new EditorEventListener[i]);
	}

	@Override
	public void removeListener(EditorEventType eventType, EditorEventListener listener) {
		widget.removeListener(toSwtConstant(eventType), new SwtListenerBridge(listener));
	}

	@Override
	public boolean isListening(EditorEventType eventType) {
		return widget.isListening(toSwtConstant(eventType));
	}

	@Override
	public boolean isDisposed() {
		return widget.isDisposed();
	}

	@Override
	public void dispose() {
		widget.dispose();
	}

	@Override
	public <V> V getData(String key) {
		return Values.cast(widget.getData(key));
	}

	@Override
	public void setData(String key, Object value) {
		widget.setData(key, value);
	}

	@Override
	public SwtEditorUi getUiFactory() {
		return SwtEditorUi.instance;
	}

	protected org.eclipse.swt.graphics.Color toSwtColor(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		return swtColor;
	}

	protected org.eclipse.swt.graphics.Color toSwtColor(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		return swtColor;
	}

	protected static Color toGdxColor(org.eclipse.swt.graphics.Color color) {
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
	}

	protected static Image toSwtImage(EditorImage editorImage) {
		return editorImage == null ? null : ((SwtEditorImage) editorImage).image;
	}

	protected Image toSwtImage(InputStream imageStream) {
		return toSwtImage(widget, imageStream);
	}

	protected static Image toSwtImage(Widget widget, InputStream imageStream) {
		if (imageStream == null) {
			return null;
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			return image;
		}
	}

	protected static SwtEditorImage toEditorImage(Image image) {
		return image == null ? null : new SwtEditorImage(image);
	}

	protected static Font toSwtFont(EditorFont editorFont) {
		return editorFont == null ? null : ((SwtEditorFont) editorFont).font;
	}

	protected Font toSwtFont(String name, int height, boolean bold, boolean italic) {
		Font font = SwtEditorUi.instance.createSwtFont(name, height, bold, italic);
		if (font != null) {
			widget.addDisposeListener(e -> font.dispose());
		}
		return font;
	}

	protected Font toSwtFont(Font originalFont, int height, boolean bold, boolean italic) {
		Font font = SwtEditorUi.instance.createSwtFont(originalFont, height, bold, italic);
		if (font != null) {
			widget.addDisposeListener(e -> font.dispose());
		}
		return font;
	}

	protected static SwtEditorFont toEditorFont(Font font) {
		return font == null ? null : new SwtEditorFont(font);
	}
}
