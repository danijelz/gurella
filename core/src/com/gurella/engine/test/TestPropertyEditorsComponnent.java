package com.gurella.engine.test;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.SceneNodeComponent2;

public class TestPropertyEditorsComponnent extends SceneNodeComponent2 {
	public List<String> list = new ArrayList<String>();
	public Array<Integer> arr = new Array<Integer>();

	public Matrix3 matrix3 = new Matrix3();
	public Matrix4 matrix4 = new Matrix4();
	public Vector3 testVector;
	public Texture texture;

	public String[] testStringArray = new String[3];
	public int[] testIntArray = new int[3];
	public Integer[] testIntegerArray = new Integer[3];
	public Vector3[] testVectorArray = new Vector3[3];
}
