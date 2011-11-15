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

import bbsignup.model.OldSenator;
import bbsignup.model.Senator;
import bbsignup.src.PMF;

@SuppressWarnings("unchecked")
public class NYSenateServices {
	
	public static void index() throws Exception {		
		List<Senator> newSenList = getSenators();
		List<Senator> prevSenList = (List<Senator>) PMF.getDetachedObjects(Senator.class);
		List<OldSenator> oldSenList = (List<OldSenator>) PMF.getDetachedObjects(OldSenator.class);
		
		HashMap<String,Senator> newSenMap = getSenatorMap(newSenList);
		HashMap<String,Senator> prevSenMap = getSenatorMap(prevSenList);
		HashMap<String,OldSenator> oldSenMap = getOldSenatorMap(oldSenList);
		
		for(String name:prevSenMap.keySet()) {
			if(newSenMap.get(name) != null) {
				prevSenList.remove(prevSenMap.get(name));
			}
			else
			{
				if(oldSenMap.get(name) != null) {
					prevSenList.remove(prevSenMap.get(name));
				}
			}
		}
		
		for(Senator senator:prevSenList) {
			oldSenList.add(new OldSenator(senator));
		}
		
		PMF.deleteObjects(OldSenator.class);
		PMF.deleteObjects(Senator.class);
		
		for(Senator s:newSenList) {
			PMF.persistObject(s);
		}
		for(OldSenator os:oldSenList) {
			PMF.persistObject(os);
		}
				
		
	}
	
	public static HashMap<String,Senator> getSenatorMap(List<Senator> list) {
		HashMap<String,Senator> map = new HashMap<String,Senator>();
		for(Senator senator:list) {
			map.put(senator.getOpenLegName(), senator);
		}
		return map;
	}
	
	public static HashMap<String,OldSenator> getOldSenatorMap(List<OldSenator> list) {
		HashMap<String,OldSenator> map = new HashMap<String,OldSenator>();
		for(OldSenator senator:list) {
			map.put(senator.getOpenLegName(), senator);
		}
		return map;
	}
	
	public static void main(String[] args) throws Exception {
		index();
		
		/*if(args.length == 0) {
			System.out.println("please indicate either 'index' or 'truncate'");
		}
		else {
			if(args[0].equals("index")) {
				index();
			}
			else  if(args[0].equals("truncate")) {
				PMF.deleteObjects(Senator.class);
			}
			else {
				System.out.println("please indicate either 'index' or 'truncate'");
			}
		}*/
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
				else if(party.matches("IP")) {
					senator.setIndependenceParty(true);
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
		
		URL url = new URL("http://open.nysenate.gov/legislation/senators");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

		Pattern districtP = Pattern.compile("District (\\d+)");
		Pattern nameP = Pattern.compile("/sponsor/(.+?)\\?filter");
		
		Matcher districtM = null;
		Matcher nameM = null;
		
		String in = null;
				
		while((in = br.readLine()) != null) {
			String d = null, n = null;
			
			districtM = districtP.matcher(in);
			
			if(districtM.find()) {
				d = districtM.group(1);
				
				in = br.readLine();

				nameM = nameP.matcher(in);
				
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










