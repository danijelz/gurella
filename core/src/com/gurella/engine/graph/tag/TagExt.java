package com.gurella.engine.graph.tag;

import com.badlogic.gdx.utils.Bits;

//TODO unused add tag family so singletone tags by family can replace layer
public class TagExt {
	public int id;
	public String name;
	public boolean abstractTag;
	public boolean singletone;
	public TagExt superTag;
	
	public TagExt() {
		
	}
	
	public int getSuper() {
		return 0;
	}
	
	public Bits getSubtypes() {
		return null;
	}
	
	public static class Tags {
		public Bits tagBits = new Bits();
		
		public void setTag(TagExt tag) {
			tagBits.set(tag.id);
			tagBits.or(tag.getSubtypes());
		}
		
		public void unsetTag(TagExt tag) {
			tagBits.clear(tag.id);
			tagBits.xor(tag.getSubtypes());
		}
	}
	
	
	public static class TagGroup {
		public String name;
		public boolean singletone;
		
		public Bits getTags() {
			return null;
		}
	}
}
