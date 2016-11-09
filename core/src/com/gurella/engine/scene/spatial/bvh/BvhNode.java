package com.gurella.engine.scene.spatial.bvh;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.Values;

//TODO https://github.com/jeske/SimpleScene/tree/master/SimpleScene/Util/ssBVH
public class BvhNode implements Poolable {
	private static final float MERGE_DISCOUNT = 0.3f;

	Bvh bvh;
	BvhNode parent;
	BvhNode left;
	BvhNode right;

	int depth;
	int nodeNumber; // for debugging
	final BoundingBox box = new BoundingBox().inf();

	// only populated in leaf nodes
	Array<BvhSpatial> spatials;

	public BvhNode(Bvh bvh) {
		this.bvh = bvh;
		this.nodeNumber = bvh.nodeCount++;
	}

	public BvhNode(Bvh bvh, Array<BvhSpatial> spatials) {
		this(bvh, null, spatials, 0);
	}

	private BvhNode(Bvh bvh, BvhNode parent, Array<BvhSpatial> spatials, int depth) {
		this.bvh = bvh;
		nodeNumber = bvh.nodeCount++;
		this.parent = parent;
		this.depth = depth;

		// Early out check due to bad data
		// If the list is empty then we have no BVHGObj, or invalid parameters
		// are passed in
		if (spatials == null || spatials.size < 1) {
			throw new IllegalArgumentException("spatials constructed with invalid paramaters");
		}

		// Check if we're at our LEAF node, and if so, save the objects and stop
		// recursing. Also store the min/max for the leaf node and update the
		// parent appropriately
		if (spatials.size <= bvh.maxLeafSpatials) {
			// once we reach the leaf node, we must set prev/next to null to
			// signify the end
			left = null;// TODO add to pool
			right = null;
			// at the leaf node we store the remaining objects, so initialise a
			// list
			this.spatials = spatials;
			for (int i = 0; i < spatials.size; i++) {
				spatials.get(i).node = this;
			}
			computeVolume();
			splitIfNecessary();
		} else {
			// if we have more than (bvh.LEAF_OBJECT_COUNT) objects, then
			// compute the volume and split
			this.spatials = spatials;
			computeVolume();
			splitNode();
			childRefit(false);
		}
	}

	public boolean isLeaf() {
		boolean isLeaf = (this.spatials.size > 0);
		// if we're a leaf, then both left and right should be null..
		if (isLeaf && ((right != null) || (left != null))) {
			throw new IllegalStateException("ssBVH Leaf has objects and left/right pointers!");
		}

		return isLeaf;
	}

	public void refitObjectChanged() {
		if (spatials == null) {
			throw new IllegalStateException("dangling leaf!");
		}

		if (refitVolume() && parent != null) {
			bvh.refitNodes.add(parent);
		}
	}

	private boolean refitVolume() {
		if (spatials.size == 0) {
			throw new UnsupportedOperationException();
		} // TODO: fix this... we should never get called in this case...

		BoundingBox oldbox = isValid(box) ? new BoundingBox() : new BoundingBox(box);// TODO garbage
		computeVolume();

		if (!box.max.equals(oldbox.max) || !box.min.equals(oldbox.min)) {
			if (parent != null) {
				parent.childRefit(true);
			}
			return true;
		} else {
			return false;
		}
	}

	private static boolean isValid(BoundingBox bBox) {
		return bBox.min.x <= bBox.max.x && bBox.min.y <= bBox.max.y && bBox.min.z <= bBox.max.z;
	}

	private void computeVolume() {
		BoundingBox firstBounds = spatials.get(0).getBounds();
		if (isValid(firstBounds)) {
			box.set(firstBounds);
		}

		for (int i = 1; i < spatials.size; i++) {
			BvhSpatial spatial = spatials.get(i);
			expandVolume(spatial.getBounds());
		}
	}

	private void expandVolume(BoundingBox volume) {
		if (!isValid(volume)) {
			return;
		}
		if (!isValid(box) || !box.contains(volume)) {
			box.ext(volume);
			if (parent != null) {
				parent.childExpanded(this);
			}
		}
	}

	private static float SAH(BvhNode node) {
		return SAH(node.box);
	}

	private static float SAH(BoundingBox bounds) {
		float x_size = bounds.max.x - bounds.min.x;
		float y_size = bounds.max.y - bounds.min.y;
		float z_size = bounds.max.z - bounds.min.z;
		return 2.0f * ((x_size * y_size) + (x_size * z_size) + (y_size * z_size));
	}

