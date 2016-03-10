package com.gurella.engine.utils;

import com.badlogic.gdx.utils.Bits;

public class ImmutableBits {
	public static final ImmutableBits empty = new ImmutableBits(new Bits());

	private Bits bits;

	public ImmutableBits(Bits bits) {
		this.bits = bits;
	}

	public boolean get(int index) {
		return bits.get(index);
	}

	public int numBits() {
		return bits.numBits();
	}

	public int length() {
		return bits.length();
	}

	public boolean isEmpty() {
		return bits.isEmpty();
	}

	public int nextSetBit(int fromIndex) {
		return bits.nextSetBit(fromIndex);
	}

	public int nextClearBit(int fromIndex) {
		return bits.nextClearBit(fromIndex);
	}

	public boolean intersects(Bits other) {
		return bits.intersects(other);
	}

	public boolean intersects(ImmutableBits other) {
		return bits.intersects(other.bits);
	}

	public boolean containsAll(Bits other) {
		return bits.containsAll(other);
	}

	public boolean containsAll(ImmutableBits other) {
		return bits.containsAll(other.bits);
	}

	public void andBits(Bits other) {
		other.and(bits);
	}

	public void andNotBits(Bits other) {
		other.andNot(bits);
	}

	public void orBits(Bits other) {
		other.or(bits);
	}

	public void xorBits(Bits other) {
		other.xor(bits);
	}

	@Override
	public int hashCode() {
		return 31 * bits.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ImmutableBits)) {
			return false;
		}

		ImmutableBits other = (ImmutableBits) obj;
		return bits.equals(other.bits);
	}
}
