package gov.nysenate.billbuzz.model;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Senator {
	String name;
	String url;
	String openLegName;

	boolean democrat;
	boolean republican;
	boolean conservative;
	boolean workingFamilies; //WF
	//boolean independentParty; //I
	boolean independenceParty; //IP
	//boolean independent; //ind

	public Senator() {
		democrat = false;
		republican = false;
		conservative = false;
		workingFamilies = false;
		independenceParty = false;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getOpenLegName() {
		return openLegName;
	}

	public boolean isDemocrat() {
		return democrat;
	}

	public boolean isRepublican() {
		return republican;
	}

	public boolean isConservative() {
		return conservative;
	}

	public boolean isWorkingFamilies() {
		return workingFamilies;
	}

	public boolean isIndependenceParty() {
		return independenceParty;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setOpenLegName(String openLegName) {
		this.openLegName = openLegName;
	}

	public void setDemocrat(boolean democrat) {
		this.democrat = democrat;
	}

	public void setRepublican(boolean republican) {
		this.republican = republican;
	}

	public void setConservative(boolean conservative) {
		this.conservative = conservative;
	}

	public void setWorkingFamilies(boolean workingFamilies) {
		this.workingFamilies = workingFamilies;
	}

	public void setIndependenceParty(boolean independanceParty) {
		this.independenceParty = independanceParty;
	}

	public String getParty() {

		String ret = "";
		if(democrat) {
			ret = "d";
		}
		else if(republican) {
			ret = "r";
		}
		if(independenceParty) {
			ret = getPartyHelper(ret,"ip");
		}
		if(conservative) {
			ret = getPartyHelper(ret,"c");
		}
		if(workingFamilies) {
			ret = getPartyHelper(ret,"wf");
		}

		return ret;
	}

	public String getPartyHelper(String parties, String party) {
		if(parties.equals("")) {
			return party;
		}
		return parties + " - " + party;
	}


}
