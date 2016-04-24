package com.gurella.studio.editor.inspector;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.badlogic.gdx.utils.Disposable;
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
import com.gurella.studio.editor.swtgl.SwtLwjglGraphics;
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
		body.setLayout(new GridLayout());
		FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
		toolkit.adapt(this);

		ExpandableComposite group = toolkit.createExpandableComposite(body, ExpandableComposite.TWISTIE);
		group.setText("Diffuse");
		toolkit.adapt(group);
		group.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		group.setLayout(new GridLayout());
		TextureAttributeEditor editor = new TextureAttributeEditor(group);
		editor.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setClient(editor);
		group.setExpanded(true);

		GLData glData = new GLData();
		glData.redSize = 8;
		glData.greenSize = 8;
		glData.blueSize = 8;
		glData.alphaSize = 8;
		glData.depthSize = 16;
		glData.stencilSize = 0;
		glData.samples = 0;
		glData.doubleBuffer = false;
		SwtLwjglGraphics graphics = (SwtLwjglGraphics) Gdx.graphics;
		glData.shareContext = graphics.getGlCanvas();

		glCanvas = new GLCanvas(body, SWT.FLAT, glData);
		glCanvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

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

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.set(new DepthTestAttribute());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		synchronized (ModelInspectableContainer.mutex) {
			glCanvas.setCurrent();
			Gdx.gl20 = gl20;
			modelBatch = new ModelBatch();
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
			//ResourceService.unload(materialDescriptor);
			materialDescriptor.destroy();
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
			Gdx.gl20 = gl20;
			model.dispose();
			model = createSphere();
			instance = new ModelInstance(model);
		}
	}

	private Model createSphere() {
		float radius = sphere.getRadius();
		VertexAttributes attributes = materialDescriptor.createVertexAttributes(true, true);

		builder.begin();
		builder.part("sphere", GL20.GL_TRIANGLES, attributes, material).sphere(radius, radius, radius, 30, 30);
		Model result = builder.end();

		Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
		while (disposables.hasNext()) {
			Disposable disposable = disposables.next();
			if (disposable instanceof Texture) {
				disposables.remove();
			}
		}

		return result;
	}

	private class TextureAttributeEditor extends Composite {
		private ColorSelector colorSelector;
		private Spinner alphaSpinner;
		private Button colorEnabledButton;

		private TextureSelector textureSelector;
		private Button textureEnabledButton;
		private Text offsetU;
		private Text offsetV;
		private Text scaleU;
		private Text scaleV;

		public TextureAttributeEditor(Composite parent) {
			super(parent, SWT.NONE);

			FormToolkit toolkit = GurellaStudioPlugin.getToolkit();
			toolkit.adapt(this);

			GridLayout layout = new GridLayout(5, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 5;
			layout.verticalSpacing = 5;
			setLayout(layout);

			toolkit.createLabel(this, "RGB:");
			colorSelector = new ColorSelector(this);

			toolkit.createLabel(this, "A:");
			alphaSpinner = new Spinner(this, SWT.BORDER);
			alphaSpinner.setMinimum(0);
			alphaSpinner.setMaximum(255);
			alphaSpinner.setIncrement(1);
			alphaSpinner.setPageIncrement(1);
			toolkit.adapt(alphaSpinner);

			colorEnabledButton = toolkit.createButton(this, "Enabled", SWT.CHECK);

			Color color = materialDescriptor.diffuseColor;
			if (color == null) {
				colorSelector.setColorValue(new RGB(255, 255, 255));
				alphaSpinner.setSelection(255);
				colorEnabledButton.setSelection(false);
			} else {
				colorSelector.setColorValue(new RGB((int) color.r * 255, (int) color.g * 255, (int) color.b * 255));
				alphaSpinner.setSelection((int) color.a * 255);
				colorEnabledButton.setSelection(true);
			}
			enableColor();

			colorEnabledButton.addListener(SWT.Selection, e -> enableColor());
			colorSelector.addListener(e -> valueChanged());
			alphaSpinner.addModifyListener(e -> valueChanged());

			Label label = toolkit.createLabel(this, "", SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

			toolkit.createLabel(this, "Texture:");
			textureSelector = new TextureSelector(this);
			toolkit.adapt(textureSelector);
			textureSelector.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));

			textureEnabledButton = toolkit.createButton(this, "Enabled", SWT.CHECK);
			textureEnabledButton.setSelection(materialDescriptor.isDiffuseTextureEnabled());
			textureEnabledButton.addListener(SWT.Selection, e -> enableTexture());

			label = toolkit.createLabel(this, "Offset U:");
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			offsetU = UiUtils.createFloatWidget(this);
			offsetU.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			offsetU.addModifyListener(e -> valueChanged());

			label = toolkit.createLabel(this, " V:");
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			offsetV = UiUtils.createFloatWidget(this);
			offsetV.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			offsetV.addModifyListener(e -> valueChanged());
			label = toolkit.createLabel(this, " ");
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

			toolkit.createLabel(this, "Scale U:");
			scaleU = UiUtils.createFloatWidget(this);
			scaleU.addModifyListener(e -> valueChanged());

			toolkit.createLabel(this, " V:");
			scaleV = UiUtils.createFloatWidget(this);
			scaleV.addModifyListener(e -> valueChanged());
			label = toolkit.createLabel(this, " ");
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

			enableTexture();
		}

		private void enableColor() {
			boolean selection = colorEnabledButton.getSelection();
			colorSelector.setEnabled(selection);
			alphaSpinner.setEnabled(selection);
		}

		private void enableTexture() {
			boolean selection = textureEnabledButton.getSelection();
			textureSelector.setEnabled(selection);
			offsetU.setEnabled(selection);
			offsetV.setEnabled(selection);
			scaleU.setEnabled(selection);
			scaleV.setEnabled(selection);
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
			protected void assetSelectionChanged(Texture oldAsset, Texture newAsset) {
				if (oldAsset != null) {
					materialDescriptor.unload(oldAsset);
				}
				if (newAsset != null) {
					materialDescriptor.bindAsset(newAsset);
				}
				valueChanged();
			}
		}
	}
}
