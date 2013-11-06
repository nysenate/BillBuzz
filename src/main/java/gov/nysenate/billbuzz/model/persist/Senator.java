package gov.nysenate.billbuzz.model.persist;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Senator implements Comparable<Senator> {
	String name;
	String url;
	String openLegName;

	boolean democrat;
	boolean republican;
	boolean conservative;
	boolean workingFamilies; //WF
	boolean independentParty; //I
	boolean independenceParty; //IP
	boolean independent; //ind

	public Senator() {
		democrat = false;
		republican = false;
		conservative = false;
		workingFamilies = false;
		independentParty = false;
		independenceParty = false;
		independent = false;
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

	public boolean isIndependentParty() {
		return independentParty;
	}

	public boolean isIndependenceParty() {
		return independenceParty;
	}

	public boolean isIndependent() {
		return independent;
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

	public void setIndependentParty(boolean independentParty) {
		this.independentParty = independentParty;
	}

	public void setIndependenceParty(boolean independanceParty) {
		this.independenceParty = independanceParty;
	}

	public void setIndependent(boolean independent) {
		this.independent = independent;
	}

	public boolean setParty(String party) {
	    if (party.equalsIgnoreCase("d")) {
	        democrat = true;
	    }
	    else if (party.equalsIgnoreCase("r")) {
	        republican = true;
	    }
	    else if (party.equalsIgnoreCase("i")) {
	        independentParty = true;
	    }
	    else if (party.equalsIgnoreCase("ip")) {
	        independenceParty = true;
	    }
	    else if (party.equalsIgnoreCase("c")) {
            conservative = true;
        }
	    else if (party.equalsIgnoreCase("ind")) {
            independent = true;
        }
	    else if (party.equalsIgnoreCase("wf")) {
            workingFamilies = true;
        }
	    else {
	        return false;
	    }
	    return true;
	}

	public String getParty() {

		String ret = "";
		if(democrat) {
			ret = "d";
		}
		else if(republican) {
			ret = "r";
		}
		if(independentParty) {
			ret = getPartyHelper(ret,"i");
		}
		if(independenceParty) {
			ret = getPartyHelper(ret,"ip");
		}
		if(independent) {
			ret = getPartyHelper(ret,"ind");
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

    @Override
    public int compareTo(Senator other)
    {
        return this.getUrl().compareTo(other.getUrl());
    }


}
