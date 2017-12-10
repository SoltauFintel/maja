package de.mwvb.maja.web;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.commons.lang.StringEscapeUtils;

public class Escaper {

	/** Escape HTML */
	public String esc(String text) {
		return StringEscapeUtils.escapeHtml(text);
	}
	
	public static String toPrettyURL(String string) {
		// https://stackoverflow.com/a/4581526/3478021
	    return Normalizer.normalize(string.toLowerCase(), Form.NFD)
	        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	        .replaceAll("[^\\p{Alnum}]+", "-");
	}
}
