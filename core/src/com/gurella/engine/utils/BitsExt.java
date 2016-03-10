package com.gurella.engine.utils;

import com.badlogic.gdx.utils.Bits;

public class BitsExt extends Bits {
	private ImmutableBits immutable;

	public BitsExt() {
		super();
	}

	public BitsExt(int nbits) {
		super(nbits);
	}

	public ImmutableBits immutable() {
		if (immutable == null) {
			immutable = new ImmutableBits(this);
		}

		return immutable;
	}

	@Override
	public int hashCode() {
		return 31 + super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BitsExt)) {
			return false;
		}

		BitsExt other = (BitsExt) obj;
		int length = length();
		if (length != other.length()) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (get(i) != other.get(i)) {
				return false;
			}
		}

		return true;
	}
}
