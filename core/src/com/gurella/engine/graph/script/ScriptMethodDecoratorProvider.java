package com.gurella.engine.graph.script;

public @interface ScriptMethodDecoratorProvider {
	Class<? extends ScriptMethodDecorator> decorator();
}
