package com.gurella.engine.asset;

public interface DependencyTracker {
	void increaseDependencyRefCount(String dependency);
}
