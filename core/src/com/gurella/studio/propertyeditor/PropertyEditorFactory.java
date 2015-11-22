package com.gurella.studio.propertyeditor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.audio.Pan;
import com.gurella.engine.audio.Pitch;
import com.gurella.engine.audio.Volume;
import com.gurella.engine.geometry.Angle;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.ResourceModelProperty;

public class PropertyEditorFactory {
	public static ResourcePropertyEditor<?> createEditor(ModelResourceFactory<?> factory, ResourceModelProperty property) {
		Class<?> propertyType = property.getPropertyType();

		if (propertyType == Boolean.class || propertyType == Boolean.TYPE) {
			return new BooleanPropertyEditor(property, factory);
		} else if (propertyType == Float.class || propertyType == Float.TYPE) {
			return new FloatPropertyEditor(property, factory);
		} else if (propertyType == Integer.class || propertyType == Integer.TYPE) {
			return new IntegerPropertyEditor(property, factory);
		} else if (propertyType == Long.class || propertyType == Long.TYPE) {
			return new LongPropertyEditor(property, factory);
		} else if (propertyType == String.class) {
			return new StringPropertyEditor(property, factory);
		} else if (ClassReflection.isAssignableFrom(Enum.class, propertyType)) {
			return new EnumPropertyEditor(property, factory);
		} else if (ClassReflection.isAssignableFrom(Angle.class, propertyType)) {
			return new AnglePropertyEditor(property, factory);
		} else if (ClassReflection.isAssignableFrom(Volume.class, propertyType)) {
			return new VolumePropertyEditor(property, factory);
		} else if (ClassReflection.isAssignableFrom(Pan.class, propertyType)) {
			return new PanPropertyEditor(property, factory);
		} else if (ClassReflection.isAssignableFrom(Pitch.class, propertyType)) {
			return new PitchPropertyEditor(property, factory);
		} else if (ClassReflection.isAssignableFrom(Vector3.class, propertyType)) {
			return new Vector3PropertyEditor(property, factory);
		}  else if (ClassReflection.isAssignableFrom(Texture.class, propertyType)) {
			return new AssetPropertyEditor<Texture>(property, factory, Texture.class);
		}/*  else if (ClassReflection.isAssignableFrom(TextureRegion.class, propertyType)) {
			return new AssetPropertyEditor<TextureRegion>(property, factory);
		}*/ else if (propertyType.isArray()) {
			return new ArrayPropertyEditor(property, factory);
		} else if (!propertyType.isInterface() && ClassReflection.isAssignableFrom(Object.class, propertyType)) {
			return new ComplexResourcePropertyEditor(property, factory);
		} else {
			return new DefaultPropertyEditor(property, factory);
		}
	}
}
