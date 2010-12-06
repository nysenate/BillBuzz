package gov.nysenate.billbuzz;

import gov.nysenate.billbuzz.model.BillInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
public class OpenLeg {
	private static String _openLegURL = "http://open.nysenate.gov/legislation/api/1.0/json/bill/";
	public static String commandResults(String command) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(command).openStream()));
		String line = reader.readLine();
		reader.close();
		return line;
	}
	/**
	 * This function returns the associated bill information from OpenLeg
	 * @param billURL is the OpenLeg url of a bill
	 * @returns bill information in BillInfo object
	 * @throws Exception if there is a connection error
	 */
	public static BillInfo getBill(String billURL) throws Exception{
		String results = commandResults(_openLegURL + getBillFromURL(billURL));		
		JsonParser parser = new JsonParser();
		JsonArray bill = (JsonArray)parser.parse(results);
		Iterator<JsonElement> itr = bill.iterator();
		Gson gson = new Gson();
		if(itr.hasNext()) {
			return gson.fromJson(itr.next(), BillInfo.class);
		}
		return null;
	}
	/**
	 * @param url is a url from OpenLeg
	 * @returns the billID from any OpenLeg url
	 */
	public static String getBillFromURL(String url) {
		return url.split("/bill/")[1];
	}
}