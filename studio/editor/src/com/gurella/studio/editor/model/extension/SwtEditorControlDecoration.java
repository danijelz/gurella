package com.gurella.studio.editor.model.extension;

import static org.eclipse.jface.fieldassist.FieldDecorationRegistry.DEC_ERROR;
import static org.eclipse.jface.fieldassist.FieldDecorationRegistry.DEC_INFORMATION;
import static org.eclipse.jface.fieldassist.FieldDecorationRegistry.DEC_REQUIRED;
import static org.eclipse.jface.fieldassist.FieldDecorationRegistry.DEC_WARNING;

import java.io.InputStream;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorControlDecoration;
import com.gurella.engine.editor.ui.EditorImage;

public class SwtEditorControlDecoration implements EditorControlDecoration {
	private ControlDecoration decoration;

	public SwtEditorControlDecoration(SwtEditorControl<?> control, HorizontalAlignment horizontalAlignment,
			VerticalAlignment verticalAlignment) {
		this.decoration = new ControlDecoration(control.widget, position(horizontalAlignment, verticalAlignment));
	}

	private static int position(HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		int position = 0;

		switch (horizontalAlignment) {
		case CENTER:
			position |= SWT.CENTER;
			break;
		case LEFT:
			position |= SWT.LEFT;
			break;
		case RIGHT:
			position |= SWT.RIGHT;
			break;
		}

		switch (verticalAlignment) {
		case CENTER:
			position |= SWT.CENTER;
			break;
		case TOP:
			position |= SWT.TOP;
			break;
		case BOTTOM:
			position |= SWT.BOTTOM;
			break;
		}

		return position;
	}

	@Override
	public void dispose() {
		decoration.dispose();
		((SwtEditorControl<?>) getControl()).decoration = null;
	}

	@Override
	public EditorControl getControl() {
		return SwtEditorWidget.getEditorWidget(decoration.getControl());
	}

	@Override
	public String getDescriptionText() {
		return decoration.getDescriptionText();
	}

	@Override
	public EditorImage getImage() {
		Image image = decoration.getImage();
		return image == null ? null : new SwtEditorImage(image);
	}

	@Override
	public int getMarginWidth() {
		return decoration.getMarginWidth();
	}

	@Override
	public boolean getShowHover() {
		return decoration.getShowHover();
	}

	@Override
	public boolean getShowOnlyOnFocus() {
		return decoration.getShowOnlyOnFocus();
	}

	@Override
	public void hide() {
		decoration.hide();
	}

	@Override
	public void hideHover() {
		decoration.hideHover();
	}

	@Override
	public boolean isVisible() {
		return decoration.isVisible();
	}

	@Override
	public void setDescriptionText(String text) {
		decoration.setDescriptionText(text);
	}

	@Override
	public void setImage(EditorImage image) {
		decoration.setImage(image == null ? null : ((SwtEditorImage) image).image);
	}

	@Override
	public void setImage(InputStream imageStream) {
		if (imageStream == null) {
			decoration.setImage(null);
		} else {
			Control control = decoration.getControl();
			Image image = new Image(control.getDisplay(), imageStream);
			control.addListener(SWT.Dispose, e -> image.dispose());
			decoration.setImage(image);
		}
	}

	@Override
	public void setMarginWidth(int marginWidth) {
		decoration.setMarginWidth(marginWidth);
	}

	@Override
	public void setShowHover(boolean showHover) {
		decoration.setShowHover(showHover);
	}

	@Override
	public void setShowOnlyOnFocus(boolean showOnlyOnFocus) {
		decoration.setShowOnlyOnFocus(showOnlyOnFocus);
	}

	@Override
	public void show() {
		decoration.show();
	}

	@Override
	public void showHoverText(String text) {
		decoration.showHoverText(text);
	}

	@Override
	public void setInfoImage() {
		decoration.setImage(getFieldDecorationImage(DEC_INFORMATION));
	}

	private static Image getFieldDecorationImage(String id) {
		return FieldDecorationRegistry.getDefault().getFieldDecoration(id).getImage();
	}

	private static String getFieldDecorationDescription(String id) {
		return FieldDecorationRegistry.getDefault().getFieldDecoration(id).getDescription();
	}

	@Override
	public void setErrorImage() {
		decoration.setImage(getFieldDecorationImage(DEC_ERROR));
	}

	@Override
	public void setRequiredImage() {
		decoration.setImage(getFieldDecorationImage(DEC_REQUIRED));
	}

	@Override
	public void setWarningImage() {
		decoration.setImage(getFieldDecorationImage(DEC_WARNING));
	}
}
