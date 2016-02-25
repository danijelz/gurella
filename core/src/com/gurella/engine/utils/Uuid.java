package com.gurella.engine.utils;

import com.badlogic.gdx.math.RandomXS128;

public class Uuid implements Comparable<Uuid> {
	private static final RandomXS128 random = new RandomXS128();
	private static final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public final long mostSigBits;
	public final long leastSigBits;

	public Uuid(long mostSigBits, long leastSigBits) {
		this.mostSigBits = mostSigBits;
		this.leastSigBits = leastSigBits;
	}

	private Uuid(byte[] data) {
		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++) {
			msb = (msb << 8) | (data[i] & 0xff);
		}
		for (int i = 8; i < 16; i++) {
			lsb = (lsb << 8) | (data[i] & 0xff);
		}
		mostSigBits = msb;
		leastSigBits = lsb;
	}

	public static Uuid randomUuid() {
		return new Uuid(randomBytes());
	}

	private static byte[] randomBytes() {
		byte[] randomBytes = new byte[16];
		random.nextBytes(randomBytes);
		randomBytes[6] &= 0x0f; /* clear version */
		randomBytes[6] |= 0x40; /* set to version 4 */
		randomBytes[8] &= 0x3f; /* clear variant */
		randomBytes[8] |= 0x80; /* set to IETF variant */
		return randomBytes;
	}

	public static Uuid fromString(String name) {
		if (name.length() != 32) {
			throw new IllegalArgumentException("Invalid Uuid string: " + name);
		}

		long mostSigBits = toLong(name.substring(0, 16));
		long leastSigBits = toLong(name.substring(16));
		return new Uuid(mostSigBits, leastSigBits);
	}

	private static long toLong(String hex) throws NumberFormatException {
		long value = 0;
		for (int i = 0; i < 16; i++) {
			char c = hex.charAt(i);
			if (c >= '0' && c <= '9') {
				value = ((value << 4) | (0xff & (c - '0')));
			} else if (c >= 'a' && c <= 'f') {
				value = ((value << 4) | (0xff & (c - 'a' + 10)));
			} else if (c >= 'A' && c <= 'F') {
				value = ((value << 4) | (0xff & (c - 'A' + 10)));
			} else {
				throw new NumberFormatException("Invalid hex character: " + c);
			}
		}

		return value;
	}

	public static String randomUuidString() {
		byte[] data = randomBytes();
		long mostSigBits = 0;
		long leastSigBits = 0;
		for (int i = 0; i < 8; i++) {
			mostSigBits = (mostSigBits << 8) | (data[i] & 0xff);
		}
		for (int i = 8; i < 16; i++) {
			leastSigBits = (leastSigBits << 8) | (data[i] & 0xff);
		}
		return toString(mostSigBits, leastSigBits);
	}

	@Override
	public String toString() {
		char[] hexs = new char[32];

		long value = mostSigBits;
		for (int i = 0; i < 16; i++) {
			int c = (int) (value & 0xf);
			hexs[15 - i] = hexChars[c];
			value = value >> 4;
		}

		value = leastSigBits;
		for (int i = 0; i < 16; i++) {
			int c = (int) (value & 0xf);
			hexs[31 - i] = hexChars[c];
			value = value >> 4;
		}

		return toString(mostSigBits, leastSigBits);
	}

	private static String toString(long mostSigBits, long leastSigBits) {
		char[] hexs = new char[32];

		long value = mostSigBits;
		for (int i = 0; i < 16; i++) {
			int c = (int) (value & 0xf);
			hexs[15 - i] = hexChars[c];
			value = value >> 4;
		}

		value = leastSigBits;
		for (int i = 0; i < 16; i++) {
			int c = (int) (value & 0xf);
			hexs[31 - i] = hexChars[c];
			value = value >> 4;
		}

		return new String(hexs);
	}

	@Override
	public int hashCode() {
		long hilo = mostSigBits ^ leastSigBits;
		return ((int) (hilo >> 32)) ^ (int) hilo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != Uuid.class) {
			return false;
		}

		Uuid other = (Uuid) obj;
		return (mostSigBits == other.mostSigBits && leastSigBits == other.leastSigBits);
	}

	@Override
	public int compareTo(Uuid other) {
		int result = Values.compare(mostSigBits, other.mostSigBits);
		return result == 0 ? Values.compare(leastSigBits, other.leastSigBits) : result;
	}

	public static void main(String[] args) {
		Uuid uuid = new Uuid(0, 0);
		System.out.println(uuid);
		System.out.println(uuid.equals(Uuid.fromString(uuid.toString())));
		System.out.println("");
		uuid = Uuid.fromString("7f0fea10254242309897e52725a136c0");
		System.out.println(uuid);
		System.out.println(uuid.equals(Uuid.fromString(uuid.toString())));
		System.out.println("");
		uuid = Uuid.randomUuid();
		System.out.println(uuid);
		System.out.println(uuid.equals(Uuid.fromString(uuid.toString())));
	}
}
