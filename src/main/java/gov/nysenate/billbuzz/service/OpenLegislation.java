package gov.nysenate.billbuzz.service;

import gov.nysenate.billbuzz.model.openleg.BillInfo;
import gov.nysenate.billbuzz.model.persist.Senator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenLegislation
{
    private static final Logger logger = Logger.getLogger(OpenLegislation.class);
	private static String openLegURL = "http://open.nysenate.gov/legislation/api/1.0/json/bill/";
	private static Pattern billPattern = Pattern.compile("/bill/([A-Z][0-9]{1,5}(-[0-9]{4})?)");

	/**
	 * This function returns the associated bill information from OpenLeg
	 * @param billURL is the OpenLeg url of a bill
	 * @returns bill information in BillInfo object
	 * @throws Exception if there is a connection error
	 */

	public static void main(String[] args) {
	    System.out.println(billPattern.matcher("http://open.nysenate.gov/legislation/bill/S7801-2011").find());
	    System.exit(0);

	    System.out.println("Starting");
	    List<Senator> senators = OpenLegislation.getSenators(2013);
	    for (Senator senator : senators) {
	        String fmt = "%s (%s) - %s";
	        System.out.println(String.format(fmt,senator.getName(),senator.getOpenLegName(), senator.getParty()));
	    }
	    PMF.persistObject(senators.toArray());
	    System.out.println("Done");
	}

	public static List<Senator> getSenators(int year) {
	    try {
    	    String url = String.format("http://open.nysenate.gov/legislation/senators/%d.json",  year);
    	    String results = Request.Get(url).execute().returnContent().asString();
            JsonNode senatorsJSON = new ObjectMapper().readTree(results);
            List<Senator> senators = new ArrayList<Senator>();
            for (int i=0; i<senatorsJSON.size(); i++) {
                Senator senator = new Senator();
                JsonNode senatorJSON = senatorsJSON.get(i);
                senator.setOpenLegName(senatorJSON.get("shortName").asText());
                senator.setUrl(senatorJSON.get("url").asText());
                senator.setName(senatorJSON.get("name").asText());
                JsonNode affiliationsJSON = senatorJSON.get("partyAffiliations");
                for (int j=0; j<affiliationsJSON.size(); j++) {
                    JsonNode affiliationJSON = affiliationsJSON.get(j);
                    if (!senator.setParty(affiliationJSON.asText())) {
                        logger.warn("Invalid party "+affiliationJSON.asText()+" for "+senator.getName());
                    }
                }
                senators.add(senator);
            }
            return senators;
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	        logger.error("Unable to fetch senators for year "+year, e);
	        return null;
	    }
	}

	public static BillInfo getBillInfo(String bill) {
       try {
            String results = Request.Get(openLegURL + bill).execute().returnContent().asString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode resultsJSON = mapper.readTree(results);
            if (resultsJSON.size() != 0) {
                JsonNode billJSON = resultsJSON.get(0);
                return mapper.treeToValue(billJSON, BillInfo.class);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error("Unable to get bill info for "+bill, e);
        }
       return null;
	}

	public static BillInfo getBillInfoFromURL(String billURL)
	{
	    return getBillInfo(getBillFromURL(billURL));
	}

	public static String getBillFromURL(String url)
	{
	    Matcher urlMatcher = billPattern.matcher(url);
	    if (urlMatcher.find()) {
	        String bill = urlMatcher.group(1);
	        if(bill.indexOf("-") == -1) {
	            bill += "-2009";
	        }
	        return bill;
	    }
	    else {
	        return null;
	    }
	}
}