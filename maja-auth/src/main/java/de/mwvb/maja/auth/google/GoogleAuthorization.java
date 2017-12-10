package de.mwvb.maja.auth.google;

import de.mwvb.maja.auth.BaseAuthorization;

public class GoogleAuthorization extends BaseAuthorization {

	@Override
	protected String getService() {
		return "Google";
	}
}
