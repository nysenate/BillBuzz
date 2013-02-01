package gov.nysenate.billbuzz.persist;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class LastUse {

	
	private String lastUse;
	
	public LastUse() {
		
	}
	
	public LastUse(String lastUse) {
		this.lastUse = lastUse;
	}
	
	public String getLastUse() {
		return lastUse;
	}
	
	public void setLastUse(String lastUse) {
		this.lastUse = lastUse;
	}
	
	
}
