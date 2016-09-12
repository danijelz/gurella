package com.gurella.studio.editor.model.extension;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
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
		super(parent, style);
	}

	@Override
	Section createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createSection(parent, style);
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
		return SwtEditorUi.toGdxColor(widget.getTitleBarBackground());
	}

	@Override
	public Color getTitleBarBorderColor() {
		return SwtEditorUi.toGdxColor(widget.getTitleBarBorderColor());
	}

	@Override
	public Color getTitleBarGradientBackground() {
		return SwtEditorUi.toGdxColor(widget.getTitleBarGradientBackground());
	}

	@Override
	public void setBackgroundImage(EditorImage image) {
		widget.setBackgroundImage(image == null ? null : ((SwtEditorImage) image).image);
	}

	@Override
	public void setBackgroundImage(InputStream imageStream) {
		if (imageStream == null) {
			widget.setBackgroundImage(null);
		} else {
			Image image = new Image(widget.getDisplay(), imageStream);
			widget.addListener(SWT.Dispose, e -> image.dispose());
			widget.setBackgroundImage(image);
		}
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
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarBackground(swtColor);
	}

	@Override
	public void setTitleBarBackground(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarBackground(swtColor);
	}

	@Override
	public void setTitleBarBorderColor(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarBorderColor(swtColor);
	}

	@Override
	public void setTitleBarBorderColor(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarBorderColor(swtColor);
	}

	@Override
	public void setTitleBarGradientBackground(Color color) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(color);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarGradientBackground(swtColor);
	}

	@Override
	public void setTitleBarGradientBackground(int r, int g, int b, int a) {
		org.eclipse.swt.graphics.Color swtColor = GurellaStudioPlugin.createColor(r, g, b, a);
		widget.addListener(SWT.Dispose, e -> GurellaStudioPlugin.destroyColor(swtColor));
		widget.setTitleBarGradientBackground(swtColor);
	}
}
