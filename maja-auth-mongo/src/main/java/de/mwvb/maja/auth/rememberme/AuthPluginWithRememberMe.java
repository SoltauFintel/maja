package de.mwvb.maja.auth.rememberme;

import de.mwvb.maja.auth.AuthPlugin;

public class AuthPluginWithRememberMe extends AuthPlugin {

	public AuthPluginWithRememberMe() {
		super(new RememberMeInMongoDB());
	}
}
