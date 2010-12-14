package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import bbsignup.model.Senator;
import bbsignup.src.Controller;

public class SenatorContext {
	
	public static synchronized List<Senator> getSenators(ServletContext context) throws IOException {
		List<Senator> senators = (List<Senator>)context.getAttribute("senators");
		if(senators == null) {
			senators = new Controller().getSenators();
			context.setAttribute("senators", senators);
		}
		return senators;
	}
	
}
