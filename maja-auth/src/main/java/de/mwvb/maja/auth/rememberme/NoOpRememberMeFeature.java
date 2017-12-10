package de.mwvb.maja.auth.rememberme;

import spark.Request;
import spark.Response;

public class NoOpRememberMeFeature implements RememberMeFeature {

	@Override
	public void rememberMe(boolean rememberMeWanted, Response res, String user, String userId) {
	}
	
	@Override
	public void forget(Response res, String userId) {
	}

	@Override
	public IKnownUser getUserIfKnown(Request req, Response res) {
		return null; // important: must return null!
	}
}
