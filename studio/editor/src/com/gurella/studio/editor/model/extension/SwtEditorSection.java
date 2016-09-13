package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorSection;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorSection extends SwtEditorBaseExpandableComposite<Section> implements EditorSection {
	SwtEditorSection(SwtEditorLayoutComposite<?> parent, int style) {
		super(GurellaStudioPlugin.getToolkit().createSection(parent.widget, style));
	}

	@Override
	public String getDescription() {
		return widget.getDescription();
	}

	@Override
	public SwtEditorControl<?> getDescriptionControl() {
		Control control = widget.getDescriptionControl();
		if (control == null) {
			return null;
		} else {
			SwtEditorControl<?> editorControl = getEditorWidget(control);
			return editorControl == null ? new SwtEditorText((Text) control) : editorControl;
		}
	}

	@Override
	public SwtEditorControl<?> getSeparatorControl() {
		return getEditorWidget(widget.getSeparatorControl());
	}

	@Override
	public Color getTitleBarBackground() {
		return toGdxColor(widget.getTitleBarBackground());
	}

	@Override
	public Color getTitleBarBorderColor() {
		return toGdxColor(widget.getTitleBarBorderColor());
	}

	@Override
	public Color getTitleBarGradientBackground() {
		return toGdxColor(widget.getTitleBarGradientBackground());
	}

	@Override
	public void setBackgroundImage(EditorImage image) {
		widget.setBackgroundImage(toSwtImage(image));
	}

	@Override
	public void setBackgroundImage(InputStream imageStream) {
		widget.setBackgroundImage(toSwtImage(imageStream));
	}

	@Override
	public void setDescription(String description) {
		widget.setDescription(description);
	}

	@Override
	public void setDescriptionControl(EditorControl control) {
		widget.setDescriptionControl(control == null ? null : ((SwtEditorControl<?>) control).widget);
	}

	@Override
	public void setSeparatorControl(EditorControl separator) {
		widget.setSeparatorControl(separator == null ? null : ((SwtEditorControl<?>) separator).widget);
	}

	@Override
	public void setTitleBarBackground(Color color) {
		widget.setTitleBarBackground(toSwtColor(color));
	}

	@Override
	public void setTitleBarBackground(int r, int g, int b, int a) {
		widget.setTitleBarBackground(toSwtColor(r, g, b, a));
	}

	@Override
	public void setTitleBarBorderColor(Color color) {
		widget.setTitleBarBorderColor(toSwtColor(color));
	}

	@Override
	public void setTitleBarBorderColor(int r, int g, int b, int a) {
		widget.setTitleBarBorderColor(toSwtColor(r, g, b, a));
	}

	@Override
	public void setTitleBarGradientBackground(Color color) {
		widget.setTitleBarGradientBackground(toSwtColor(color));
	}

	@Override
	public void setTitleBarGradientBackground(int r, int g, int b, int a) {
		widget.setTitleBarGradientBackground(toSwtColor(r, g, b, a));
	}
}
