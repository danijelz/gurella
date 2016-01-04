package com.gurella.engine.application;

//TODO try to extract enum
public interface CommonUpdatePriority {
	public static int INPUT = 100;
	public static int THINK = 200;
	public static int PHYSICS = 300;
	public static int UPDATE = 400;
	public static int PRE_RENDER = 500;
	public static int RENDER = 600;
	public static int POST_RENDER = 800;
	public static int CLEANUP = 900;
}
