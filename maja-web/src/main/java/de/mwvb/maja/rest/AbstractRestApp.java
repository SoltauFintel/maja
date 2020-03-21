package de.mwvb.maja.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

import de.mwvb.maja.httpverb.DELETE;
import de.mwvb.maja.httpverb.GET;
import de.mwvb.maja.httpverb.PATCH;
import de.mwvb.maja.httpverb.POST;
import de.mwvb.maja.httpverb.PUT;
import de.mwvb.maja.web.AbstractWebApp;
import spark.Request;
import spark.Route;
import spark.Spark;

public abstract class AbstractRestApp extends AbstractWebApp {
    // TODO Vermutlich muss man mehr Verantwortung an AbstractRestService geben.
    // AbstractRestService könnte einen Standard-Builder liefern, der
    // unten stehenden Code enthält.
    // TODO Doku Erzeugungs Modus (Swagger)

    protected <T> void addRestService(Class<? extends AbstractNoEntityRestService> restServiceClass) {
        RestService classpath = restServiceClass.getAnnotation(RestService.class);
        if (classpath == null) {
            throw new RuntimeException("REST service class " + restServiceClass.getName() + " must be annotated with @"
                    + RestService.class.getSimpleName());
        }
        int n = 0;
        for (Method method : restServiceClass.getMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                if (method.isAnnotationPresent(GET.class)) {
                    GET path = method.getAnnotation(GET.class);
                    Spark.get(classpath.value() + path.value(), createRoute(restServiceClass, method));
                    n++;
                } else if (method.isAnnotationPresent(POST.class)) {
                    POST path = method.getAnnotation(POST.class);
                    Spark.post(classpath.value() + path.value(), createRoute(restServiceClass, method));
                    n++;
                } else if (method.isAnnotationPresent(PUT.class)) {
                    PUT path = method.getAnnotation(PUT.class);
                    Spark.put(classpath.value() + path.value(), createRoute(restServiceClass, method));
                    n++;
                } else if (method.isAnnotationPresent(DELETE.class)) {
                    DELETE path = method.getAnnotation(DELETE.class);
                    Spark.delete(classpath.value() + path.value(), createRoute(restServiceClass, method));
                    n++;
                } else if (method.isAnnotationPresent(PATCH.class)) {
                    PATCH path = method.getAnnotation(PATCH.class);
                    Spark.patch(classpath.value() + path.value(), createRoute(restServiceClass, method));
                    n++;
                }
            }
        }
        if (n == 0) {
            throw new RuntimeException("REST service class " + restServiceClass.getName() + " has no @"
                    + Path.class.getSimpleName() + " annotated methods");
        }
    }

    protected <T> Route createRoute(Class<? extends AbstractNoEntityRestService> restServiceClass, Method method) {
        RestRoute<T> route;
        int args = method.getParameterCount();
        switch (args) {
        case 0:
            route = new RestRoute<T>(restServiceClass, method);
            break;
        case 1: // typically methods for verbs PUT or POST
            route = new RestRoute<T>(restServiceClass, method) {
                @SuppressWarnings("unchecked")
                @Override
                protected Object callMethod(AbstractNoEntityRestService restService, Request req)
                        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    if (restService instanceof AbstractRestService) {
                        return callMethod1T((AbstractRestService<T>) restService, req);
                    } else {
                        return super.callMethod(restService, req);
                    }
                }
            };
            break;
        default: // >1
            throw new RuntimeException(
                    "Unsupported number of method parameters: " + method.getName() + "(" + args + ")");
        }
        return route;
    }
}
