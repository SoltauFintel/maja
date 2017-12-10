package de.mwvb.maja.web;

import org.pmw.tinylog.Logger;

public class GuruErrorPage extends ActionBase implements ErrorPage {
	private Exception exception;
	
	@Override
	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String run() {
		Logger.error(exception);
		res.status(500);
		return "<!doctype html>\n" + 
			"<html lang=\"de\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
			"<head>\n" +
			"<meta charset=\"windows-1252\"/>\n" + 
			"<title>ERROR</title>\n" +
			"<style>\n" +
			"body {\n" +
			"	border-style: solid;\n" + 
			"	border-width: 4pt;\n" +
			"	border-color: red;\n" +
			"	margin: 20px 20px 20px 20px;\n" + 
			"	padding: 0.1cm 1cm 0.5cm 1cm;\n" +
			"	background-color: black;\n" +
			"	color: red;\n" +
			"	text-align: center;\n" + 
			"	font-family: Courier New;\n" + 
			"}\n" +
			"</style>\n" + 
			"</head>\n" +
			"<body>\n" +
			"	<h1>ERROR</h1>\n" + 
			"	<p>" + new Escaper().esc(exception.getMessage()) + "</p>\n" +
			"</body>\n" +
			"</html>";
	}
}
