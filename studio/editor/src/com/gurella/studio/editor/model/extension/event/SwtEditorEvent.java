package com.gurella.studio.editor.model.extension.event;

import org.eclipse.swt.widgets.Event;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.event.EditorEvent;
import com.gurella.engine.editor.ui.event.EditorEventType;
import com.gurella.studio.editor.model.extension.SwtEditorControl;

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
	public EditorControl getItem() {
		return SwtEditorControl.getEditorWidget(event.item);
	}

	@Override
	public void setItem(EditorControl item) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setKeyLocation(int keyLocation) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getMagnification() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMagnification(double magnification) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRotation(double rotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getSegments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSegments(int[] segments) {
		// TODO Auto-generated method stub

	}

	@Override
	public char[] getSegmentsChars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSegmentsChars(char[] segmentsChars) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStart(int start) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStateMask() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStateMask(int stateMask) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

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
	public EditorControl getWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWidget(EditorControl widget) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWidth(int width) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setX(int x) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getxDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setxDirection(int xDirection) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setY(int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getyDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setyDirection(int yDirection) {
		// TODO Auto-generated method stub

	}
}
