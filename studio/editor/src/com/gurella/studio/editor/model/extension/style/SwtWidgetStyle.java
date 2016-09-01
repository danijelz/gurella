package com.gurella.studio.editor.model.extension.style;

import org.eclipse.swt.SWT;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.Direction;
import com.gurella.engine.editor.ui.EditorButton.ArrowDirection;
import com.gurella.engine.editor.ui.EditorButton.BaseButtonStyle;
import com.gurella.engine.editor.ui.EditorCombo.ComboStyle;
import com.gurella.engine.editor.ui.EditorControl.ControlStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateTimeLength;
import com.gurella.engine.editor.ui.EditorLabel.BaseLabelStyle;
import com.gurella.engine.editor.ui.EditorLabel.ShadowType;
import com.gurella.engine.editor.ui.EditorList.ListStyle;
import com.gurella.engine.editor.ui.EditorScrollable.ScrollableStyle;
import com.gurella.engine.editor.ui.EditorShell.Modality;
import com.gurella.engine.editor.ui.EditorShell.ShellStyle;
import com.gurella.engine.editor.ui.EditorSpinner.SpinnerStyle;
import com.gurella.engine.editor.ui.EditorTabFolder.TabFolderStyle;
import com.gurella.engine.editor.ui.EditorTable.TableStyle;
import com.gurella.engine.editor.ui.EditorText.TextStyle;
import com.gurella.engine.editor.ui.EditorToolBar.ToolBarStyle;
import com.gurella.engine.editor.ui.EditorTree.TreeStyle;

public class SwtWidgetStyle {
	private static int extractControlStyle(ControlStyle<?> style) {
		int result = 0;
		if (style.textDirection != null) {
			result |= style.textDirection == Direction.leftToRight ? SWT.LEFT_TO_RIGHT : SWT.RIGHT_TO_LEFT;
		}

		if (style.border) {
			result |= SWT.BORDER;
		}

		if (style.flipTextDirection) {
			result |= SWT.FLIP_TEXT_DIRECTION;
		}

		return result;
	}

	private static int extractScrollableStyle(ScrollableStyle<?> style) {
		int result = extractControlStyle(style);

		if (style.hScroll) {
			result |= SWT.H_SCROLL;
		}

		if (style.vScroll) {
			result |= SWT.V_SCROLL;
		}

		return result;
	}

	public static int extractToolBarStyle(ToolBarStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);

		if (style.wrap) {
			result |= SWT.WRAP;
		}

		if (style.right) {
			result |= SWT.RIGHT;
		}

		if (style.flat) {
			result |= SWT.FLAT;
		}

		if (style.shadowOut) {
			result |= SWT.SHADOW_OUT;
		}