	private static BoundingBox boundsOfPair(BvhNode first, BvhNode second) {
		BoundingBox pairbox = new BoundingBox(first.box); // TODO garbage
		pairbox.ext(second.box);
		return pairbox;
	}

	void optimize() {
		// if we are not a grandparent, then we can't rotate, so queue our
		// parent and bail out
		if (left.isLeaf() && right.isLeaf() && parent != null) {
			bvh.refitNodes.add(parent);
			return;
		}

		// for each rotation, check that there are grandchildren as necessary
		// (aka not a leaf)
		// then compute total SAH cost of our branches after the rotation.
		float branchesSAH = SAH(left) + SAH(right);
		RotationOption bestRot = bestRotation(branchesSAH);

		// perform the best rotation...
		if (bestRot.rot != Rotation.NONE) {
			// if the best rotation is no-rotation... we check our parents
			// anyhow..
			// but only do it some random percentage of the time.
			if (parent != null && (System.nanoTime() % 100) < 2) {
				bvh.refitNodes.add(parent);
			}
		} else {
			if (parent != null) {
				bvh.refitNodes.add(parent);
			}

			if (((branchesSAH - bestRot.SAH) / branchesSAH) < 0.3f) {
				return; // the benefit is not worth the cost
			}

			Gdx.app.debug("BvhNode", Values.format("BVH swap %s from %s to %s", bestRot.rot.toString(),
					Float.toString(branchesSAH), Float.toString(bestRot.SAH)));

			// in order to swap we need to:
			// 1. swap the node locations
			// 2. update the depth (if child-to-grandchild)
			// 3. update the parent pointers
			// 4. refit the boundary box
			BvhNode swap = null;
			switch (bestRot.rot) {
			case NONE:
				break;
			// child to grandchild rotations
			case L_RL:
				swap = left;
				swap.depth++;
				left = right.left;
				left.parent = this;
				left.depth--;
				right.left = swap;
				swap.parent = right;
				right.childRefit(true);
				break;
			case L_RR:
				swap = left;
				swap.depth++;
				left = right.right;
				left.parent = this;
				left.depth--;
				right.right = swap;
				swap.parent = right;
				right.childRefit(true);
				break;
			case R_LL:
				swap = right;
				swap.depth++;
				right = left.left;
				right.parent = this;
				right.depth--;
				left.left = swap;
				swap.parent = left;
				left.childRefit(true);
				break;
			case R_LR:
				swap = right;
				swap.depth++;
				right = left.right;
				right.parent = this;
				right.depth--;
				left.right = swap;
				swap.parent = left;
				left.childRefit(true);
				break;
			// grandchild to grandchild rotations
			case LL_RR:
				swap = left.left;
				left.left = right.right;
				right.right = swap;
				left.left.parent = left;
				swap.parent = right;
				left.childRefit(false);
				right.childRefit(true);
				break;
			case LL_RL:
				swap = left.left;
				left.left = right.left;
				right.left = swap;
				left.left.parent = left;
				swap.parent = right;
				left.childRefit(false);
				right.childRefit(true);
				break;
			default:
				// unknown...
				throw new IllegalArgumentException(
						"missing implementation for BVH Rotation .. " + bestRot.rot.toString());
			}
		}
	}

	private RotationOption bestRotation(float SAH) {
		RotationOption bestRotationOption = null;
		Rotation[] values = Rotation.values();
		for (int i = 0; i < values.length; i++) {
			Rotation rot = values[i];
			RotationOption opt = switchRotation(rot, SAH);
			if (bestRotationOption == null || bestRotationOption.compareTo(opt) > 0) {
				bestRotationOption = opt;
			}
		}

		return bestRotationOption;
	}

