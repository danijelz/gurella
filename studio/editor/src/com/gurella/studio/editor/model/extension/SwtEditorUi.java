package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.studio.GurellaStudioPlugin.createErrorStatus;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.arrowDirection;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractButtonStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractComboStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractLabelStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractShellStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractSimpleControlStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractSimpleScrollableStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractSpinnerStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractTabFolderStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractTableStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractTextStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractToolBarStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.extractTreeStyle;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.length;

import java.io.InputStream;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.ui.EditorButton;
import com.gurella.engine.editor.ui.EditorButton.ArrowButtonStyle;
import com.gurella.engine.editor.ui.EditorButton.ArrowDirection;
import com.gurella.engine.editor.ui.EditorButton.ButtonStyle;
import com.gurella.engine.editor.ui.EditorButton.CheckBoxStyle;
import com.gurella.engine.editor.ui.EditorButton.RadioButtonStyle;
import com.gurella.engine.editor.ui.EditorButton.ToggleButtonStyle;
import com.gurella.engine.editor.ui.EditorCombo.ComboStyle;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorComposite.CompositeStyle;
import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorDateTime.CalendarStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateTimeLength;
import com.gurella.engine.editor.ui.EditorDateTime.DropDownDateStyle;
import com.gurella.engine.editor.ui.EditorDateTime.TimeStyle;
import com.gurella.engine.editor.ui.EditorExpandBar.ExpandBarStyle;
import com.gurella.engine.editor.ui.EditorFont;
import com.gurella.engine.editor.ui.EditorGroup.GroupStyle;
import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.editor.ui.EditorInputValidator;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorLabel.LabelStyle;
import com.gurella.engine.editor.ui.EditorLabel.SeparatorStyle;
import com.gurella.engine.editor.ui.EditorLink.LinkStyle;
import com.gurella.engine.editor.ui.EditorList.ListStyle;
import com.gurella.engine.editor.ui.EditorLogLevel;
import com.gurella.engine.editor.ui.EditorProgressBar.ProgressBarStyle;
import com.gurella.engine.editor.ui.EditorSash.SashStyle;
import com.gurella.engine.editor.ui.EditorSashForm.SashFormStyle;
import com.gurella.engine.editor.ui.EditorScale.ScaleStyle;
import com.gurella.engine.editor.ui.EditorShell;
import com.gurella.engine.editor.ui.EditorShell.ShellStyle;
import com.gurella.engine.editor.ui.EditorSlider.SliderStyle;
import com.gurella.engine.editor.ui.EditorSpinner.SpinnerStyle;
import com.gurella.engine.editor.ui.EditorTabFolder.TabFolderStyle;
import com.gurella.engine.editor.ui.EditorTable.TableStyle;
import com.gurella.engine.editor.ui.EditorText.TextStyle;
import com.gurella.engine.editor.ui.EditorToolBar.ToolBarStyle;
import com.gurella.engine.editor.ui.EditorTree.TreeContentProvider;
import com.gurella.engine.editor.ui.EditorTree.TreeStyle;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.dialog.EditorDialog.EditorDialogProperties;
import com.gurella.engine.editor.ui.dialog.EditorTitleAreaDialog.EditorTitleAteaDialogProperties;
import com.gurella.engine.editor.ui.layout.EditorLayout;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.HorizontalAlignment;
import com.gurella.engine.editor.ui.layout.EditorLayoutData.VerticalAlignment;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

//TODO import methods from UiUtils
public class SwtEditorUi implements EditorUi {
	public static final SwtEditorUi instance = new SwtEditorUi();

	private SwtEditorUi() {
	}

	@Override
	public void log(EditorLogLevel level, String message) {
		GurellaStudioPlugin.log(level, message);
	}

	@Override
	public void logError(Throwable t, String message) {
		GurellaStudioPlugin.log(t, message);
	}

	@Override
	public EditorImage createImage(InputStream imageStream) {
		return new SwtEditorImage(new Image(getDisplay(), imageStream));
	}

	@Override
	public SwtEditorFont createFont(String name, int height, boolean bold, boolean italic) {
		Font font = createSwtFont(name, height, bold, italic);
		return font == null ? null : new SwtEditorFont(font);
	}

