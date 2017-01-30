package com.gurella.engine.asset2;

enum AssetLoadingState {
	ready, waitingDependencies, asyncLoading, syncLoading, finished;
}
