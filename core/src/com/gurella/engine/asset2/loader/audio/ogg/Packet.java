package com.gurella.engine.asset2.loader.audio.ogg;

import com.badlogic.gdx.utils.Pool.Poolable;

public class Packet implements Poolable {
	public byte[] packet_base;
	public int packet;
	public int bytes;
	public int b_o_s;
	public int e_o_s;
	public long granulepos;
	public long packetno;

	@Override
	public void reset() {
		packet_base = null;
		packet = 0;
		bytes = 0;
		b_o_s = 0;
		e_o_s = 0;
		granulepos = 0;
		packetno = 0;
	}
}