	public Font createSwtFont(String name, int height, boolean bold, boolean italic) {
		return FontDescriptor.createFrom(name, height, getFontStyle(bold, italic)).createFont(getDisplay());
	}

	protected static int getFontStyle(boolean bold, boolean italic) {
		int style = bold ? SWT.BOLD : 0;
		style |= italic ? SWT.ITALIC : SWT.NORMAL;
		return style;
	}

	@Override
	public SwtEditorFont createFont(EditorFont initial, int height, boolean bold, boolean italic) {
		Font oldFont = ((SwtEditorFont) initial).font;
		Font font = createSwtFont(oldFont, height, bold, italic);
		return font == null ? null : new SwtEditorFont(font);
	}

	protected Font createSwtFont(Font oldFont, int height, boolean bold, boolean italic) {
		if (oldFont == null) {
			return null;
		}

		int style = getFontStyle(bold, italic);
		Font font = FontDescriptor.createFrom(oldFont).setHeight(height).setStyle(style).createFont(getDisplay());
		return font;
	}

	@Override
	public SwtEditorFont createFont(EditorControl control, int height, boolean bold, boolean italic) {
		Font oldFont = ((SwtEditorControl<?>) control).widget.getFont();
		Font font = createSwtFont(oldFont, height, bold, italic);
		return font == null ? null : new SwtEditorFont(font);
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public static Shell getShell() {
		return getDisplay().getActiveShell();
	}

	public static Color toGdxColor(org.eclipse.swt.graphics.Color color) {
		return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
	}

	public static SwtEditorComposite createComposite(Composite parent) {
		return new SwtEditorComposite(parent);
	}

	@Override
	public SwtEditorComposite createComposite(EditorComposite parent) {
		return new SwtEditorComposite(cast(parent), SWT.NONE);
	}

	@Override
	public SwtEditorComposite createComposite(EditorComposite parent, CompositeStyle style) {
		return new SwtEditorComposite(cast(parent), extractSimpleScrollableStyle(style));
	}

	@Override
	public SwtEditorGroup createGroup(EditorComposite parent) {
		return new SwtEditorGroup(cast(parent), SWT.NONE);
	}

	@Override
	public SwtEditorGroup createGroup(EditorComposite parent, GroupStyle style) {
		return new SwtEditorGroup(cast(parent), extractSimpleScrollableStyle(style));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent) {
		return new SwtEditorLabel(cast(parent), SWT.NONE);
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, LabelStyle style) {
		return new SwtEditorLabel(cast(parent), extractLabelStyle(style));
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text) {
		return new SwtEditorLabel(cast(parent), text, SWT.NONE);
	}

	@Override
	public EditorLabel createLabel(EditorComposite parent, String text, LabelStyle style) {
		return new SwtEditorLabel(cast(parent), text, extractLabelStyle(style));
	}

	@Override
	public SwtEditorLabel createSeparator(EditorComposite parent, boolean vertical) {
		return new SwtEditorLabel(cast(parent), SWT.SEPARATOR | orientation(vertical));
	}

	@Override
	public SwtEditorLabel createSeparator(EditorComposite parent, SeparatorStyle style) {
		return new SwtEditorLabel(cast(parent), SWT.SEPARATOR | orientation(style.vertical) | extractLabelStyle(style));
	}

	protected int orientation(boolean vertical) {
		return vertical ? SWT.VERTICAL : SWT.HORIZONTAL;
	}

	@Override
	public SwtEditorLink createLink(EditorComposite parent) {
		return new SwtEditorLink(cast(parent), SWT.NONE);
	}

	@Override
	public SwtEditorLink createLink(EditorComposite parent, LinkStyle style) {
		return new SwtEditorLink(cast(parent), extractSimpleControlStyle(style));
	}

	@Override
	public SwtEditorLink createLink(EditorComposite parent, String text) {
		SwtEditorLink link = createLink(parent);
		link.setText(text);
		return link;
	}

	@Override
	public SwtEditorLink createLink(EditorComposite parent, String text, LinkStyle style) {
		SwtEditorLink link = createLink(parent, style);
		link.setText(text);
		return link;
	}

	@Override
	public SwtEditorProgressBar createProgressBar(EditorComposite parent, boolean vertical, boolean smooth,
			boolean indeterminate) {
		int style = orientation(vertical);
		if (smooth) {
			style |= SWT.SMOOTH;
		}
		if (indeterminate) {
			style |= SWT.INDETERMINATE;
		}
		return new SwtEditorProgressBar(cast(parent), style);
	}

	@Override
	public SwtEditorProgressBar createProgressBar(EditorComposite parent, ProgressBarStyle style) {
		int result = orientation(style.vertical) | extractSimpleControlStyle(style);
		if (style.smooth) {
			result |= SWT.SMOOTH;
		}
		if (style.indeterminate) {
			result |= SWT.INDETERMINATE;
		}
		return new SwtEditorProgressBar(cast(parent), result);
	}

	@Override
	public SwtEditorSash createSash(EditorComposite parent, boolean vertical, boolean smooth) {
		int style = orientation(vertical);
		if (smooth) {
			style |= SWT.SMOOTH;
		}
		return new SwtEditorSash(cast(parent), style);
	}

	@Override
	public SwtEditorSash createSash(EditorComposite parent, SashStyle style) {
		int result = orientation(style.vertical) | extractSimpleControlStyle(style);
		if (style.smooth) {
			result |= SWT.SMOOTH;
		}
		return new SwtEditorSash(cast(parent), result);
	}

	@Override
	public SwtEditorScale createScale(EditorComposite parent, boolean vertical) {
		return new SwtEditorScale(cast(parent), orientation(vertical));
	}

	@Override
	public SwtEditorScale createScale(EditorComposite parent, ScaleStyle style) {
		return new SwtEditorScale(cast(parent), orientation(style.vertical) | extractSimpleControlStyle(style));
	}

	@Override
	public SwtEditorSlider createSlider(EditorComposite parent, boolean vertical) {
		return new SwtEditorSlider(cast(parent), orientation(vertical));
	}

	@Override
	public SwtEditorSlider createSlider(EditorComposite parent, SliderStyle style) {
		return new SwtEditorSlider(cast(parent), orientation(style.vertical) | extractSimpleControlStyle(style));
	}

	@Override
	public <ELEMENT> SwtEditorList<ELEMENT> createList(EditorComposite parent, boolean multi) {
		return new SwtEditorList<ELEMENT>(cast(parent), multi ? SWT.MULTI : SWT.SINGLE);
	}

	@Override
	public <ELEMENT> SwtEditorList<ELEMENT> createList(EditorComposite parent, ListStyle style) {
		return new SwtEditorList<ELEMENT>(cast(parent), SwtWidgetStyle.extractListStyle(style));
	}

	@Override
	public SwtEditorText createText(EditorComposite parent) {
		return createText(parent, false, SWT.SINGLE);
	}

	protected SwtEditorText createText(EditorComposite parent, boolean formBorder, int style) {
		SwtEditorText text = new SwtEditorText(cast(parent), style);
		if (formBorder) {
			text.widget.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			text.getParent().paintBorders();
		}
		return text;
	}

	@Override
	public SwtEditorText createText(EditorComposite parent, TextStyle style) {
		return createText(cast(parent), style.formBorder, SWT.SINGLE | extractTextStyle(style));
	}

	@Override
	public SwtEditorText createTextArea(EditorComposite parent) {
		return createText(cast(parent), false, SWT.MULTI);
	}

	@Override
	public SwtEditorText createTextArea(EditorComposite parent, TextStyle style) {
		return createText(cast(parent), style.formBorder, SWT.MULTI | extractTextStyle(style));
	}

	@Override
	public <ELEMENT> SwtEditorCombo<ELEMENT> createCombo(EditorComposite parent) {
		return new SwtEditorCombo<ELEMENT>(cast(parent), SWT.DROP_DOWN | SWT.READ_ONLY);
	}

	@Override
	public <ELEMENT> SwtEditorCombo<ELEMENT> createCombo(EditorComposite parent, ComboStyle style) {
		return new SwtEditorCombo<ELEMENT>(cast(parent), SWT.DROP_DOWN | extractComboStyle(style));
	}

	@Override
	public SwtEditorDateTime createDate(EditorComposite parent, DateTimeLength length) {
		return new SwtEditorDateTime(cast(parent), SWT.DATE | length(length));
	}

	@Override
	public SwtEditorDateTime createDate(EditorComposite parent, DateStyle style) {
		return new SwtEditorDateTime(cast(parent),
				SWT.DATE | SwtWidgetStyle.length(style.length) | extractSimpleScrollableStyle(style));
	}

	@Override
	public SwtEditorDateTime createDropDownDate(EditorComposite parent) {
		return new SwtEditorDateTime(cast(parent), SWT.DATE | SWT.DROP_DOWN);
	}

	@Override
	public SwtEditorDateTime createDropDownDate(EditorComposite parent, DropDownDateStyle style) {
		return new SwtEditorDateTime(cast(parent), SWT.DATE | SWT.DROP_DOWN | extractSimpleScrollableStyle(style));
	}

	@Override
	public SwtEditorDateTime createTime(EditorComposite parent, DateTimeLength length) {
		return new SwtEditorDateTime(cast(parent), SWT.TIME | length(length));
	}

	@Override
	public SwtEditorDateTime createTime(EditorComposite parent, TimeStyle style) {
		return new SwtEditorDateTime(cast(parent), SWT.TIME | extractSimpleScrollableStyle(style));
	}

	@Override
	public SwtEditorDateTime createCalendar(EditorComposite parent) {
		return new SwtEditorDateTime(cast(parent), SWT.CALENDAR);
	}

	@Override
	public SwtEditorDateTime createCalendar(EditorComposite parent, CalendarStyle style) {
		return new SwtEditorDateTime(cast(parent), SWT.CALENDAR | extractSimpleScrollableStyle(style));
	}

	@Override
	public SwtEditorSpinner createSpinner(EditorComposite parent) {
		return new SwtEditorSpinner(cast(parent), SWT.NONE);
	}

	@Override
	public SwtEditorSpinner createSpinner(EditorComposite parent, SpinnerStyle style) {
		return new SwtEditorSpinner(cast(parent), extractSpinnerStyle(style));
	}

	@Override
	public SwtEditorTabFolder createTabFolder(EditorComposite parent, boolean bottom) {
		return new SwtEditorTabFolder(cast(parent), bottom ? SWT.BOTTOM : SWT.TOP);
	}

	@Override
	public SwtEditorTabFolder createTabFolder(EditorComposite parent, TabFolderStyle style) {
		return new SwtEditorTabFolder(cast(parent), extractTabFolderStyle(style));
	}

	@Override
	public SwtEditorExpandBar createExpandBar(EditorComposite parent, boolean verticalScroll) {
		return new SwtEditorExpandBar(cast(parent), verticalScroll ? SWT.V_SCROLL : 0);
	}

	@Override
	public SwtEditorExpandBar createExpandBar(EditorComposite parent, ExpandBarStyle style) {
		return new SwtEditorExpandBar(cast(parent), extractSimpleScrollableStyle(style));
	}

	@Override
	public SwtEditorButton createCheckBox(EditorComposite parent) {
		return new SwtEditorButton(cast(parent), SWT.CHECK);
	}

	@Override
	public SwtEditorButton createCheckBox(EditorComposite parent, CheckBoxStyle style) {
		return new SwtEditorButton(cast(parent), SWT.CHECK | extractButtonStyle(style));
	}

	@Override
	public EditorButton createButton(EditorComposite parent) {
		return new SwtEditorButton(cast(parent), SWT.PUSH);
	}

	@Override
	public EditorButton createButton(EditorComposite parent, ButtonStyle style) {
		return new SwtEditorButton(cast(parent), SWT.PUSH | extractButtonStyle(style));
	}

	@Override
	public EditorButton createToggleButton(EditorComposite parent) {
		return new SwtEditorButton(cast(parent), SWT.TOGGLE);
	}

	@Override
	public EditorButton createToggleButton(EditorComposite parent, ToggleButtonStyle style) {
		return new SwtEditorButton(cast(parent), SWT.TOGGLE | extractButtonStyle(style));
	}

	@Override
	public EditorButton createRadioButton(EditorComposite parent) {
		return new SwtEditorButton(cast(parent), SWT.RADIO);
	}

	@Override
	public EditorButton createRadioButton(EditorComposite parent, RadioButtonStyle style) {
		return new SwtEditorButton(cast(parent), SWT.RADIO | extractButtonStyle(style));
	}

	@Override
	public EditorButton createArrowButton(EditorComposite parent, ArrowDirection arrowDirection) {
		return new SwtEditorButton(cast(parent), SWT.ARROW | arrowDirection(arrowDirection));
	}

	@Override
	public EditorButton createArrowButton(EditorComposite parent, ArrowButtonStyle style) {
		return new SwtEditorButton(cast(parent),
				SWT.ARROW | arrowDirection(style.direction) | extractButtonStyle(style));
	}

	@Override
	public SwtEditorMenu createMenu(EditorControl parent) {
		return new SwtEditorMenu((SwtEditorControl<?>) parent);
	}

	@Override
	public SwtEditorToolBar createToolBar(EditorComposite parent, boolean vertical) {
		return new SwtEditorToolBar(cast(parent), orientation(vertical));
	}

	@Override
	public SwtEditorToolBar createToolBar(EditorComposite parent, boolean vertical, ToolBarStyle style) {
		return new SwtEditorToolBar(cast(parent), orientation(vertical) | extractToolBarStyle(style));
	}

	@Override
	public <ELEMENT> SwtEditorTable<ELEMENT> createTable(EditorComposite parent) {
		return new SwtEditorTable<ELEMENT>(cast(parent), SWT.SINGLE | SWT.FULL_SELECTION);
	}

	@Override
	public <ELEMENT> SwtEditorTable<ELEMENT> createTable(EditorComposite parent, TableStyle style) {
		return new SwtEditorTable<ELEMENT>(cast(parent), extractTableStyle(style));
	}

	@Override
	public <ELEMENT> SwtEditorTree<ELEMENT> createTree(EditorComposite parent,
			TreeContentProvider<ELEMENT> contentProvider) {
		return createTree(cast(parent), contentProvider, false, SWT.SINGLE | SWT.FULL_SELECTION);
	}

	private static <ELEMENT> SwtEditorTree<ELEMENT> createTree(EditorComposite parent,
			TreeContentProvider<ELEMENT> contentProvider, boolean formBorder, int style) {
		SwtEditorTree<ELEMENT> tree = new SwtEditorTree<>(cast(parent), contentProvider, style);
		if (formBorder) {
			tree.widget.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			tree.getParent().paintBorders();
		}
		return tree;
	}

	@Override
	public <ELEMENT> SwtEditorTree<ELEMENT> createTree(EditorComposite parent, TreeStyle<ELEMENT> style) {
		return createTree(cast(parent), style.contentProvider, style.formBorder, extractTreeStyle(style));
	}

	@Override
	public EditorShell createShell(ShellStyle style) {
		return new SwtEditorShell(getShell(), extractShellStyle(style));
	}

	@Override
	public SwtEditorSashForm createSashForm(EditorComposite parent, boolean vertical, boolean smooth) {
		int swtStyle = vertical ? SWT.VERTICAL : 0;
		if (smooth) {
			swtStyle |= SWT.SMOOTH;
		}

		return new SwtEditorSashForm(cast(parent), swtStyle);
	}

	@Override
	public SwtEditorSashForm createSashForm(EditorComposite parent, SashFormStyle style) {
		int swtStyle = extractSimpleScrollableStyle(style);

		if (style.vertical) {
			swtStyle |= SWT.VERTICAL;
		}

		if (style.smooth) {
			swtStyle |= SWT.SMOOTH;
		}

		return new SwtEditorSashForm(cast(parent), swtStyle);
	}

	@Override
	public void showErrorDialog(String title, String message) {
		MessageDialog.openError(getShell(), title, message);
	}

	@Override
	public void showErrorDialog(String dialogTitle, String message, Throwable t) {
		ErrorDialog.openError(getShell(), dialogTitle, message, createErrorStatus(t, t.toString()));
	}

	@Override
	public void showWarningDialog(String title, String message) {
		MessageDialog.openWarning(getShell(), title, message);
	}

	@Override
	public void showInformationDialog(String title, String message) {
		MessageDialog.openInformation(getShell(), title, message);
	}

	@Override
	public boolean showQuestionDialog(String title, String message) {
		return MessageDialog.openQuestion(getShell(), title, message);
	}

	@Override
	public boolean showConfirmDialog(String title, String message) {
		return MessageDialog.openConfirm(getShell(), title, message);
	}

	@Override
	public String showInputDialog(String dialogTitle, String dialogMessage, String initialValue,
			EditorInputValidator validator) {
		IInputValidator swtValidator = validator == null ? null : new IInputValidator() {
			@Override
			public String isValid(String newText) {
				return validator.isValid(newText);
			}
		};
		InputDialog dialog = new InputDialog(getShell(), dialogTitle, dialogMessage, initialValue, swtValidator);
		return dialog.open() == Window.OK ? dialog.getValue() : null;
	}

	@Override
	public <T> T showDialog(EditorDialogProperties dialogProperties) {
		SwtEditorDialog dialog = new SwtEditorDialog(dialogProperties);
		dialog.create();
		dialog.open();
		return cast(dialog.returnValue);
	}

	@Override
	public <T> T showDialog(EditorTitleAteaDialogProperties dialogProperties) {
		SwtEditorTitleAreaDialog dialog = new SwtEditorTitleAreaDialog(dialogProperties);
		dialog.create();
		dialog.open();
		return cast(dialog.returnValue);
	}

	public static EditorLayoutData transformLayoutData(GridData data) {
		return new EditorLayoutData()
				.alignment(horizontalAlignment(data.horizontalAlignment), vericalAlignment(data.verticalAlignment))
				.hint(data.widthHint, data.heightHint).indent(data.horizontalIndent, data.verticalIndent)
				.span(data.horizontalSpan, data.verticalSpan)
				.grab(data.grabExcessHorizontalSpace, data.grabExcessVerticalSpace)
				.minSize(data.minimumWidth, data.minimumHeight).exclude(data.exclude);
	}

	public static int vericalAlignment(VerticalAlignment vAlign) {
		switch (vAlign) {
		case BOTTOM:
			return SWT.BOTTOM;
		case CENTER:
			return SWT.CENTER;
		case FILL:
			return SWT.FILL;
		case TOP:
			return SWT.TOP;
		default:
			return SWT.DEFAULT;
		}
	}

	public static int horizontalAlignment(HorizontalAlignment hAlign) {
		switch (hAlign) {
		case LEFT:
			return SWT.LEFT;
		case CENTER:
			return SWT.CENTER;
		case FILL:
			return SWT.FILL;
		case RIGHT:
			return SWT.RIGHT;
		default:
			return SWT.DEFAULT;
		}
	}

	public static VerticalAlignment vericalAlignment(int vAlign) {
		switch (vAlign) {
		case SWT.BOTTOM:
			return VerticalAlignment.BOTTOM;
		case SWT.CENTER:
			return VerticalAlignment.CENTER;
		case SWT.FILL:
			return VerticalAlignment.FILL;
		case SWT.TOP:
			return VerticalAlignment.TOP;
		default:
			return null;
		}
	}

	public static HorizontalAlignment horizontalAlignment(int hAlign) {
		switch (hAlign) {
		case SWT.LEFT:
			return HorizontalAlignment.LEFT;
		case SWT.CENTER:
			return HorizontalAlignment.CENTER;
		case SWT.FILL:
			return HorizontalAlignment.FILL;
		case SWT.RIGHT:
			return HorizontalAlignment.RIGHT;
		default:
			return null;
		}
	}

	public static GridData transformLayoutData(EditorLayoutData data) {
		return GridDataFactory.swtDefaults()
				.align(horizontalAlignment(data.horizontalAlignment), vericalAlignment(data.verticalAlignment))
				.hint(data.widthHint, data.heightHint).indent(data.horizontalIndent, data.verticalIndent)
				.span(data.horizontalSpan, data.verticalSpan)
				.grab(data.grabExcessHorizontalSpace, data.grabExcessVerticalSpace)
				.minSize(data.minimumWidth, data.minimumHeight).exclude(data.exclude).create();
	}

	public static EditorLayout transformLayout(GridLayout layout) {
		return new EditorLayout().numColumns(layout.numColumns)
				.margins(layout.marginLeft, layout.marginRight, layout.marginTop, layout.marginBottom)
				.spacing(layout.horizontalSpacing, layout.verticalSpacing)
				.columnsEqualWidth(layout.makeColumnsEqualWidth);
	}

	public static GridLayout transformLayout(EditorLayout data) {
		return GridLayoutFactory.swtDefaults().numColumns(data.numColumns)
				.extendedMargins(data.leftMargin, data.rightMargin, data.topMargin, data.bottomMargin)
				.spacing(data.horizontalSpacing, data.verticalSpacing).equalWidth(data.makeColumnsEqualWidth).create();
	}
}
