package gov.nysenate.billbuzz.disqus.models;

public abstract class BaseObject {
	protected String id;

	public void setId(String id) {
	    this.id = id;
	}

	public String getID() {
		return id;
	}
}
