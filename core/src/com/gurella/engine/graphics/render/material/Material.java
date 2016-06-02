package com.gurella.engine.graphics.render.material;

import com.gurella.engine.graphics.render.path.RenderPath.RenderPathMaterialProperties;
import com.gurella.engine.graphics.render.shader.Shader;
import com.gurella.engine.graphics.render.shader.Shader.ShaderUniforms;

public class Material {
	Shader shader;
	ShaderUniforms shaderOverrides;
	RenderPathMaterialProperties renderPathOverrides;
	
	public void bind() {
		// TODO Auto-generated method stub
		
	}
}
