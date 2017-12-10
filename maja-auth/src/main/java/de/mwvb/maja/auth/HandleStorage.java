package de.mwvb.maja.auth;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Universal storage for auth handles, that cannot become too big.
 */
public class HandleStorage<HANDLE> {
	private final Cache<String, HANDLE> data = // thread-safe and cannot become too big 
			CacheBuilder.newBuilder().initialCapacity(20).maximumSize(1000)
				.expireAfterWrite(5, TimeUnit.MINUTES).build();

	public synchronized void push(String key, HANDLE handle) {
		data.put(key, handle);
	}
	
	public synchronized HANDLE pop(String key) {
		HANDLE ret = data.getIfPresent(key);
		if (ret != null) {
			data.invalidate(key);
		}
		return ret;
	}
}
