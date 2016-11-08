package com.gurella.engine.scene.renderable.terrain;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * Copied from https://github.com/mbrlabs/Mundus/blob/master/commons/src/main/com/mbrlabs/mundus/commons/terrain/TerrainTextureAttribute.java
 * @author Marcus Brummer
 */
public class TerrainTextureAttribute extends Attribute {

    public static final String ATTRIBUTE_SPLAT0_ALIAS = "splat0";
    public static final long ATTRIBUTE_SPLAT0 = register(ATTRIBUTE_SPLAT0_ALIAS);
    public static final String ATTRIBUTE_SPLAT1_ALIAS = "splat1";
    public static final long ATTRIBUTE_SPLAT1 = register(ATTRIBUTE_SPLAT1_ALIAS);

    public TerrainTexture terrainTexture;

    protected static long Mask = ATTRIBUTE_SPLAT0 | ATTRIBUTE_SPLAT1;

    public static boolean is(final long type) {
        return (type & Mask) != 0;
    }

    public TerrainTextureAttribute(long type, TerrainTexture terrainTexture) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.terrainTexture = terrainTexture;
    }

    public TerrainTextureAttribute(TerrainTextureAttribute other) {
        super(other.type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.terrainTexture = other.terrainTexture;
    }

    protected TerrainTextureAttribute(long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
    }

    @Override
    public Attribute copy() {
        return new TerrainTextureAttribute(this);
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        final long v = NumberUtils.doubleToLongBits(terrainTexture.hashCode());
        return prime * super.hashCode() + (int) (v ^ (v >>> 32));
    }

    @Override
    public int compareTo(Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        TerrainTexture otherValue = ((TerrainTextureAttribute) o).terrainTexture;
        return terrainTexture.equals(otherValue) ? 0 : -1;
    }
}
