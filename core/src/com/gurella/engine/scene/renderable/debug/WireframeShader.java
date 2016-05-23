package com.gurella.engine.scene.renderable.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

//https://github.com/mbrlabs/Mundus/blob/master/editor/src/main/com/mbrlabs/mundus/shader/WireframeShader.java
public class WireframeShader extends BaseShader {
	private static final String VERTEX_SHADER = "com/gurella/engine/scene/renderable/debug/wire.vert.glsl";
	private static final String FRAGMENT_SHADER = "com/gurella/engine/scene/renderable/debug/wire.frag.glsl";

	protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
	protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));

	private ShaderProgram program;

	public WireframeShader() {
		super();
		String vert = Gdx.files.classpath(VERTEX_SHADER).readString();
		String frag = Gdx.files.classpath(FRAGMENT_SHADER).readString();

		program = new ShaderProgram(vert, frag);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}
	}

	@Override
	public void init() {
		super.init(program, null);
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.context = context;
		this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
		this.context.setDepthMask(true);

		program.begin();

		set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
	}

	@Override
	public void render(Renderable renderable) {
		renderable.worldTransform.scale(1.01f, 1.01f, 1.01f);
		set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);
		int primitiveType = renderable.meshPart.primitiveType;
		renderable.meshPart.primitiveType = GL20.GL_LINE_LOOP;
		renderable.meshPart.render(program);
		renderable.meshPart.primitiveType = primitiveType;
	}

	@Override
	public void end() {
		program.end();
	}

	@Override
	public void dispose() {
		program.dispose();
	}
}
