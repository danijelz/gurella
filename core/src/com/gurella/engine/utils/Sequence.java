package com.gurella.engine.utils;

import java.util.concurrent.atomic.AtomicInteger;

public final class Sequence {
	public static final int invalidId = -1;
	private static final AtomicInteger sequence = new AtomicInteger(0);

	private Sequence() {
	}

	public static int next() {
		return sequence.getAndIncrement();
	}
}
