package com.gurella.engine.asset2.loader;

enum AssetLoadingState {
	ready, waitingDependencies, asyncLoading, syncLoading, finished;
}
