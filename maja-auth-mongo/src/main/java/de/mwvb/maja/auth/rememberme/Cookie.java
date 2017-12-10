package de.mwvb.maja.auth.rememberme;

import org.pmw.tinylog.Logger;

import spark.Request;
import spark.Response;

public class Cookie {
	private final String name;
	
	public Cookie(String name) {
		this.name = name;
	}
	
	public String get(Request req) {
		String ret = req.cookie(name);
		Logger.trace("get Cookie " + name + ": " + ret);
		return ret;
	}
	
	public void set(String value, Response res, String action) {
		res.cookie("", "/", name, value, 60 * 60 * 24 * 30 /* 30 days */, false, false);
		Logger.trace("Cookie " + name + " set [" + action + "]: " + value);
	}
	
	public void extendLifeTime(String value, Response res) {
		set(value, res, "extends-cookie-life-time");
	}
	
	public void remove(Response res) {
		res.removeCookie(name);
		Logger.trace("Cookie removed: " + name);
	}
}
