package de.mwvb.maja.auth;

import spark.Request;
import spark.Response;

public abstract class BaseAuthorization implements Authorization {

	protected abstract String getService();
	
	@Override
	public boolean isRelevant(String service) {
		return getService().equalsIgnoreCase(service);
	}

	@Override
	public String check(Request req, Response res, String name, String foreignId, String service) {
		if (getService().equalsIgnoreCase(service)) {
			return null;
		}
		return OneUserAuthorization.notAuthorized(res, name, foreignId, service);
	}
}
