package com.gurella.studio.editor.engine.ui.event;

import static com.gurella.studio.editor.engine.ui.SwtEditorWidget.getEditorWidget;

import org.eclipse.swt.widgets.Event;

import com.gurella.engine.editor.ui.EditorWidget;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.engine.ui.SwtEditorWidget;

public class SwtEditorEvent implements EditorEvent {
	private Event event;

	public SwtEditorEvent(Event event) {
		this.event = event;
	}

	@Override
	public int getButton() {
		return event.button;
	}

	@Override
	public void setButton(int button) {
		event.button = button;
	}

	@Override
	public char getCharacter() {
		return event.character;
	}

	@Override
	public void setCharacter(char character) {
		event.character = character;
	}

	@Override
	public int getCount() {
		return event.count;
	}

	@Override
	public void setCount(int count) {
		event.count = count;
	}

	@Override
	public Object getData() {
		return event.data;
	}

	@Override
	public void setData(Object data) {
		event.data = data;
	}

	@Override
	public int getDetail() {
		return event.detail;
	}

	@Override
	public void setDetail(int detail) {
		event.detail = detail;
	}

	@Override
	public boolean isDoit() {
		return event.doit;
	}

	@Override
	public void setDoit(boolean doit) {
		event.doit = doit;
	}

	@Override
	public int getEnd() {
		return event.end;
	}

	@Override
	public void setEnd(int end) {
		event.end = end;
	}

	@Override
	public int getHeight() {
		return event.height;
	}

	@Override
	public void setHeight(int height) {
		event.height = height;
	}

	@Override
	public int getIndex() {
		return event.index;
	}

	@Override
	public void setIndex(int index) {
		event.index = index;
	}

	@Override
	public EditorWidget getItem() {
		return getEditorWidget(event.item);
	}

	@Override
	public void setItem(EditorWidget item) {
		event.item = item == null ? null : ((SwtEditorWidget<?>) item).getWidget();
	}

	@Override
	public int getKeyCode() {
		return event.keyCode;
	}

	@Override
	public void setKeyCode(int keyCode) {
		event.keyCode = keyCode;
	}

	@Override
	public int getKeyLocation() {
		return event.keyLocation;
	}

	@Override
	public void setKeyLocation(int keyLocation) {
		event.keyLocation = keyLocation;
	}

	@Override
	public double getMagnification() {
		return event.magnification;
	}

	@Override
	public void setMagnification(double magnification) {
		event.magnification = magnification;
	}

	@Override
	public double getRotation() {
		return event.rotation;
	}

	@Override
	public void setRotation(double rotation) {
		event.rotation = rotation;
	}

	@Override
	public int[] getSegments() {
		return event.segments;
	}

	@Override
	public void setSegments(int[] segments) {
		event.segments = segments;
	}

	@Override
	public char[] getSegmentsChars() {
		return event.segmentsChars;
	}

	@Override
	public void setSegmentsChars(char[] segmentsChars) {
		event.segmentsChars = segmentsChars;
	}

	@Override
	public int getStart() {
		return event.start;
	}

	@Override
	public void setStart(int start) {
		event.start = start;
	}

	@Override
	public int getStateMask() {
		return event.stateMask;
	}

	@Override
	public void setStateMask(int stateMask) {
		event.stateMask = stateMask;
	}

	@Override
	public String getText() {
		return event.text;
	}

	@Override
	public void setText(String text) {
		event.text = text;
	}

	@Override
	public int getTime() {
		return event.time;
	}

	@Override
	public void setTime(int time) {
		event.time = time;
	}

	@Override
	public EditorEventType getType() {
		return SwtEditorEventType.fromSwtConstant(event.type);
	}

	@Override
	public void setType(EditorEventType type) {
		event.type = SwtEditorEventType.toSwtConstant(type);
	}

	@Override
	public EditorWidget getWidget() {
		return getEditorWidget(event.widget);
	}

	@Override
	public void setWidget(EditorWidget widget) {
		event.widget = widget == null ? null : ((SwtEditorWidget<?>) widget).getWidget();
	}

	@Override
	public int getWidth() {
		return event.width;
	}

	@Override
	public void setWidth(int width) {
		event.width = width;
	}

	@Override
	public int getX() {
		return event.x;
	}

	@Override
	public void setX(int x) {
		event.x = x;
	}

	@Override
	public int getxDirection() {
		return event.xDirection;
	}

	@Override
	public void setxDirection(int xDirection) {
		event.xDirection = xDirection;
	}

	@Override
	public int getY() {
		return event.y;
	}

	@Override
	public void setY(int y) {
		event.y = y;
	}

	@Override
	public int getyDirection() {
		return event.yDirection;
	}

	@Override
	public void setyDirection(int yDirection) {
		event.yDirection = yDirection;
	}

	@Override
	public SwtEditorUi getEditorUi() {
		return SwtEditorUi.instance;
	}
}
