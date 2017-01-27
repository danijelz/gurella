package com.gurella.engine.asset2.loader;

enum AssetLoadingState {
	ready, waitingDependencies, syncLoading, asyncLoading, finished, error;
}
