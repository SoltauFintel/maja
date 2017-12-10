package de.mwvb.maja.web;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

public class Template {

	public static String render(ModelAndView model) {
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName()); // disable velocity.log
        VelocityEngine velocityEngine = new org.apache.velocity.app.VelocityEngine(properties);
		return new VelocityTemplateEngine(velocityEngine).render(model);
	}
}
