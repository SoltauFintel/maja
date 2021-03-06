package de.mwvb.maja.web;

import java.util.Collection;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import spark.Spark;

/**
 * The page of this action has the file name "src/main/resources/templates/{this class name}.html". Use put() to add data within execute().
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
    
    public void putInt(String name, int number) {
        model.putInt(name, number);
    }
    
    public void putHas(String name, Object o) {
        model.putHas(name, o);
    }
    
    public void putSize(String name, Collection<?> collection) {
        model.putSize(name, collection);
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
        return Escaper.urlEncode(text, fallback);
    }
}
