package de.mwvb.maja.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.pmw.tinylog.Logger;

import redis.clients.jedis.Jedis;

/**
 * @param <KEY_TYPE>   usually String, but could be also a more complex class type
 * @param <VALUE_TYPE> a class type, sometimes just String
 */
public class CacheTK<KEY_TYPE, VALUE_TYPE> {
    private final Jedis redis; // TODO auf JedisPool umstellen, vgl. tvprogramm.HtmlCache
    private final Translator<KEY_TYPE> translator;
    private final int expirationTime;

    /**
     * expiration time: 10 days
     */
    public CacheTK(String host, int port, Translator<KEY_TYPE> translator) {
        this(host, port, translator, 10);
    }

    /**
     * @param host           host name without protocol
     * @param port
     * @param translator     key translator
     * @param expirationTime in days
     */
    public CacheTK(String host, int port, Translator<KEY_TYPE> translator, int expirationTime) {
        this.redis = new Jedis(host, port);
        this.translator = translator;
        this.expirationTime = 60 * 60 * 24 * expirationTime; // days to seconds
    }

    protected byte[] translate(KEY_TYPE key) {
        return translator.translate(key);
    }

    protected int getExpirationTimeInSeconds() {
        return expirationTime;
    }

    public void put(KEY_TYPE key, VALUE_TYPE value) {
        try {
            byte[] tkey = translate(key);
            redis.set(tkey, serialize(value));
            redis.expire(tkey, getExpirationTimeInSeconds());
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public VALUE_TYPE get(KEY_TYPE key) {
        try {
            return deserialize(redis.get(translate(key)));
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }

    public void remove(KEY_TYPE key) {
        try {
            redis.del(translate(key));
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public void clearDatabase() {
        redis.flushDB();
    }

    public Long getDatabaseSize() {
        return redis.dbSize();
    }

    private byte[] serialize(VALUE_TYPE tp) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(tp);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            Logger.error(e);
            return null;
        } finally {
            try {
                bos.close();
            } catch (IOException ignore) { //
            }
        }
    }

    @SuppressWarnings("unchecked")
    private VALUE_TYPE deserialize(byte[] data) {
        if (data == null) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (VALUE_TYPE) in.readObject();
        } catch (Exception e) {
            Logger.error(e);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignore) { //
            }
        }
    }
}
