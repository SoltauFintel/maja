package de.mwvb.maja.web;

import com.google.gson.Gson;

public abstract class JsonAction extends ActionBase {
	protected Object result;
	
	@Override
	public String run() {
		res.type("application/json");
		execute();
		return render();
	}
	
	@Override
	protected String render() {
		return new Gson().toJson(result);
	}
}
