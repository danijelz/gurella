package com.gurella.engine.test;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.editor.property.PropertyEditorDescriptor;
import com.gurella.engine.scene.SceneNodeComponent2;

public class TestArrayEditorComponent extends SceneNodeComponent2 {
	@PropertyEditorDescriptor(genericTypes = { String.class })
	public final Array<String> strings = new Array<String>();

	@PropertyEditorDescriptor(genericTypes = { Vector.class })
	public final Array<Vector> vectors = new Array<Vector>();
	
	@PropertyEditorDescriptor(genericTypes = { Vector.class })
	public final List<Vector> list = new ArrayList<Vector>();
}