	// TODO garbage
	private RotationOption switchRotation(Rotation rot, float SAH) {
		switch (rot) {
		case NONE:
			return new RotationOption(SAH, Rotation.NONE);
		// child to grandchild rotations
		case L_RL:
			if (right.isLeaf()) {
				return new RotationOption(Float.MAX_VALUE, Rotation.NONE);
			} else {
				return new RotationOption(SAH(right.left) + SAH(boundsOfPair(left, right.right)), rot);
			}
		case L_RR:
			if (right.isLeaf()) {
				return new RotationOption(Float.MAX_VALUE, Rotation.NONE);
			} else {
				return new RotationOption(SAH(right.right) + SAH(boundsOfPair(left, right.left)), rot);
			}
		case R_LL:
			if (left.isLeaf()) {
				return new RotationOption(Float.MAX_VALUE, Rotation.NONE);
			} else {
				return new RotationOption(SAH(boundsOfPair(right, left.right)) + SAH(left.left), rot);
			}
		case R_LR:
			if (left.isLeaf()) {
				return new RotationOption(Float.MAX_VALUE, Rotation.NONE);
			} else {
				return new RotationOption(SAH(boundsOfPair(right, left.left)) + SAH(left.right), rot);
			}
			// grandchild to grandchild rotations
		case LL_RR:
			if (left.isLeaf() || right.isLeaf()) {
				return new RotationOption(Float.MAX_VALUE, Rotation.NONE);
			} else {
				return new RotationOption(
						SAH(boundsOfPair(right.right, left.right)) + SAH(boundsOfPair(right.left, left.left)), rot);
			}
		case LL_RL:
			if (left.isLeaf() || right.isLeaf()) {
				return new RotationOption(Float.MAX_VALUE, Rotation.NONE);
			} else {
				return new RotationOption(
						SAH(boundsOfPair(right.left, left.right)) + SAH(boundsOfPair(left.left, right.right)), rot);
			}
			// unknown...
		default:
			throw new IllegalArgumentException(
					"missing implementation for BVH Rotation SAH Computation .. " + rot.toString());
		}
	}

	private void splitNode() {
		// second, decide which axis to split on, and sort..
		Array<BvhSpatial> splitlist = new Array<BvhSpatial>(BvhSpatial.class);
		splitlist.addAll(spatials);
		for (int i = 0; i < splitlist.size; i++) {
			splitlist.get(i).node = null;
		}

		// sort along the appropriate axis
		final BvhAxis splitAxis = pickSplitAxis();
		splitlist.sort(splitAxis.comparator);

		// Find the center object in our current sub-list
		int center = splitlist.size / 2;
		spatials = null;

		// create the new left and right nodes...

		// Split the Hierarchy to the left
		left = new BvhNode(bvh, this,
				new Array<BvhSpatial>(Arrays.<BvhSpatial> copyOfRange(splitlist.items, 0, center)), this.depth + 1);
		// Split the Hierarchy to the right
		right = new BvhNode(bvh, this,
				new Array<BvhSpatial>(Arrays.<BvhSpatial> copyOfRange(splitlist.items, center, splitlist.size)),
				this.depth + 1);
	}

	private BvhAxis pickSplitAxis() {
		float axis_x = box.max.x - box.min.x;
		float axis_y = box.max.y - box.min.y;
		float axis_z = box.max.z - box.min.z;

		// return the biggest axis
		if (axis_x > axis_y) {
			if (axis_x > axis_z) {
				return BvhAxis.X;
			} else {
				return BvhAxis.Z;
			}
		} else {
			if (axis_y > axis_z) {
				return BvhAxis.Y;
			} else {
				return BvhAxis.Z;
			}
		}
	}

	private void splitIfNecessary() {
		if (spatials.size > bvh.maxLeafSpatials) {
			splitNode();
		}
	}

	void addSpatial(BvhSpatial newSpatial) {
		BoundingBox newSpatialBox = newSpatial.getBounds();
		float newSpatialSAH = SAH(newSpatialBox);
		addObject(newSpatial, newSpatialBox, newSpatialSAH);
	}

