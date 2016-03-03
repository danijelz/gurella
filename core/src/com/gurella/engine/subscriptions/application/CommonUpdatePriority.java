package com.gurella.engine.subscriptions.application;

public interface CommonUpdatePriority {
	public static int IO = -400;
	public static int INPUT = -300;
	public static int LOGIC = -200;
	public static int PHYSICS = -100;
	public static int UPDATE = 0;
	public static int LATE_UPDATE = 100;
	public static int PRE_RENDER = 200;
	public static int RENDER = 300;
	public static int POST_RENDER = 400;
	public static int DEBUG_RENDER = 500;
	public static int CLEANUP = 600;
}
