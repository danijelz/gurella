package com.gurella.engine.asset;

enum AssetLoadingPhase {
	ready, waitingDependencies, async, sync, finished;
}
