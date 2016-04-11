package com.gurella.engine.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.scene.SceneNodeComponent2;

public class TestPropertyEditorsComponnent extends SceneNodeComponent2 {
	public Matrix3 matrix3 = new Matrix3();
	public Matrix4 matrix4 = new Matrix4();
	public Vector3 testVector;
	public Texture texture;
	
	public String[] testStringArray = new String[3];
	public int[] testIntArray = new int[3];
	public Integer[] testIntegerArray;
	public Vector3[] testVectorArray;
}
