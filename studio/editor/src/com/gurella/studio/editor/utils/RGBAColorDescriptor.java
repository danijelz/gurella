package com.gurella.studio.editor.utils;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGBA;

public class RGBAColorDescriptor extends ColorDescriptor {
	private RGBA color;

	/**
	 * Color being copied, or null if none
	 */
	private Color originalColor = null;

	public RGBAColorDescriptor(RGBA color) {
		this.color = color;
	}

	public RGBAColorDescriptor(int red, int green, int blue, int alpha) {
		this.color = new RGBA(red, green, blue, alpha);
	}

	public RGBAColorDescriptor(Color originalColor) {
		this(originalColor.getRGBA());
		this.originalColor = originalColor;
	}

	public RGBA getColor() {
		return new RGBA(color.rgb.red, color.rgb.green, color.rgb.blue, color.alpha);
	}

	@Override
	public Color createColor(Device device) {
		// If this descriptor is wrapping an existing color, then we can return the original color
		// if this is the same device.
		if (originalColor != null) {
			// If we're allocating on the same device as the original color, return the original.
			if (originalColor.getDevice() == device) {
				return originalColor;
			}
		}

		return new Color(device, color);
	}

	@Override
	public void destroyColor(Color toDestroy) {
		if (toDestroy == originalColor) {
			return;
		}

		toDestroy.dispose();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RGBAColorDescriptor) {
			RGBAColorDescriptor other = (RGBAColorDescriptor) obj;
			return other.color.equals(color) && other.originalColor == originalColor;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return color.hashCode();
	}
}
