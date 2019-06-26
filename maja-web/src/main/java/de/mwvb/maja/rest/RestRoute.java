package de.mwvb.maja.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.pmw.tinylog.Logger;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;

public class RestRoute<T> implements Route {
	private final Class<? extends AbstractNoEntityRestService> restServiceClass;
	private final Method _method;

	public RestRoute(Class<? extends AbstractNoEntityRestService> restServiceClass, Method method) {
		this.restServiceClass = restServiceClass;
		this._method = method;
	}
	
	@Override
	public Object handle(Request req, Response res) throws Exception {
		info(req);
		AbstractNoEntityRestService restService = create();
		init(req, res, restService);
		Object response;
		try {
			response = callMethod(restService, req);
		} catch (InvocationTargetException e) {
			Logger.error(e.getCause(), _method.getName()); // The detail error message should also be within the REST service.
			res.status(500);
			response = new ErrorMessage(e.getCause()); // The caller needs the error message.
		} catch (Exception e) {
			Logger.error(e, _method.getName()); // The detail error message should also be within the REST service.
			res.status(500);
			response = new ErrorMessage(e); // The caller needs the error message.
		}
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
	
	protected AbstractNoEntityRestService create() throws Exception {
		return restServiceClass.newInstance();
	}
	
	protected void init(Request req, Response res, AbstractNoEntityRestService rest) {
		rest.init(req, res);
	}
	
	protected Object callMethod(AbstractNoEntityRestService restService, Request req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return _method.invoke(restService);
	}
	
	/** invoke with 1 arg of type T */ 
	protected Object callMethod1T(AbstractRestService<T> restService, Request req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		T t = new Gson().fromJson(req.body(), restService.getEntityClass());
		return _method.invoke(restService, t);
	}

	protected String render(Object response, Response res) {
	    if (response == null) {
	        return "";
	    } else if (response instanceof String) {
			return (String) response;
		} 
		res.type("application/json");
		return new Gson().toJson(response);
	}
}
