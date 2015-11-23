package com.gurella.engine.graph.script;

import com.badlogic.gdx.utils.Array;

public interface ScriptSystemExtension {
	Array<ScriptMethod> getScriptMethods();

	ScriptSystemListener getScriptSystemListener();
}
