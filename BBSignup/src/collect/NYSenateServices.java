package collect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbsignup.model.Senator;
import bbsignup.src.PMF;

@SuppressWarnings("unchecked")
public class NYSenateServices {
	
	public static void index() throws Exception {		
		List<Senator> senators = getSenators();
		
		for(Senator s:senators) {
			PMF.persistObject(s);
		}
	}
	
	public static void main(String[] args) throws Exception {
		index();
	}
	
	public static List<Senator> getSenators() throws Exception {		
		HashMap<String,String> olMap = getOpenLegSenators();
		
		List<Senator> ret = new ArrayList<Senator>();
		
		XmlRpc rpc = new XmlRpc();

		Object[] objects = (Object[])rpc.getView("senators", null, null,
				null, false, null);

		for(Object o:objects) {
			Senator senator = new Senator();

			/*Current map from Senator view */
			HashMap<String,Object> map = (HashMap<String,Object>)o;

			String sid = (String)map.get("nid");
			String did = (String)map.get("node_data_field_status_field_senators_district_nid"); 
			String name = (String)map.get("node_title");

			/*District Node*/
			HashMap<String,Object> disNode = (HashMap<String,Object>) rpc.getNode(new Integer(did));

			HashMap<String,Object> dNumberMap =	rpc.getMap(disNode.get("field_district_number"));
			String dNumber = "" + (String)dNumberMap.get("value");

			/*Senator Node*/
			HashMap<String,Object> senNode = (HashMap<String,Object>) rpc.getNode(new Integer(sid));

			String sPath = "http://www.nysenate.gov/" +	(String)senNode.get("path");
			
			Object[] parties = (Object[]) senNode.get("field_party_affiliation");
			
			for(Object p:parties) {
				HashMap<String,Object> pMap = (HashMap<String,Object>)p;
				String party = (String)pMap.get("value");
				
				if(party.matches("D")) {
					senator.setDemocrat(true);
				}
				else if(party.matches("R")) {
					senator.setRepublican(true);
				}
				else if(party.matches("I")) {
					senator.setIndependentParty(true);
				}
				else if(party.matches("IP")) {
					senator.setIndependenceParty(true);
				}
				else if(party.matches("Ind")) {
					senator.setIndependent(true);
				}
				else if(party.matches("WF")) {
					senator.setWorkingFamilies(true);
				}
				else if(party.matches("C")) {
					senator.setConservative(true);
				}
			}
			senator.setName(name);
			senator.setUrl(sPath);
			senator.setOpenLegName(olMap.get(dNumber));
						
			ret.add(senator);
			
			System.out.println(senator.getName() + ": " + senator.getParty());
		}
		
		return ret;
	}
	
	public static HashMap<String,String> getOpenLegSenators() throws IOException {
		HashMap<String,String> ret = new HashMap<String,String>();
		
		URL url = new URL("http://open.nysenate.gov/legislation/senators/");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		
		Pattern lineP = Pattern.compile("<div class=\"views-field-field-senators-district-nid\">");
		Pattern districtP = Pattern.compile("District (\\d+)");
		Pattern nameP = Pattern.compile("/legislation/sponsor/(\\w+)");
		
		Matcher lineM = null;
		Matcher districtM = null;
		Matcher nameM = null;
		
		String in = null;
				
		while((in = br.readLine()) != null) {
			String d = null, n = null;
			
			lineM = lineP.matcher(in);
			
			if(lineM.find()) {
				
				in = br.readLine();
				
				districtM = districtP.matcher(in);
				nameM = nameP.matcher(in);
				
				if(districtM.find()) {
					d = districtM.group(1);
					
					if(districtM.group(1).equals("6")) {
						n = "hannon";
					}
				}
				
				if(nameM.find()) {
					n = nameM.group(1);
				}
				
				ret.put(d,n);

			}
			
		}
		br.close();
		
		return ret;
	}
}










