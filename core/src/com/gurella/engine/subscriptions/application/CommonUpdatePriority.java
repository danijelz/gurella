package com.gurella.engine.subscriptions.application;

public interface CommonUpdatePriority {
	int IO = -400;
	int INPUT = -300;
	int LOGIC = -200;
	int PHYSICS = -100;
	int UPDATE = 0;
	int PRE_RENDER = 100;
	int RENDER = 200;
	int POST_RENDER = 300;
	int DEBUG_RENDER = 400;
	int CLEANUP = 500;
}
