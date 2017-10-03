package dev.kkorolyov.pancake.platform.storage.serialization;

/**
 * Parses a value to a certain type.
 */
public interface ValueParser {
	/** @return object parsed from {@code s} */
	Object parse(String s);

	/** @return {@code true} if able to parse {@code s} */
	boolean accepts(String s);
}