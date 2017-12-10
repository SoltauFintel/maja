package de.mwvb.maja.auth;

import org.eclipse.jetty.http.HttpStatus;
import org.pmw.tinylog.Logger;

import spark.Request;
import spark.Response;

public class OneUserAuthorization implements Authorization {
	private final String userId;
	private final String service;
	
	public OneUserAuthorization(String userId, String service) {
		this.userId = userId;
		this.service = service;
	}

	@Override
	public boolean isRelevant(String service) {
		return this.service.equalsIgnoreCase(service);
	}
	
	@Override
	public String check(Request req, Response res, String name, String foreignId, String service) {
		if (this.service.equalsIgnoreCase(service) && userId.equals(foreignId)) {
			return null;
		}
		return notAuthorized(res, name, foreignId, service);
	}
	
	public static String notAuthorized(Response res, String name, String foreignId, String service) {
		Logger.error("Not authorized: " + name + " (" + foreignId + "@" + service + ")");
		res.status(HttpStatus.FORBIDDEN_403);
		throw new RuntimeException("You are not authorized to use this web application.");
	}
}
