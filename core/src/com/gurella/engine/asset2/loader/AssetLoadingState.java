package com.gurella.engine.asset2.loader;

enum AssetLoadingState {
	ready, syncLoading, waitingDependencies, asyncLoading, finished, error;
}
