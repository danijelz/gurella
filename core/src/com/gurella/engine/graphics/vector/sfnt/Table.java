package com.gurella.engine.graphics.vector.sfnt;

public class Table {
	public final RandomAccessFile raf;
	public final int offset;

	public Table(RandomAccessFile raf, int offset) {
		this.raf = raf;
		this.offset = offset;
	}

	public void setPosition(int relativeOffset) {
		raf.setPosition(this.offset + relativeOffset);
	}

	public void setPosition(Offset relativeOffset) {
		raf.setPosition(this.offset + relativeOffset.getOffset());
	}

	public int getPosition() {
		return raf.getPosition() - offset;
	}

	public float readFixed() {
		return raf.readFixed();
	}

	public float readFixed(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readFixed();
	}

	public float readFixed(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readFixed();
	}

	public String readString(int length) {
		return raf.readString(length, "ISO-8859-1");
	}

	public String readString(int relativeOffset, int length) {
		setPosition(relativeOffset);
		return raf.readString(length, "ISO-8859-1");
	}

	public String readString(Offset relativeOffset, int length) {
		setPosition(relativeOffset);
		return raf.readString(length, "ISO-8859-1");
	}

	public String readString(int length, String encoding) {
		return raf.readString(length, encoding);
	}

	public String readString(int relativeOffset, int length, String encoding) {
		setPosition(relativeOffset);
		return raf.readString(length, encoding);
	}

	public String readString(Offset relativeOffset, int length, String encoding) {
		setPosition(relativeOffset);
		return raf.readString(length, encoding);
	}

	public final boolean readBoolean() {
		return raf.readBoolean();
	}

	public final boolean readBoolean(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readBoolean();
	}

	public final boolean readBoolean(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readBoolean();
	}

	public final byte readByte() {
		return raf.readByte();
	}

	public final byte readByte(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readByte();
	}

	public final byte readByte(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readByte();
	}

	public final short readUnsignedByte() {
		return raf.readUnsignedByte();
	}

	public final short readUnsignedByte(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedByte();
	}

	public final short readUnsignedByte(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedByte();
	}

	public int readUnsignedInt24() {
		return raf.readUnsignedInt24();
	}

	public int readUnsignedInt24(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedInt24();
	}

	public int readUnsignedInt24(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedInt24();
	}

	public long readUnsignedInt() {
		return raf.readUnsignedInt();
	}

	public long readUnsignedInt(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedInt();
	}

	public long readUnsignedInt(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedInt();
	}

	public int readUnsignedIntAsInt() {
		return raf.readUnsignedIntAsInt();
	}

	public int readUnsignedIntAsInt(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedIntAsInt();
	}

	public int readUnsignedIntAsInt(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedIntAsInt();
	}

	public short readShort() {
		return raf.readShort();
	}

	public short readShort(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readShort();
	}

	public short readShort(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readShort();
	}

	public int readUnsignedShort() {
		return raf.readUnsignedShort();
	}

	public int readUnsignedShort(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedShort();
	}

	public int readUnsignedShort(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedShort();
	}

	public int[] readUnsignedShortArray(int length) {
		return raf.readUnsignedShortArray(length);
	}

	public int[] readUnsignedShortArray(int relativeOffset, int length) {
		setPosition(relativeOffset);
		return raf.readUnsignedShortArray(length);
	}

	public int[] readUnsignedShortArray(Offset relativeOffset, int length) {
		setPosition(relativeOffset);
		return raf.readUnsignedShortArray(length);
	}

	public final char readChar() {
		return raf.readChar();
	}

	public final char readChar(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readChar();
	}

	public final char readChar(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readChar();
	}

	public int readInt() {
		return raf.readInt();
	}

	public int readInt(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readInt();
	}

	public int readInt(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readInt();
	}

	public long readLong() {
		return raf.readLong();
	}

	public long readLong(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readLong();
	}

	public long readLong(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readLong();
	}

	public long readUnsignedLong() {
		return raf.readUnsignedLong();
	}

	public long readUnsignedLong(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedLong();
	}

	public long readUnsignedLong(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readUnsignedLong();
	}

	public final float readFloat() {
		return raf.readFloat();
	}

	public final float readFloat(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readFloat();
	}

	public final float readFloat(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readFloat();
	}

	public final double readDouble() {
		return raf.readDouble();
	}

	public final double readDouble(int relativeOffset) {
		setPosition(relativeOffset);
		return raf.readDouble();
	}

	public final double readDouble(Offset relativeOffset) {
		setPosition(relativeOffset);
		return raf.readDouble();
	}

	public byte[] readBytes(int numberOfBytes) {
		return raf.readBytes(numberOfBytes);
	}

	public byte[] readBytes(int relativeOffset, int numberOfBytes) {
		setPosition(relativeOffset);
		return raf.readBytes(numberOfBytes);
	}

	public byte[] readBytes(Offset relativeOffset, int numberOfBytes) {
		setPosition(relativeOffset);
		return raf.readBytes(numberOfBytes);
	}

	public void read(byte[] dst, int arrayOffset, int len) {
		raf.read(dst, arrayOffset, len);
	}

	public void read(int relativeOffset, byte[] dst, int arrayOffset, int len) {
		setPosition(relativeOffset);
		raf.read(dst, arrayOffset, len);
	}

	public void read(Offset relativeOffset, byte[] dst, int arrayOffset, int len) {
		setPosition(relativeOffset);
		raf.read(dst, arrayOffset, len);
	}

	public interface Offset {
		int getOffset();
	}
}
