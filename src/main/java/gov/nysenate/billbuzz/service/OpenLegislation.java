package gov.nysenate.billbuzz.service;

import gov.nysenate.billbuzz.model.BillInfo;
import gov.nysenate.billbuzz.model.persist.Senator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


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
            JsonElement senatorsJSON = new JsonParser().parse(results);

            List<Senator> senators = new ArrayList<Senator>();
            for (JsonElement senatorElement : senatorsJSON.getAsJsonArray()) {
                Senator senator = new Senator();
                JsonObject senatorJSON = senatorElement.getAsJsonObject();
                senator.setOpenLegName(senatorJSON.get("shortName").getAsString());
                senator.setUrl(senatorJSON.get("url").getAsString());
                senator.setName(senatorJSON.get("name").getAsString());
                for (JsonElement partyElement : senatorJSON.get("partyAffiliations").getAsJsonArray()) {
                    if (!senator.setParty(partyElement.getAsString())) {
                        logger.warn("Invalid party "+partyElement.getAsString()+" for "+senator.getName());
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
            JsonArray billArray = (JsonArray)new JsonParser().parse(results);
            Iterator<JsonElement> itr = billArray.iterator();
            Gson gson = new Gson();
            if(itr.hasNext()) {
                return gson.fromJson(itr.next(), BillInfo.class);
            }
        }
        catch (IOException e) {
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