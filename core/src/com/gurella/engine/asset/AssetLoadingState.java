package com.gurella.engine.asset;

enum AssetLoadingState {
	ready, waitingDependencies, asyncLoading, syncLoading, finished;
}
