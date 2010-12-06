package gov.nysenate.billbuzz.persist;

import java.io.Serializable;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Senator implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Persistent
	@PrimaryKey
	@Column(name="name")
	private String name;
	
	
	@Persistent
	@Column(name="url")
	private String url;
	
	@Persistent
	@Column(name="party")
	private String party;
	
	
	
	
	
	public Senator() {
		
	}
	
	public Senator(String name, String url, String party) {
		this.name = name;
		this.url = url;
		this.party = party;
		
	}
	
	
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public String getParty() {
		return party;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setParty(String party) {
		this.party = party;
	}
	
}
