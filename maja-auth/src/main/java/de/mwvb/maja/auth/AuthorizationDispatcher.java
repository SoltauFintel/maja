package de.mwvb.maja.auth;

import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

import spark.Request;
import spark.Response;

public class AuthorizationDispatcher implements Authorization {
	private final List<Authorization> authList = new ArrayList<>();
	
	public AuthorizationDispatcher(Authorization ... theAuthList) {
		for (Authorization auth : theAuthList) {
			authList.add(auth);
		}
	}

	@Override
	public boolean isRelevant(String service) {
		return true; // always! ;-)
	}

	@Override
	public String check(Request req, Response res, String name, String foreignId, String service) {
		for (Authorization auth : authList) {
			if (auth.isRelevant(service)) {
				return auth.check(req, res, name, foreignId, service);
			}
		}
		Logger.error("Unknown service: " + service);
		return "Unknown service!";
	}
}
