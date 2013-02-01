package gov.nysenate.billbuzz.persist;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class Unmoderated {

	@Persistent(defaultFetchGroup="true")
	List<String> unmoderated;
	
	public Unmoderated() {
		unmoderated = new ArrayList<String>();
	}
	
	public Unmoderated(List<String> unmoderated) {
		this.unmoderated = unmoderated;
	}
	
	public List<String> getUnmoderated() {
		return unmoderated;
	}
	
	public void setUnmoderated(List<String> unmoderated) {
		this.unmoderated = unmoderated;
	}	
	
}
