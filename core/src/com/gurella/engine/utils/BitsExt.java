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
}
