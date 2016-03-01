package com.gurella.engine.utils;

import java.util.concurrent.atomic.AtomicInteger;

public final class SequenceGenerator {
	private static final AtomicInteger sequence = new AtomicInteger(0);

	private SequenceGenerator() {
	}

	public static int next() {
		return sequence.getAndIncrement();
	}
}
