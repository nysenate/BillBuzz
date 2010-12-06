package gov.nysenate.billbuzz.model;


import java.util.List;
public class ForumDescription extends DisqusObject {
	private String created_at;
	private String shortname;
	private String description;
	private String id;
	private String name;
	private List<ThreadDescription> _threads;
	
	public ForumDescription() {
		super.id = id;
	}
	public String getCreatedAt() {
		return created_at;
	}
	public String getShortName() {
		return shortname;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}
	public List<ThreadDescription> getThreads() {
		return _threads;
	}
	public void setThreads(List<ThreadDescription> threads) {
		_threads = threads;
	}
}
