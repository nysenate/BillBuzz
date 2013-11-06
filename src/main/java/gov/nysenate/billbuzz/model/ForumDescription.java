package gov.nysenate.billbuzz.model;

import gov.nysenate.billbuzz.disqus.models.BaseObject;

import java.util.List;

public class ForumDescription extends BaseObject {
	private String created_at;
	private String shortname;
	private String description;

	private String name;
	private List<ThreadDescription> _threads;

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
