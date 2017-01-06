package com.gurella.engine.scene.renderable.skybox;

import static com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute.EnvironmentMap;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author Marcus Brummer
 * @version 08-01-2016
 *          https://github.com/mbrlabs/Mundus/blob/master/commons/src/main/com/mbrlabs/mundus/commons/skybox/SkyboxShader.java
 */
public class SkyboxShader extends BaseShader {
	private static final ObjectMap<Application, SkyboxShader> instances = new ObjectMap<Application, SkyboxShader>();

	private final String VERTEX_SHADER = "com/gurella/engine/scene/renderable/skybox/skybox.vert.glsl";
	private final String FRAGMENT_SHADER = "com/gurella/engine/scene/renderable/skybox/skybox.frag.glsl";

	protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
	protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
	protected final int UNIFORM_TEXTURE = register(new Uniform("u_texture"));

	protected final int UNIFORM_FOG = register(new Uniform("u_fog"));
	protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"));

	private Matrix4 transform = new Matrix4();

	public static SkyboxShader getInstance() {
		SkyboxShader instance = instances.get(Gdx.app);
		if (instance == null) {
			instance = new SkyboxShader();
			instances.put(Gdx.app, instance);
		}
		return instance;
	}

	private SkyboxShader() {
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
		context.begin();
		program.begin();

		set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
		transform.idt();
		transform.translate(camera.position);
		set(UNIFORM_TRANS_MATRIX, transform);
	}

	@Override
	public void render(Renderable renderable) {
		CubemapAttribute cubemapAttribute = (CubemapAttribute) renderable.material.get(EnvironmentMap);
		set(UNIFORM_TEXTURE, cubemapAttribute.textureDescription);
		set(UNIFORM_FOG, 0);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		context.end();
		program.end();
	}

	@Override
	public void dispose() {
		program.dispose();
	}

}