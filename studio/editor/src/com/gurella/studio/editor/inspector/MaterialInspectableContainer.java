package com.gurella.studio.editor.inspector;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.gurella.engine.base.resource.ResourceService;
import com.gurella.engine.graphics.material.MaterialDescriptor;
import com.gurella.engine.graphics.material.MaterialDescriptor.TextureAttributeProperties;
import com.gurella.engine.math.geometry.shape.Sphere;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.common.AssetSelectionWidget;
import com.gurella.studio.editor.common.UiUtils;
import com.gurella.studio.editor.scene.Compass;
import com.gurella.studio.editor.swtgl.LwjglGL20;
import com.gurella.studio.editor.swtgl.SwtLwjglInput;

public class MaterialInspectableContainer extends InspectableContainer<IFile> {
	private ModelBuilder builder;
	private Sphere sphere;
	private Model model;
	private ModelInstance instance;
	private Material material;

	private GLCanvas glCanvas;

	private LwjglGL20 gl20 = new LwjglGL20();
	private SwtLwjglInput input;

	private PerspectiveCamera cam;
	private CameraInputController camController;
	private ModelBatch modelBatch;
	private Environment environment;
	private Color backgroundColor = new Color(0.501960f, 0.501960f, 0.501960f, 1f);
	private Compass compass;

	private MaterialDescriptor materialDescriptor;

	public MaterialInspectableContainer(InspectorView parent, IFile target) {
		super(parent, target);

		materialDescriptor = ResourceService.load(target.getLocation().toString());

		Composite body = getBody();
		body.setLayout(new GridLayout(2, false));
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		TextureAttributeEditor editor = new TextureAttributeEditor(body);
		editor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = false;

		glCanvas = new GLCanvas(body, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Point size = glCanvas.getSize();
		cam = new PerspectiveCamera(67, size.x, size.y);
		cam.position.set(0f, 1f, -1f);
		cam.lookAt(0, 0, 0);
		cam.near = 0.1f;
		cam.far = 1000;
		cam.update();

		glCanvas.addListener(SWT.Resize, e -> updateSizeByParent());

		camController = new CameraInputController(cam);
		input = new SwtLwjglInput(glCanvas);
		input.setInputProcessor(camController);

		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		synchronized (ModelInspectableContainer.mutex) {
			glCanvas.setCurrent();
			sphere = new Sphere();
			sphere.setRadius(0.8f);
			material = materialDescriptor.createMaterial();
			builder = new ModelBuilder();
			model = createSphere();
			instance = new ModelInstance(model);
			compass = new Compass(cam);
		}

		addDisposeListener(e -> onDispose());
		render();
	}

	private void onDispose() {
		synchronized (ModelInspectableContainer.mutex) {
			model.dispose();
			modelBatch.dispose();
			compass.dispose();
			glCanvas.dispose();
		}
	}

	private void updateSizeByParent() {
		Point size = glCanvas.getSize();
		cam.viewportWidth = size.x;
		cam.viewportHeight = size.y;
		cam.update(true);
	}

	private void render() {
		input.update();
		camController.update();
		synchronized (ModelInspectableContainer.mutex) {
			if (glCanvas.isDisposed()) {
				return;
			}

			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			Point size = glCanvas.getSize();
			Color color = backgroundColor;
			gl20.glClearColor(color.r, color.g, color.b, color.a);
			gl20.glEnable(GL20.GL_DEPTH_TEST);
			gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			gl20.glViewport(0, 0, size.x, size.y);
			modelBatch.begin(cam);
			modelBatch.render(instance, environment);
			compass.render(modelBatch);
			modelBatch.end();
		}

		getDisplay().timerExec(60, this::render);
	}

	private void refreshMaterial() {
		synchronized (ModelInspectableContainer.mutex) {
			material = materialDescriptor.createMaterial();
			glCanvas.setCurrent();
			model.dispose();
			model = createSphere();
			instance = new ModelInstance(model);
		}
	}

	private Model createSphere() {
		float radius = sphere.getRadius();
		builder.begin();
		builder.part("sphere", GL20.GL_TRIANGLES, materialDescriptor.createVertexAttributes(true, true), material)
				.sphere(radius, radius, radius, 30, 30);
		return builder.end();
	}

	private class TextureAttributeEditor extends Composite {
		private ColorSelector colorSelector;
		private Spinner alphaSpinner;

		private TextureSelector textureSelector;
		private Text offsetU;
		private Text offsetV;
		private Text scaleU;
		private Text scaleV;

		public TextureAttributeEditor(Composite parent) {
			super(parent, SWT.NONE);

			GridLayout layout = new GridLayout(2, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			layout.verticalSpacing = 0;
			setLayout(layout);

			colorSelector = new ColorSelector(this);
			alphaSpinner = new Spinner(this, SWT.NONE);
			GurellaStudioPlugin.getToolkit().adapt(this);
			alphaSpinner.setMinimum(0);
			alphaSpinner.setMaximum(255);
			alphaSpinner.setIncrement(1);
			alphaSpinner.setPageIncrement(1);

			Color color = materialDescriptor.diffuseColor;
			if (color == null) {
				colorSelector.setColorValue(new RGB(255, 255, 255));
				alphaSpinner.setSelection(255);
			} else {
				colorSelector.setColorValue(new RGB((int) color.r * 255, (int) color.g * 255, (int) color.b * 255));
				alphaSpinner.setSelection((int) color.a * 255);
			}
			colorSelector.addListener(e -> valueChanged());
			alphaSpinner.addModifyListener(e -> valueChanged());

			textureSelector = new TextureSelector(this);
			textureSelector.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

			offsetU = UiUtils.createFloatWidget(this);
			offsetU.addModifyListener(e -> valueChanged());

			offsetV = UiUtils.createFloatWidget(this);
			offsetV.addModifyListener(e -> valueChanged());

			scaleU = UiUtils.createFloatWidget(this);
			scaleU.addModifyListener(e -> valueChanged());

			scaleV = UiUtils.createFloatWidget(this);
			scaleV.addModifyListener(e -> valueChanged());
		}

		private void valueChanged() {
			RGB rgb = colorSelector.getColorValue();
			int a = alphaSpinner.getSelection();
			float r = 1 / 255f;
			materialDescriptor.diffuseColor = new Color(rgb.red * r, rgb.green * r, rgb.blue * r, a * r);

			TextureAttributeProperties properties = materialDescriptor.diffuseTexture;
			properties.texture = textureSelector.getAsset();

			String textValue = offsetU.getText();
			properties.offsetU = Values.isBlank(textValue) ? 0 : Float.valueOf(textValue).floatValue();

			textValue = offsetV.getText();
			properties.offsetV = Values.isBlank(textValue) ? 0 : Float.valueOf(textValue).floatValue();

			textValue = scaleU.getText();
			properties.scaleU = Values.isBlank(textValue) ? 1 : Float.valueOf(textValue).floatValue();

			textValue = scaleV.getText();
			properties.scaleV = Values.isBlank(textValue) ? 1 : Float.valueOf(textValue).floatValue();

			refreshMaterial();
		}

		private class TextureSelector extends AssetSelectionWidget<Texture> {
			public TextureSelector(TextureAttributeEditor parent) {
				super(parent, Texture.class);
			}

			@Override
			protected void assetSelected(Texture asset) {
				valueChanged();
			}
		}
	}
}
