package de.mwvb.maja.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.pmw.tinylog.Logger;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import spark.Spark;

/**
 * The page of this action has the file name "src/main/resources/templates/{this
 * class name}.html". Use put() to add data within execute().
 */
public abstract class Action extends ActionBase {
    public static CompiledTemplates templates;
    protected final DataMap model = new DataMap();

    public void put(String name, String text) {
        model.put(name, text);
    }

    public void put(String name, boolean condition) {
        model.put(name, condition);
    }

    public DataList list(String name) {
        return model.list(name);
    }

    @Override
    protected String render() {
        return templates.render(getPage(), model);
    }

    public String getPage() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public static void get(String path, Class<? extends ActionBase> actionClass) {
        Spark.get(path, (req, res) -> {
            ActionBase action = actionClass.newInstance();
            action.init(req, res);
            return action.run();
        });
    }

    public static void get(String path, ActionBase action) {
        Spark.get(path, (req, res) -> {
            action.init(req, res);
            return action.run();
        });
    }

    public static String urlEncode(String text, String fallback) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error(e);
            return fallback;
        }
    }
}
