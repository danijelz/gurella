package com.gurella.engine.editor.ui;

import java.io.InputStream;

import com.gurella.engine.editor.ui.EditorButton.ArrowButtonStyle;
import com.gurella.engine.editor.ui.EditorButton.ArrowDirection;
import com.gurella.engine.editor.ui.EditorButton.ButtonStyle;
import com.gurella.engine.editor.ui.EditorButton.CheckBoxStyle;
import com.gurella.engine.editor.ui.EditorButton.RadioButtonStyle;
import com.gurella.engine.editor.ui.EditorButton.ToggleButtonStyle;
import com.gurella.engine.editor.ui.EditorCombo.ComboStyle;
import com.gurella.engine.editor.ui.EditorComposite.CompositeStyle;
import com.gurella.engine.editor.ui.EditorDateTime.CalendarStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateStyle;
import com.gurella.engine.editor.ui.EditorDateTime.DateTimeLength;
import com.gurella.engine.editor.ui.EditorDateTime.DropDownDateStyle;
import com.gurella.engine.editor.ui.EditorDateTime.TimeStyle;
import com.gurella.engine.editor.ui.EditorExpandBar.ExpandBarStyle;
import com.gurella.engine.editor.ui.EditorExpandableComposite.ExpandableCompositeStyle;
import com.gurella.engine.editor.ui.EditorGroup.GroupStyle;
import com.gurella.engine.editor.ui.EditorLabel.LabelStyle;
import com.gurella.engine.editor.ui.EditorLabel.SeparatorStyle;
import com.gurella.engine.editor.ui.EditorLink.LinkStyle;
import com.gurella.engine.editor.ui.EditorList.ListStyle;
import com.gurella.engine.editor.ui.EditorProgressBar.ProgressBarStyle;
import com.gurella.engine.editor.ui.EditorSash.SashStyle;
import com.gurella.engine.editor.ui.EditorSashForm.SashFormStyle;
import com.gurella.engine.editor.ui.EditorScale.ScaleStyle;
import com.gurella.engine.editor.ui.EditorScrolledComposite.ScrolledCompositeStyle;
import com.gurella.engine.editor.ui.EditorSection.SectionStyle;
import com.gurella.engine.editor.ui.EditorShell.ShellStyle;
import com.gurella.engine.editor.ui.EditorSlider.SliderStyle;
import com.gurella.engine.editor.ui.EditorSpinner.SpinnerStyle;
import com.gurella.engine.editor.ui.EditorTabFolder.TabFolderStyle;
import com.gurella.engine.editor.ui.EditorTable.TableStyle;
import com.gurella.engine.editor.ui.EditorText.TextStyle;
import com.gurella.engine.editor.ui.EditorToolBar.ToolBarStyle;
import com.gurella.engine.editor.ui.EditorTree.TreeContentProvider;
import com.gurella.engine.editor.ui.EditorTree.TreeStyle;
import com.gurella.engine.editor.ui.dialog.EditorDialog.EditorDialogProperties;
import com.gurella.engine.editor.ui.dialog.EditorTitleAreaDialog.EditorTitleAteaDialogProperties;

public interface EditorUi {
	void log(EditorLogLevel level, String message);

	void logError(Throwable t, String message);

	EditorImage createImage(InputStream imageStream);

	EditorFont createFont(String name, int height, boolean bold, boolean italic);

	EditorFont createFont(EditorFont initial, int height, boolean bold, boolean italic);

	EditorFont createFont(EditorControl control, int height, boolean bold, boolean italic);

	EditorComposite createComposite(EditorComposite parent);

	EditorComposite createComposite(EditorComposite parent, CompositeStyle style);

	EditorGroup createGroup(EditorComposite parent);

	EditorGroup createGroup(EditorComposite parent, GroupStyle style);

	EditorLabel createLabel(EditorComposite parent);

	EditorLabel createLabel(EditorComposite parent, LabelStyle style);

	EditorLabel createLabel(EditorComposite parent, String text);

	EditorLabel createLabel(EditorComposite parent, String text, LabelStyle style);

	EditorLabel createSeparator(EditorComposite parent, boolean vertical);

	EditorLabel createSeparator(EditorComposite parent, SeparatorStyle style);

	EditorLink createLink(EditorComposite parent);

	EditorLink createLink(EditorComposite parent, LinkStyle style);

	EditorLink createLink(EditorComposite parent, String text);

	EditorLink createLink(EditorComposite parent, String text, LinkStyle style);

	EditorProgressBar createProgressBar(EditorComposite parent, boolean vertical, boolean smooth,
			boolean indeterminate);

	EditorProgressBar createProgressBar(EditorComposite parent, ProgressBarStyle style);

	EditorSash createSash(EditorComposite parent, boolean vertical, boolean smooth);

	EditorSash createSash(EditorComposite parent, SashStyle style);

	EditorScale createScale(EditorComposite parent, boolean vertical);

