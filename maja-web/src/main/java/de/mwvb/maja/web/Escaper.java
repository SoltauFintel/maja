package de.mwvb.maja.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.commons.lang.StringEscapeUtils;
import org.pmw.tinylog.Logger;

public class Escaper {

    /** Escape HTML */
    public String esc(String text) {
        return StringEscapeUtils.escapeHtml(text);
    }

    public static String toPrettyURL(String string) {
        // https://stackoverflow.com/a/4581526/3478021
        return Normalizer.normalize(string.toLowerCase(), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
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
