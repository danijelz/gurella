package com.gurella.studio.editor.model.extension.event;

import org.eclipse.swt.SWT;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.editor.event.EditorEventType;

public enum SwtEditorEventType {
	Activate(SWT.Activate),
	Arm(SWT.Arm),
	Close(SWT.Close),
	Collapse(SWT.Collapse),
	Deactivate(SWT.Deactivate),
	DefaultSelection(SWT.DefaultSelection),
	Deiconify(SWT.Deiconify),
	Dispose(SWT.Dispose),
	DragDetect(SWT.DragDetect),
	EraseItem(SWT.EraseItem),
	Expand(SWT.Expand),
	FocusIn(SWT.FocusIn),
	FocusOut(SWT.FocusOut),
	Gesture(SWT.Gesture),
	HardKeyDown(SWT.HardKeyDown),
	HardKeyUp(SWT.HardKeyUp),
	Help(SWT.Help),
	Hide(SWT.Hide),
	Iconify(SWT.Iconify),
	ImeComposition(SWT.ImeComposition),
	KeyDown(SWT.KeyDown),
	KeyUp(SWT.KeyUp),
	MeasureItem(SWT.MeasureItem),
	MenuDetect(SWT.MenuDetect),
	Modify(SWT.Modify),
	MouseDoubleClick(SWT.MouseDoubleClick),
	MouseDown(SWT.MouseDown),
	MouseEnter(SWT.MouseEnter),
	MouseExit(SWT.MouseExit),
	MouseHorizontalWheel(SWT.MouseHorizontalWheel),
	MouseHover(SWT.MouseHover),
	MouseMove(SWT.MouseMove),
	MouseUp(SWT.MouseUp),
	MouseVerticalWheel(SWT.MouseVerticalWheel),
	MouseWheel(SWT.MouseWheel),
	Move(SWT.Move),
	None(SWT.None),
	OpenDocument(SWT.OpenDocument),
	OrientationChange(SWT.OrientationChange),
	Paint(SWT.Paint),
	PaintItem(SWT.PaintItem),
	Resize(SWT.Resize),
	Segments(SWT.Segments),
	Selection(SWT.Selection),
	SetData(SWT.SetData),
	Settings(SWT.Settings),
	Show(SWT.Show),
	Skin(SWT.Skin),
	Touch(SWT.Touch),
	Traverse(SWT.Traverse),
	Verify(SWT.Verify);

	private static IntMap<SwtEditorEventType> swtValues;

	public final int swtValue;

	private SwtEditorEventType(int swtValue) {
		this.swtValue = swtValue;
		getSwtValues().put(swtValue, this);
	}

	private static IntMap<SwtEditorEventType> getSwtValues() {
		if (swtValues == null) {
			swtValues = new IntMap<>();
		}
		return swtValues;
	}

	public static int toSwtConstant(EditorEventType eventType) {
		return valueOf(eventType.name()).swtValue;
	}

	public static EditorEventType fromSwtConstant(int type) {
		SwtEditorEventType swtEditorEventType = getSwtValues().get(type);
		return swtEditorEventType == null ? null : EditorEventType.valueOf(swtEditorEventType.name());
	}
}
