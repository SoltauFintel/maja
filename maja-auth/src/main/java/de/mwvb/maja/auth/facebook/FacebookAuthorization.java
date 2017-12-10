package de.mwvb.maja.auth.facebook;

import de.mwvb.maja.auth.BaseAuthorization;

/**
 * Any Facebook user can log-in and use this web application.
 */
public class FacebookAuthorization extends BaseAuthorization {

	@Override
	protected String getService() {
		return "Facebook";
	}
}
