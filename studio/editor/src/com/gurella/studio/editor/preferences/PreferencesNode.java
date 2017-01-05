package com.gurella.studio.editor.preferences;

import static java.lang.Boolean.TRUE;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import org.osgi.service.prefs.Preferences;

import com.gurella.studio.editor.utils.Try;

public class PreferencesNode {
	private Preferences preferences;

	public PreferencesNode(Preferences preferences) {
		this.preferences = preferences;
	}

	public PreferencesNode put(String key, String value) {
		preferences.put(key, value);
		return this;
	}

	public PreferencesNode get(String key, String def, Consumer<String> consumer) {
		consumer.accept(preferences.get(key, def));
		return this;
	}

	public String get(String key, String def) {
		return preferences.get(key, def);
	}

	public PreferencesNode remove(String key) {
		preferences.remove(key);
		return this;
	}

	public PreferencesNode clear() {
		Try.unchecked(() -> preferences.clear());
		return this;
	}

	public PreferencesNode putInt(String key, int value) {
		preferences.putInt(key, value);
		return this;
	}

	public PreferencesNode getInt(String key, int def, IntConsumer consumer) {
		consumer.accept(preferences.getInt(key, def));
		return this;
	}

	public int getInt(String key, int def) {
		return preferences.getInt(key, def);
	}

	public PreferencesNode putLong(String key, long value) {
		preferences.putLong(key, value);
		return this;
	}

	public PreferencesNode getLong(String key, long def, LongConsumer consumer) {
		consumer.accept(preferences.getLong(key, def));
		return this;
	}

	public long getLong(String key, long def) {
		return preferences.getLong(key, def);
	}

	public PreferencesNode putBoolean(String key, boolean value) {
		preferences.putBoolean(key, value);
		return this;
	}

	public PreferencesNode getBoolean(String key, boolean def, Consumer<Boolean> consumer) {
		consumer.accept(Boolean.valueOf(preferences.getBoolean(key, def)));
		return this;
	}

	public boolean getBoolean(String key, boolean def) {
		return preferences.getBoolean(key, def);
	}

	public PreferencesNode putFloat(String key, float value) {
		preferences.putFloat(key, value);
		return this;
	}

	public PreferencesNode getFloat(String key, float def, Consumer<Float> consumer) {
		consumer.accept(Float.valueOf(preferences.getFloat(key, def)));
		return this;
	}

	public float getFloat(String key, float def) {
		return preferences.getFloat(key, def);
	}

	public PreferencesNode putDouble(String key, double value) {
		preferences.putDouble(key, value);
		return this;
	}

	public PreferencesNode getDouble(String key, double def, DoubleConsumer consumer) {
		consumer.accept(preferences.getDouble(key, def));
		return this;
	}

	public double getDouble(String key, double def) {
		return preferences.getDouble(key, def);
	}

	public PreferencesNode putByteArray(String key, byte[] value) {
		preferences.putByteArray(key, value);
		return this;
	}

	public PreferencesNode getByteArray(String key, byte[] def, Consumer<byte[]> consumer) {
		consumer.accept(preferences.getByteArray(key, def));
		return this;
	}

	public byte[] getByteArray(String key, byte[] def) {
		return preferences.getByteArray(key, def);
	}

	public Optional<PreferencesNode> parent() {
		return Optional.ofNullable(preferences.parent()).map(PreferencesNode::new);
	}

	public PreferencesNode node(String pathName) {
		return new PreferencesNode(preferences.node(pathName));
	}

	public PreferencesNode node(Class<?> pathId) {
		return new PreferencesNode(preferences.node(pathId.getName()));
	}

	public PreferencesNode node(String pathName, Consumer<PreferencesNode> consumer) {
		PreferencesNode node = node(pathName);
		consumer.accept(node);
		return node;
	}

	public Optional<PreferencesNode> nodeIfExists(String pathName) {
		return Try.ofFailable(() -> Boolean.valueOf(preferences.nodeExists(pathName))).filter(b -> TRUE.equals(b))
				.map(b -> node(pathName)).toOptional();
	}

	public Optional<PreferencesNode> removeNode() {
		Preferences parent = preferences.parent();
		return Try.successful(preferences).peek(p -> p.removeNode()).map(n -> new PreferencesNode(parent)).toOptional();
	}

	public PreferencesNode flush() {
		Try.unchecked(() -> preferences.flush());
		return this;
	}

	public PreferencesNode sync() {
		Try.unchecked(() -> preferences.sync());
		return this;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public PreferencesNode preferences(Consumer<Preferences> consumer) {
		consumer.accept(preferences);
		return this;
	}
}