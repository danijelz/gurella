package com.gurella.engine.application.events;

//TODO try to extract enum
public interface CommonUpdatePriority {
	public static int LOAD = 100;
	public static int INPUT = 200;
	public static int THINK = 300;
	public static int PHYSICS = 400;
	public static int UPDATE = 500;
	public static int PRE_RENDER = 600;
	public static int RENDER = 700;
	public static int POST_RENDER = 800;
	public static int DEBUG_RENDER = 900;
	public static int CLEANUP = 1000;
}
