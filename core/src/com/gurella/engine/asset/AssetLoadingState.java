package com.gurella.engine.asset;

enum AssetLoadingState {
	ready, waitingForDependencies, readyForSyncLoading, readyForAsyncLoading, finished, error;
}