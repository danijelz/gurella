package com.gurella.engine.application;

import com.gurella.engine.disposable.DisposablesService;
import com.gurella.engine.pool.PoolService;

//TODO unused
public class ApplicationContext {
	private ApplicationContext() {
	}
	
	public DisposablesService disposablesService;
	public PoolService poolService;
}
