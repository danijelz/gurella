package com.gurella.engine.application;

//TODO try to extract enum
public interface UpdateOrder {
	public static int INPUT = 100;
	public static int THINK = 200;
	public static int PHYSICS = 300;
	public static int PRE_RENDER = 400;
	public static int RENDER = 500;
	public static int DEBUG_RENDER = 600;
	public static int AFTER_RENDER = 700;
	public static int STATE_TRANSITION = 800;
	public static int CLEANUP = 900;
}
