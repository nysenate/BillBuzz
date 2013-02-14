package gov.nysenate.billbuzz.servlet;

import gov.nysenate.billbuzz.Controller;
import gov.nysenate.billbuzz.model.persist.Senator;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BBFilter implements Filter
{
    private List<Senator> senators;

    public void init(FilterConfig config) throws ServletException
    {
        senators = Controller.getSenators();
        if (senators == null) {
            throw new ServletException("Could not load senators");
        }
        for (Senator senator : senators) {
            String fmt = "%s (%s) - %s";
            System.out.println(String.format(fmt,senator.getName(),senator.getOpenLegName(), senator.getParty()));
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException
    {
        request.setAttribute("senators", senators);
        chain.doFilter(request, response);
    }

    public void destroy() {}
}
