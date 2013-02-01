package gov.nysenate.billbuzz.servlets;

import gov.nysenate.billbuzz.model.Senator;
import gov.nysenate.billbuzz.src.Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;

public class SenatorContext {

	public static synchronized List<Senator> getSenators(ServletContext context) throws IOException {
		@SuppressWarnings("unchecked")
		List<Senator> senators = (List<Senator>)context.getAttribute("senators");
		if(senators == null) {
			senators = new Controller().getSenators();
			context.setAttribute("senators", senators);
		}
		return senators;
	}

}
