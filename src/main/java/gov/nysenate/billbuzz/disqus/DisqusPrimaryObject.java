package gov.nysenate.billbuzz.disqus;

public abstract class DisqusPrimaryObject {
	protected String id;

	public void setId(String id) {
	    this.id = id;
	}

	public String getId() {
		return id;
	}
}