		return result;
	}

	public static int extractSimpleScrollableStyle(ScrollableStyle<?> style) {
		return style == null ? SWT.NONE : extractScrollableStyle(style);
	}

	public static int extractTableStyle(TableStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);
		result |= (style.multiSelection ? SWT.MULTI : SWT.SINGLE);

		if (style.check) {
			result |= SWT.CHECK;
		}

		if (style.fullSelection) {
			result |= SWT.FULL_SELECTION;
		}

		if (style.hideSelection) {
			result |= SWT.HIDE_SELECTION;
		}

		if (style.virtual) {
			result |= SWT.VIRTUAL;
		}

		if (style.noScroll) {
			result |= SWT.NO_SCROLL;
		}

		return result;
	}

	public static int extractTreeStyle(TreeStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);
		result |= (style.multiSelection ? SWT.MULTI : SWT.SINGLE);

		if (style.check) {
			result |= SWT.CHECK;
		}

		if (style.fullSelection) {
			result |= SWT.FULL_SELECTION;
		}

		if (style.virtual) {
			result |= SWT.VIRTUAL;
		}

		if (style.noScroll) {
			result |= SWT.NO_SCROLL;
		}

		return result;
	}

	public static int extractSimpleControlStyle(ControlStyle<?> style) {
		return style == null ? SWT.NONE : extractControlStyle(style);
	}

	public static int extractLabelStyle(BaseLabelStyle<?> style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractControlStyle(style);

		if (style.alignment != null) {
			result |= alignment(style.alignment);
		}

		if (style.shadowType != null) {
			result |= (style.shadowType == ShadowType.SHADOW_IN ? SWT.SHADOW_IN : SWT.SHADOW_OUT);
		}

		if (style.wrap) {
			result |= SWT.WRAP;
		}

		return result;
	}

	public static int alignment(Alignment alignment) {
		switch (alignment) {
		case LEFT:
			return SWT.LEFT;
		case CENTER:
			return SWT.CENTER;
		case RIGHT:
			return SWT.RIGHT;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static Alignment alignment(int alignment) {
		switch (alignment) {
		case SWT.LEFT:
			return Alignment.LEFT;
		case SWT.CENTER:
			return Alignment.CENTER;
		case SWT.RIGHT:
			return Alignment.RIGHT;
		default:
			return null;
		}
	}

	public static int extractSpinnerStyle(SpinnerStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);

		if (style.wrap) {
			result |= SWT.WRAP;
		}

		if (style.readOnly) {
			result |= SWT.READ_ONLY;
		}

		return result;
	}

	public static int length(DateTimeLength length) {
		switch (length) {
		case SHORT:
			return SWT.SHORT;
		case MEDIUM:
			return SWT.MEDIUM;
		case LONG:
			return SWT.LONG;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static int extractComboStyle(ComboStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);

		if (style.readOnly) {
			result |= SWT.READ_ONLY;
		}

		return result;
	}

	public static int extractTabFolderStyle(TabFolderStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);
		result |= style.bottom ? SWT.BOTTOM : SWT.TOP;

		return result;
	}

	public static int extractListStyle(ListStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);
		result |= style.multi ? SWT.MULTI : SWT.SINGLE;

		return result;
	}

	public static int extractTextStyle(TextStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);
		if (style.alignment != null) {
			result |= alignment(style.alignment);
		}

		if (style.wrap) {
			result |= SWT.WRAP;
		}

		if (style.readOnly) {
			result |= SWT.READ_ONLY;
		}

		if (style.password) {
			result |= SWT.PASSWORD;
		}

		return result;
	}

	public static int extractButtonStyle(BaseButtonStyle<?> style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractControlStyle(style);
		if (style.alignment != null) {
			result |= alignment(style.alignment);
		}

		if (style.wrap) {
			result |= SWT.WRAP;
		}

		if (style.flat) {
			result |= SWT.FLAT;
		}

		return result;
	}

	public static int arrowDirection(ArrowDirection arrowDirection) {
		switch (arrowDirection) {
		case UP:
			return SWT.UP;
		case DOWN:
			return SWT.DOWN;
		case LEFT:
			return SWT.LEFT;
		case RIGHT:
			return SWT.RIGHT;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static int extractShellStyle(ShellStyle style) {
		if (style == null) {
			return SWT.NONE;
		}

		int result = extractScrollableStyle(style);

		if (style.modality != null) {
			result |= modality(style.modality);
		}

		if (style.close) {
			result |= SWT.CLOSE;
		}

		if (style.min) {
			result |= SWT.MIN;
		}

		if (style.max) {
			result |= SWT.MAX;
		}

		if (style.noTrim) {
			result |= SWT.NO_TRIM;
		}

		if (style.resize) {
			result |= SWT.RESIZE;
		}

		if (style.title) {
			result |= SWT.TITLE;
		}

		if (style.onTop) {
			result |= SWT.ON_TOP;
		}

		if (style.tool) {
			result |= SWT.TOOL;
		}

		if (style.sheet) {
			result |= SWT.SHEET;
		}

		return result;
	}

	private static int modality(Modality modality) {
		switch (modality) {
		case APPLICATION_MODAL:
			return SWT.APPLICATION_MODAL;
		case MODELESS:
			return SWT.MODELESS;
		case PRIMARY_MODAL:
			return SWT.PRIMARY_MODAL;
		case SYSTEM_MODAL:
			return SWT.SYSTEM_MODAL;
		default:
			throw new IllegalArgumentException();
		}
	}
}
