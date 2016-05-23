package com.gurella.engine.scene.renderable.debug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author Marcus Brummer
 */
// https://github.com/mbrlabs/Mundus/blob/master/editor/src/main/com/mbrlabs/mundus/shader/WireframeShader.java
public class WireframeShader extends BaseShader {
	private static final ObjectMap<Application, WireframeShader> instances = new ObjectMap<Application, WireframeShader>();

	private static final String VERTEX_SHADER = "com/gurella/engine/scene/renderable/debug/wire.vert.glsl";
	private static final String FRAGMENT_SHADER = "com/gurella/engine/scene/renderable/debug/wire.frag.glsl";

	protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
	protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));

	private ShaderProgram program;
	private final Matrix4 worldTransform = new Matrix4();

	public static WireframeShader getInstance() {
		WireframeShader instance = instances.get(Gdx.app);
		if (instance == null) {
			instance = new WireframeShader();
			instances.put(Gdx.app, instance);
		}
		return instance;
	}

	private WireframeShader() {
		String vert = Gdx.files.classpath(VERTEX_SHADER).readString();
		String frag = Gdx.files.classpath(FRAGMENT_SHADER).readString();

		program = new ShaderProgram(vert, frag);
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}

		init();
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
		Gdx.gl20.glLineWidth(2.4f);

		program.begin();
		set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
	}

	@Override
	public void render(Renderable renderable) {
		worldTransform.set(renderable.worldTransform);
		worldTransform.scale(1.002f, 1.002f, 1.002f);
		set(UNIFORM_TRANS_MATRIX, worldTransform);

		int primitiveType = renderable.meshPart.primitiveType;
		renderable.meshPart.primitiveType = GL20.GL_LINE_LOOP;
		renderable.meshPart.render(program);
		renderable.meshPart.primitiveType = primitiveType;
	}

	@Override
	public void end() {
		program.end();
		Gdx.gl20.glLineWidth(1f);
	}

	@Override
	public void dispose() {
		program.dispose();
	}
}
