package de.mwvb.maja.rest;

import spark.Request;
import spark.Response;

/**
 * Each public web method must have a Path annotation. Specify the HTTP verb if
 * it's not GET.
 */
public abstract class AbstractNoEntityRestService {
    protected Request req;
    protected Response res;

    public void init(Request req, Response res) {
        this.req = req;
        this.res = res;
    }
}