	EditorScale createScale(EditorComposite parent, ScaleStyle style);

	EditorSlider createSlider(EditorComposite parent, boolean vertical);

	EditorSlider createSlider(EditorComposite parent, SliderStyle style);

	<ELEMENT> EditorCombo<ELEMENT> createCombo(EditorComposite parent);

	<ELEMENT> EditorCombo<ELEMENT> createCombo(EditorComposite parent, ComboStyle style);

	EditorDateTime createDate(EditorComposite parent, DateTimeLength length);

	EditorDateTime createDate(EditorComposite parent, DateStyle styles);

	EditorDateTime createDropDownDate(EditorComposite parent);

	EditorDateTime createDropDownDate(EditorComposite parent, DropDownDateStyle style);

	EditorDateTime createTime(EditorComposite parent, DateTimeLength length);

	EditorDateTime createTime(EditorComposite parent, TimeStyle style);

	EditorDateTime createCalendar(EditorComposite parent);

	EditorDateTime createCalendar(EditorComposite parent, CalendarStyle style);

	EditorSpinner createSpinner(EditorComposite parent);

	EditorSpinner createSpinner(EditorComposite parent, SpinnerStyle style);

	EditorTabFolder createTabFolder(EditorComposite parent, boolean bottom);

	EditorTabFolder createTabFolder(EditorComposite parent, TabFolderStyle style);

	EditorExpandBar createExpandBar(EditorComposite parent, boolean verticalScroll);

	EditorExpandBar createExpandBar(EditorComposite parent, ExpandBarStyle style);

	<ELEMENT> EditorList<ELEMENT> createList(EditorComposite parent, boolean multi);

	<ELEMENT> EditorList<ELEMENT> createList(EditorComposite parent, ListStyle style);

	EditorText createText(EditorComposite parent, String text);

	EditorText createText(EditorComposite parent, String text, TextStyle style);

	EditorText createTextArea(EditorComposite parent, String text);

	EditorText createTextArea(EditorComposite parent, String text, TextStyle style);

	EditorButton createCheckBox(EditorComposite parent);

	EditorButton createCheckBox(EditorComposite parent, CheckBoxStyle style);

	EditorButton createButton(EditorComposite parent);

	EditorButton createButton(EditorComposite parent, ButtonStyle styles);

	EditorButton createToggleButton(EditorComposite parent);

	EditorButton createToggleButton(EditorComposite parent, ToggleButtonStyle style);

	EditorButton createRadioButton(EditorComposite parent);

	EditorButton createRadioButton(EditorComposite parent, RadioButtonStyle style);

	EditorButton createArrowButton(EditorComposite parent, ArrowDirection arrowDirection);

	EditorButton createArrowButton(EditorComposite parent, ArrowButtonStyle arrowDirection);

	EditorMenu createMenu(EditorControl parent);

	EditorToolBar createToolBar(EditorComposite parent, boolean vertical);

	EditorToolBar createToolBar(EditorComposite parent, boolean vertical, ToolBarStyle style);

	<ELEMENT> EditorTable<ELEMENT> createTable(EditorComposite parent);

	<ELEMENT> EditorTable<ELEMENT> createTable(EditorComposite parent, TableStyle style);

	<ELEMENT> EditorTree<ELEMENT> createTree(EditorComposite parent, TreeContentProvider<ELEMENT> contentProvider);

	<ELEMENT> EditorTree<ELEMENT> createTree(EditorComposite parent, TreeStyle<ELEMENT> style);

	EditorShell createShell(ShellStyle style);

	EditorSashForm createSashForm(EditorComposite parent, boolean vertical, boolean smooth);

	EditorSashForm createSashForm(EditorComposite parent, SashFormStyle style);

	void showErrorDialog(String dialogTitle, String message, Throwable throwable);

	void showErrorDialog(String title, String message);

	void showWarningDialog(String title, String message);

	void showInformationDialog(String title, String message);

	boolean showQuestionDialog(String title, String message);

	boolean showConfirmDialog(String title, String message);

	String showInputDialog(String dialogTitle, String dialogMessage, String initialValue,
			EditorInputValidator validator);

	<T> T showDialog(EditorDialogProperties dialogProperties);

	<T> T showDialog(EditorTitleAteaDialogProperties dialogProperties);

	EditorExpandableComposite createExpandableComposite(EditorComposite parent);

	EditorExpandableComposite createExpandableComposite(EditorComposite parent, ExpandableCompositeStyle style);

	EditorSection createSection(EditorComposite parent);

	EditorSection createSection(EditorComposite parent, SectionStyle style);

	EditorScrolledComposite createScrolledComposite(EditorComposite parent);

	EditorScrolledComposite createScrolledComposite(EditorComposite parent, ScrolledCompositeStyle style);

	EditorForm createForm(EditorComposite parent);

	EditorScrolledForm createScrolledForm(EditorComposite parent);
}
