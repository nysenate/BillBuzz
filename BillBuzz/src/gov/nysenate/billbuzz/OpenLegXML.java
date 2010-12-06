package gov.nysenate.billbuzz;

import gov.nysenate.billbuzz.model.BillInfo;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * @author jaredwilliams
 * THis class connects to OpenLeg and generates BillInfo object representing a particular
 * bill.  The only information it does not contain is the bill contents.
 *
 */
public class OpenLegXML
{
	//base url for xml interface
	private static String _openLegURL = "http://open.nysenate.gov/legislation/api/1.0/XML/bill/";
	
	public OpenLegXML() {
		
	}
	
	/**
	 * @param url is a url url from OpenLeg
	 * @returns the billid from the url
	 */
	public static String getBillFromURL(String url) {
		return url.split("/bill/")[1];
	}
	
	
	/**
	 * This function calls the XML parser
	 * @param url is a url from OpenLeg
	 * @return the BillInfo object representing the information
	 */
	public BillInfo getBillByURL(String url)
	{
		try {
			OpenLegHelper olh = new OpenLegHelper();
			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(olh);
			parser.parse(_openLegURL + getBillFromURL(url));
			return olh.getBill();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * This class handles the parsing of the object
	 * @author jaredwilliams
	 *
	 */
	class OpenLegHelper extends DefaultHandler
	{
		BillInfo b;
		boolean cosp = false;
		boolean summary = false;
		boolean committee = false;
		public OpenLegHelper() {
			b = new BillInfo();
		}

		public void startElement(String nsURI, String strippedName,
						String tagName, Attributes attributes) throws SAXException
		{
			if (tagName.equalsIgnoreCase("bill")) {
				b.setYear(attributes.getValue("year"));
				b.setSenateId(attributes.getValue("senateId"));
				b.setBillId(attributes.getValue("billId"));
				b.setTitle(attributes.getValue("title"));
				b.setLawSection(attributes.getValue("lawSection"));
				b.setSponsor(attributes.getValue("sponsor"));
				b.setAssemblySameAs(attributes.getValue("assemblySameAs"));
				b.setSameAs(attributes.getValue("sameAs"));
			}
			if(tagName.equalsIgnoreCase("cosponsor")) {
				cosp = true;
			}
			if(tagName.equalsIgnoreCase("summary")) {
				summary = true;
			}
			if(tagName.equalsIgnoreCase("committee")) {
				committee = true;
			}
		}

		public void characters(char[] ch, int start, int length)
		{
			String s = new String(ch, start, length);
			if(cosp) {
				cosp = false;
				b.addCosponsor(s);
			}
			else if(summary) {
				summary = false;
				b.setSummary(s);
			}
			else if(committee) {
				committee = false;
				b.setCommittee(s);
			}
		}
		
		public BillInfo getBill() {
			return b;
		}
	}


}