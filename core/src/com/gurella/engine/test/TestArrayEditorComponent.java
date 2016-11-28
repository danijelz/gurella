package com.gurella.engine.test;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.scene.SceneNodeComponent;

public class TestArrayEditorComponent extends SceneNodeComponent {
	@PropertyEditorDescriptor(genericTypes = { String.class })
	public Array<String> strings = new Array<String>();

	@PropertyEditorDescriptor(genericTypes = { Vector.class })
	public Array<Vector<?>> vectors = new Array<Vector<?>>();

	@PropertyEditorDescriptor(genericTypes = { Vector.class })
	public List<Vector<?>> list = new ArrayList<Vector<?>>();
}
