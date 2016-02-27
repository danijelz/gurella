package com.gurella.engine.subscriptions.application;

public interface CommonUpdatePriority {
	public static int IO = -400;
	public static int INPUT = -300;
	public static int THINK = -200;
	public static int PHYSICS = -100;
	public static int UPDATE = 0;
	public static int PRE_RENDER = 100;
	public static int RENDER = 200;
	public static int POST_RENDER = 300;
	public static int DEBUG_RENDER = 400;
	public static int CLEANUP = 500;
}
