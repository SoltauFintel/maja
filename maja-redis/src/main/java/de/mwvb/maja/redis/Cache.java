package de.mwvb.maja.redis;

/**
 * Cache with String keys and String values. Expiration: 10 days.
 */
public class Cache extends CacheTK<String, String> {

    public Cache(String host, int port) {
        super(host, port, key -> key.getBytes());
    }
}
