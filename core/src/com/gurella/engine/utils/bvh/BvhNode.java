package com.gurella.engine.utils.bvh;

import com.gurella.engine.utils.struct.Struct;
import com.gurella.engine.utils.struct.StructProperty.BoundingBoxStructProperty;
import com.gurella.engine.utils.struct.StructProperty.IntStructProperty;
import com.gurella.engine.utils.struct.StructProperty.ReferenceStructProperty;

public class BvhNode extends Struct {
	public static final IntStructProperty depth = new IntStructProperty();
	public static final BoundingBoxStructProperty box = new BoundingBoxStructProperty();

	public static final ReferenceStructProperty<BvhNode> parent = new ReferenceStructProperty<BvhNode>(BvhNode.class);
	public static final ReferenceStructProperty<BvhNode> left = new ReferenceStructProperty<BvhNode>(BvhNode.class);
	public static final ReferenceStructProperty<BvhNode> right = new ReferenceStructProperty<BvhNode>(BvhNode.class);
}
