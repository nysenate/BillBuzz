package gov.nysenate.billbuzz.model.openleg;

import java.util.ArrayList;

public class BillInfo {
	private String year;
	private String senateId;
	private String billId;
	private String title;
	private String lawSection;
	private String sponsor;
	private String assemblySameAs;
	private String sameAs;
	private ArrayList<Vote> votes;
	private ArrayList<Action> actions;
	private ArrayList<Cosponsor> cosponsors;

	private String summary;

	private String committee;

	public BillInfo() {
		this.cosponsors = new ArrayList<Cosponsor>();
		this.actions = new ArrayList<Action>();
		this.votes = new ArrayList<Vote>();
	}

	public BillInfo(String senateId, String sponsor, String title, String summary) {
	    super();

		this.senateId = senateId;
		this.sponsor = sponsor;
		this.title = title;
		this.summary = summary;
	}

	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = (year == null) ? "":year;
	}
	public String getSenateId() {
		return senateId;
	}
	public void setSenateId(String senateId) {
		this.senateId = (senateId == null) ? "":senateId;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = (billId ==null) ? "":billId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = (title == null) ? "":title;
	}
	public String getLawSection() {
		return lawSection;
	}
	public void setLawSection(String lawSection) {
		this.lawSection = (lawSection == null) ? "":lawSection;
	}
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = (sponsor == null) ? "":sponsor;
	}
	public String getAssemblySameAs() {
		return assemblySameAs;
	}
	public void setAssemblySameAs(String assemblySameAs) {
		this.assemblySameAs = (assemblySameAs == null) ? "":assemblySameAs;
	}
	public String getSameAs() {
		return sameAs;
	}
	public void setSameAs(String sameAs) {
		this.sameAs = (sameAs == null) ? "":sameAs;
	}
	public ArrayList<Cosponsor> getCosponsors() {
		return cosponsors;
	}
	public void setCosponsors(ArrayList<Cosponsor> cosponsors) {
		this.cosponsors = cosponsors;
	}
	public void addCosponsor(String s) {
		cosponsors.add(new Cosponsor(s));
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = (summary == null) ? "":summary;
	}
	public String getCommittee() {
		return committee;
	}
	public void setCommittee(String committee) {
		this.committee = (committee == null) ? "":committee;
	}

	@Override
	public String toString() {
		return "BillInfo [assemblySameAs=" + assemblySameAs + ", billId="
				+ billId + ", committee=" + committee + ", cosponsors="
				+ cosponsors + ", lawSection=" + lawSection + ", sameAs="
				+ sameAs + ", senateId=" + senateId + ", sponsor=" + sponsor
				+ ", summary=" + summary + ", title=" + title + ", year="
				+ year + "]";
	}

    public ArrayList<Action> getActions()
    {
        return actions;
    }

    public void setActions(ArrayList<Action> actions)
    {
        this.actions = actions;
    }

    public ArrayList<Vote> getVotes()
    {
        return votes;
    }

    public void setVotes(ArrayList<Vote> votes)
    {
        this.votes = votes;
    }
}
