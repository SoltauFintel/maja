package de.mwvb.maja.redis;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class CacheTest {
	/** Redis host name (e.g. "localhost") or IP address */
	private static final String host = GetCacheHost.HOST; // GetCacheHost is not in Git repo.
	/** Redis server port */
	private static final int port = GetCacheHost.PORT;

	@Test
	public void put_get_remove() {
		Cache cache = new Cache(host, port);
		cache.put("TEST-maja-1", "abcd");
		
		Assert.assertEquals("abcd", cache.get("TEST-maja-1"));
		
		cache.remove("TEST-maja-1");
		
		Assert.assertNull(cache.get("TEST-maja-1"));
		Assert.assertNull(cache.get("TEST-maja-1-nicht da"));
	}
	
	@Test
	public void tk_put_get_remove() {
		CacheTK<LocalDate, String> cache = new CacheTK<>(host, port, key -> key.toString().getBytes());
		LocalDate key = LocalDate.now();
		cache.put(key, "abce");
		
		Assert.assertEquals("abce", cache.get(key));
		
		cache.remove(key);
		
		Assert.assertNull(cache.get(key));
		Assert.assertNull(cache.get(LocalDate.now()));
	}
}
