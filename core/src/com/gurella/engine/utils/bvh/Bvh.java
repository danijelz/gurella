package com.gurella.engine.utils.bvh;

import com.gurella.engine.utils.struct.StructArray;

public class Bvh {
	private StructArray<BvhNode> nodes = new StructArray<BvhNode>(BvhNode.class, 1024);
}
