package com.gurella.studio.propertyeditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public interface ResourcePropertyEditor<T> {
	Array<Actor> getUiComponents();
	
	public int getCellspan(int componentIndex);//TODO move to composite editor
	
	public int getRowspan(int componentIndex);
	
	public void present(T value);

	void save();

	Object getValue();
}
