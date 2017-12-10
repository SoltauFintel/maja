package de.mwvb.maja.web;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.common.base.Strings;

/**
 * Gives access to the application configuration
 * 
 * <p>Takes config filename from CONFIG env var. If it's not set "AppConfig.properties" is used.
 * If "AppConfig.properties" does not exist "/AppConfig.properties" will be used.</p>
 */
public class AppConfig {
	private final String configFile;
	private final Properties properties = new Properties();
	
	public AppConfig() {
		String dn = System.getenv("CONFIG");
		if (Strings.isNullOrEmpty(dn)) {
			dn = "AppConfig.properties";
		}
		try {
			properties.load(new FileReader(dn));
		} catch (IOException e1) {
			if (dn.startsWith("/")) {
				throw new RuntimeException(e1);
			}
			try {
				dn = "/" + dn;
				properties.load(new FileReader(dn));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		configFile = dn;
	}
	
	/**
	 * @param key
	 * @return null if property does not exist
	 */
	public String get(String key) {
		String ret = properties.getProperty(key);
		return ret == null ? null : ret.trim();
	}
	
	/**
	 * @param key
	 * @param pDefault
	 * @return pDefault if property does not exist
	 */
	public String get(String key, String pDefault) {
		String ret = properties.getProperty(key);
		return ret == null ? pDefault : ret.trim();
	}

	public String getFilename() {
		return configFile;
	}
	
	public boolean isDevelopment() {
		return "true".equals(get("development"));
	}
	
	/**
	 * @param key
	 * @return true if the key exists and has got a non-empty value
	 */
	public boolean hasFilledKey(String key) {
		String value = get(key);
		return value != null && !value.trim().isEmpty();
	}
}
