package de.mwvb.maja.redis;

public interface Translator<T> {

	/**
	 * @param key
	 * @return byte array representation for key
	 */
	byte[] translate(T key);
}
