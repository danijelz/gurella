package com.gurella.engine.graph.script;

public interface ScriptSystemListener {
	void activate();
	
	void deactivate();
	
	void associateComponentWithMethod(ScriptMethod scriptMethod, ScriptComponent component);

	void disassociateComponentWithMethod(ScriptMethod scriptMethod, ScriptComponent component);
}