	private void addObject(BvhSpatial newSpatial, BoundingBox newSpatialBox, float newSpatialSAH) {
		if (spatials != null) {
			// add the object and map it to our leaf
			spatials.add(newSpatial);
			newSpatial.node = this;
			refitVolume();
			// split if necessary...
			splitIfNecessary();
		} else {
			// find the best way to add this object.. 3 options..
			// 1. send to left node (L+N,R)
			// 2. send to right node (L,R+N)
			// 3. merge and pushdown left-and-right node (L+R,N)
			float leftSAH = SAH(left);
			float rightSAH = SAH(right);
			BoundingBox temp = new BoundingBox(); // TODO garbage
			float sendLeftSAH = rightSAH + SAH(temp.set(left.box).ext(newSpatialBox)); // (L+N,R)
			float sendRightSAH = leftSAH + SAH(temp.set(right.box).ext(newSpatialBox)); // (L,R+N)
			float mergedLeftAndRightSAH = SAH(boundsOfPair(left, right)) + newSpatialSAH; // (L+R,N)

			if (mergedLeftAndRightSAH < (Math.min(sendLeftSAH, sendRightSAH)) * MERGE_DISCOUNT) {
				// merge and pushdown left and right as a new node..
				BvhNode leftSubnode = new BvhNode(bvh);
				leftSubnode.left = left;
				leftSubnode.right = right;
				leftSubnode.parent = this;
				// we need to be an interior node... so null out our object
				// list..
				leftSubnode.spatials = null;
				left.parent = leftSubnode;
				right.parent = leftSubnode;
				leftSubnode.childRefit(false);

				// make new subnode for obj
				BvhNode rightSubnode = new BvhNode(bvh);
				rightSubnode.parent = this;
				rightSubnode.spatials = new Array<BvhSpatial>(BvhSpatial.class);
				rightSubnode.spatials.add(newSpatial);
				newSpatial.node = rightSubnode;
				rightSubnode.computeVolume();

				// make assignments..
				left = leftSubnode;
				right = rightSubnode;
				// propagate new depths to our children
				setDepth(this.depth);
				childRefit(true);
			} else if (sendLeftSAH < sendRightSAH) {
				// send left
				left.addObject(newSpatial, newSpatialBox, newSpatialSAH);
			} else {
				// send right
				right.addObject(newSpatial, newSpatialBox, newSpatialSAH);
			}
		}

	}

	void removeObject(BvhSpatial newOb) {
		if (spatials == null) {
			throw new IllegalStateException("removeObject() called on nonLeaf!");
		}

		newOb.node = null;
		spatials.removeValue(newOb, true);
		if (spatials.size > 0) {
			refitVolume();
		} else if (parent != null) {
			spatials = null;
			parent.removeLeaf(this);
			parent = null;
		}
	}

	void setDepth(int newdepth) {
		this.depth = newdepth;
		if (spatials == null) {
			left.setDepth(newdepth + 1);
			right.setDepth(newdepth + 1);
		}
	}

	void removeLeaf(BvhNode removeLeaf) {
		if (left == null || right == null) {
			throw new IllegalStateException("bad intermediate node");
		}

		BvhNode keepLeaf;
		if (removeLeaf == left) {
			keepLeaf = right;
		} else if (removeLeaf == right) {
			keepLeaf = left;
		} else {
			throw new IllegalStateException("removeLeaf doesn't match any leaf!");
		}

		if (isValid(keepLeaf.box)) {
			// "become" the leaf we are keeping.
			box.set(keepLeaf.box);
		} else {
			box.inf();
		}

		left = keepLeaf.left;
		right = keepLeaf.right;
		spatials = keepLeaf.spatials;
		// clear the leaf..
		// keepLeaf.left = null; keepLeaf.right = null; keepLeaf.gobjects =
		// null; keepLeaf.parent = null;

		if (spatials == null) {
			left.parent = this;
			right.parent = this; // reassign child parents..
			this.setDepth(this.depth); // this reassigns depth for our children
		} else {
			for (int i = 0; i < spatials.size; i++) {
				spatials.get(i).node = this;
			}
		}

		if (parent != null) {
			parent.childRefit(true);
		}
	}

	void childExpanded(BvhNode child) {
		if (!isValid(box) || !box.contains(child.box)) {
			box.ext(child.box);
			if (parent != null) {
				parent.childExpanded(this);
			}
		}
	}

	void childRefit(boolean recurse) {
		refitBox();

		if (recurse && parent != null) {
			parent.childRefit(true);
		}
	}

	private void refitBox() {
		if (isValid(left.box)) {
			box.set(left.box);
			if (isValid(right.box)) {
				box.ext(right.box);
			}
		} else if (isValid(right.box)) {
			box.set(right.box);
		}
	}

	@Override
	public void reset() {
		bvh = null;

		if (left != null) {
			left.free();
			left = null;
		}

		if (right != null) {
			right.free();
			right = null;
		}

		depth = 0;
		nodeNumber = 0;
		box.inf();
	}

	void free() {
		// TODO must first be obtained
		PoolService.free(this);
	}

	private static class RotationOption implements Comparable<RotationOption> {
		public float SAH;
		public Rotation rot;

		RotationOption(float SAH, Rotation rot) {
			this.SAH = SAH;
			this.rot = rot;
		}

		@Override
		public int compareTo(RotationOption other) {
			return Float.compare(SAH, other.SAH);
		}
	}

	private enum Rotation {
		NONE, L_RL, L_RR, R_LL, R_LR, LL_RR, LL_RL,
	}
}
