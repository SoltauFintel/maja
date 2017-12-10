package de.mwvb.maja.web;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Spark;

/**
 * The page of this action has the file name "src/main/resources/templates/{this class name}.html".
 * Use put() to add data within execute().
 */
public abstract class Action extends ActionBase {
	public static String folder = "templates/";
	public static String suffix = ".html";
	public static Escaper escaper = new Escaper();
	protected final Map<String, Object> model = new HashMap<>();
	
	public void put(String name, Object value) {
		model.put(name, value);
	}

	@Override
	public String run() {
		put("T", escaper);
		execute();
		return Template.render(new ModelAndView(model, getPage()));
	}
	
	protected abstract void execute();

	public String getPage() {
		return folder + getClass().getSimpleName().toLowerCase() + suffix;
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
}
