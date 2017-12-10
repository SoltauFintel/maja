package de.mwvb.maja.auth.facebook;

import com.github.scribejava.core.oauth.OAuth20Service;

import de.mwvb.maja.auth.AuthPlugin;

/**
 * Facebook login handle - and storage for those objects
 */
public class FacebookHandle {
	private final OAuth20Service oauth;
	private final String facebookUrl;
	private final AuthPlugin authPlugin;
	private final boolean rememberMeWanted;
	
	public FacebookHandle(OAuth20Service oauth, String facebookUrl, AuthPlugin authPlugin, boolean rememberMeWanted) {
		this.oauth = oauth;
		this.facebookUrl = facebookUrl;
		this.authPlugin = authPlugin;
		this.rememberMeWanted = rememberMeWanted;
	}
	
	public OAuth20Service getOauth() {
		return oauth;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}
	
	public String getUrl() {
		return getFacebookUrl();
	}

	public AuthPlugin getAuthPlugin() {
		return authPlugin;
	}

	public boolean isRememberMeWanted() {
		return rememberMeWanted;
	}
}
