package de.mwvb.maja.auth;

/**
 * Feature for AuthPlugin
 */
public interface AuthFeature {

	void init(AuthPlugin owner);
	
	void routes();
}
