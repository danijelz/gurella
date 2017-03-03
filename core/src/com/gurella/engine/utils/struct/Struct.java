package com.gurella.engine.utils.struct;

import com.badlogic.gdx.utils.Array;

public abstract class Struct {
	Buffer buffer;
	int offset;

	protected Struct() {
		StructType.get(getClass());
	}

	public Struct(Buffer buffer, int offset) {
		StructType.get(getClass());
		this.offset = offset;
		this.buffer = buffer;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Struct other = (Struct) obj;
		return buffer.equals(offset, other.buffer, other.offset, StructType.get(getClass()).size);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode() + buffer.hashCode(offset, StructType.get(getClass()).size);
	}

	@Override
	public String toString() {
		StructType<?> structType = StructType.get(getClass());
		StringBuilder builder = new StringBuilder();
		builder.append(structType.type.getSimpleName());
		builder.append("{");
		Array<StructProperty> properties = structType._orderedProperties;
		for (int i = 0, n = properties.size; i < n; i++) {
			StructProperty property = properties.get(i);
			builder.append(property.name);
			builder.append("=");
			builder.append(property.toString(this));

			if (i < n - 1) {
				builder.append(", ");
			}
		}
		builder.append("}");
		return builder.toString();
	}
}
