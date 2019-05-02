package de.mwvb.maja.rest;

import java.lang.reflect.Method;

import org.pmw.tinylog.Logger;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;

public class RestRoute<T> implements Route {
	private final Class<? extends AbstractRestService<T>> restServiceClass;
	private final Method _method;

	public RestRoute(Class<? extends AbstractRestService<T>> restServiceClass, Method method) {
		this.restServiceClass = restServiceClass;
		this._method = method;
	}
	
	@Override
	public Object handle(Request req, Response res) throws Exception {
		info(req);
		AbstractRestService<T> restService = create();
		init(req, res, restService);
		Object response = callMethod(restService, req);
		return render(response, res);
	}

	protected void info(Request req) {
		Logger.info(Strings.padEnd(getRestServiceClassName() + " " + getMethodName() + "()", 30, ' ') + " | " + req.pathInfo());
	}
	
	protected String getRestServiceClassName() {
		return restServiceClass.getSimpleName();
	}
	
	protected String getMethodName() {
		return _method.getName();
	}
	
	protected AbstractRestService<T> create() throws Exception {
		return restServiceClass.newInstance();
	}
	
	protected void init(Request req, Response res, AbstractRestService<?> rest) {
		rest.init(req, res);
	}
	
	protected Object callMethod(AbstractRestService<T> restService, Request req) throws Exception {
		return _method.invoke(restService);
	}
	
	/** invoke with 1 arg of type T */
	protected Object callMethod1T(AbstractRestService<T> restService, Request req) throws Exception {
		T t = new Gson().fromJson(req.body(), restService.getEntityClass());
		return _method.invoke(restService, t);
	}

	protected String render(Object response, Response res) {
		if (response instanceof String) {
			return (String) response;
		}
		res.type("application/json");
		return new Gson().toJson(response);
	}
}
