package gov.nysenate.billbuzz.model;



import java.util.ArrayList;
import java.util.List;
public class BBSenator {
	private String name;
	private String email;
	private List<ThreadDescription> threads;
	public BBSenator() { };
	public BBSenator(String name, String email, List<ThreadDescription> threads) {
		this.name = name;
		this.email = email;
		this.threads = threads;
	}
	public BBSenator(String name, String email) {
		this.name = name;
		this.email = email;
		this.threads = new ArrayList<ThreadDescription>();
	}
	public BBSenator(String name) {
		this.name = name;
		this.email = "";
		this.threads = new ArrayList<ThreadDescription>();
	}
	public BBSenator(gov.nysenate.billbuzz.model.persist.Senator ps) {
		name = ps.getName();
		email = "";
		this.threads = new ArrayList<ThreadDescription>();
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setThreads(List<ThreadDescription> threads) {
		this.threads = threads;
	}
	public String getName() {
		return name;
	}

	public List<ThreadDescription> getThreads() {
		return threads;
	}
	public void addThread(ThreadDescription td) {
		threads.add(td);
	}
}
