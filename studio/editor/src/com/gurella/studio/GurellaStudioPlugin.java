package com.gurella.studio;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.DeviceResourceManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.gurella.studio.editor.utils.RGBAColorDescriptor;

public class GurellaStudioPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.gurella.studio"; //$NON-NLS-1$
	public static final Object glMutex = new Object();

	private static GurellaStudioPlugin plugin;

	private static DeviceResourceManager resourceManager;
	private static FormToolkit toolkit;

	public GurellaStudioPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Display display = getDisplay();
		//http://www.eclipsezone.com/eclipse/forums/t61092.html  colors...
		toolkit = new FormToolkit(display);
		resourceManager = new DeviceResourceManager(display);
	}

	private static Display getDisplay() {
		Display display = Display.getCurrent();
		return display == null ? PlatformUI.getWorkbench().getDisplay() : display;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		toolkit.dispose();
		resourceManager.dispose();
	}

	/**
	 * Returns the shared instance
	 */
	public static GurellaStudioPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static DeviceResourceManager getResourceManager() {
		return resourceManager;
	}

	public static Image createImage(String path) {
		return resourceManager.createImage(getImageDescriptor(path));
	}

	public static Image createImageWithDefault(String path) {
		return resourceManager.createImageWithDefault(getImageDescriptor(path));
	}

	public static Image createImage(ImageDescriptor descriptor) {
		return resourceManager.createImage(descriptor);
	}

	public static Image createImageWithDefault(ImageDescriptor descriptor) {
		return resourceManager.createImageWithDefault(descriptor);
	}

	public static void destroyImage(String path) {
		resourceManager.destroyImage(getImageDescriptor(path));
	}

	public static void destroyImage(ImageDescriptor descriptor) {
		resourceManager.destroyImage(descriptor);
	}

	public static Color createColor(ColorDescriptor descriptor) {
		return resourceManager.createColor(descriptor);
	}

	public static Color createColor(RGB descriptor) {
		return resourceManager.createColor(descriptor);
	}

	public static Color createColor(int red, int green, int blue) {
		return resourceManager.createColor(new RGB(red, green, blue));
	}

	public static Color createColor(int red, int green, int blue, int alpha) {
		return resourceManager.createColor(new RGBAColorDescriptor(red, green, blue, alpha));
	}

	public static void destroyColor(RGB descriptor) {
		resourceManager.destroyColor(descriptor);
	}

	public static void destroyColor(int red, int green, int blue) {
		resourceManager.destroyColor(new RGB(red, green, blue));
	}

	public static void destroyColor(RGBA descriptor) {
		resourceManager.destroyColor(new RGBAColorDescriptor(descriptor));
	}

	public static void destroyColor(int red, int green, int blue, int alpha) {
		resourceManager.destroyColor(new RGBAColorDescriptor(red, green, blue, alpha));
	}

	public static void destroyColor(ColorDescriptor descriptor) {
		resourceManager.destroyColor(descriptor);
	}

	public static Font createFont(FontDescriptor descriptor) {
		return resourceManager.createFont(descriptor);
	}

	public static void destroyFont(FontDescriptor descriptor) {
		resourceManager.destroyFont(descriptor);
	}

	public static FormToolkit getToolkit() {
		return toolkit;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static IStatus log(Throwable t, String message) {
		MultiStatus status = createErrorStatus(t, message);
		getDefault().getLog().log(status);
		return status;
	}

	private static MultiStatus createErrorStatus(Throwable t, String message) {
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
		Status[] childStatuses = Arrays.stream(stackTraces)
				.map(st -> new Status(IStatus.ERROR, PLUGIN_ID, st.toString())).toArray(i -> new Status[i]);
		return new MultiStatus(PLUGIN_ID, IStatus.ERROR, childStatuses, message, t);
	}
}
