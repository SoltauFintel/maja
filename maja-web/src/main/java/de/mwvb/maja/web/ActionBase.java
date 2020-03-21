package de.mwvb.maja.web;

import spark.Request;
import spark.Response;

/**
 * Pageless Action
 * 
 * You can access Request (req) and Response (res) within run(). Prefer the
 * Action class if the action has got a page. Use this class for pageless
 * actions, e.g. save actions that redirect to another page.
 */
public abstract class ActionBase {
    public static Escaper escaper = new Escaper();
    protected Request req;
    protected Response res;

    public void init(Request req, Response res) {
        this.req = req;
        this.res = res;
    }

    public String run() {
        execute();
        return render();
    }

    protected abstract void execute();

    protected String render() {
        return "";
    }

    protected String esc(String text) {
        if (text == null) {
            return "";
        }
        return escaper.esc(text);
    }
}
