package com.gurella.engine.utils.bvh;

import com.badlogic.gdx.math.collision.BoundingBox;
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

	public int getDepth() {
		return depth.get(this);
	}

	public BoundingBox getBox() {
		return box.get(this);
	}

	public BoundingBox getBox(BoundingBox out) {
		return box.get(this, out);
	}

	public BvhNode getParent() {
		return parent.get(this);
	}

	public BvhNode getLeft() {
		return left.get(this);
	}

	public BvhNode getRight() {
		return right.get(this);
	}

	public boolean isLeaf() {
		return !left.isSet(this) && !right.isSet(this);
	}
}
