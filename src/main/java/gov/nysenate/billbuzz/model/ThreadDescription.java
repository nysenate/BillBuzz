package gov.nysenate.billbuzz.model;


import java.util.ArrayList;
import java.util.List;
public class ThreadDescription extends DisqusObject {
//	private String category;
//	private String forum;
//	private String title;
//	private String num_comments;
//	private String created_at
	private String id;
	private String url;
	private BillInfo bi;
	private List<Comment> _comments;
	private ThreadDescription sameAsThread;
	public ThreadDescription() {
		_comments = new ArrayList<Comment>();
		sameAsThread = null;
		super.id = id;
	}
	public ThreadDescription(ThreadDescription td) {
		_comments = new ArrayList<Comment>();
		sameAsThread = null;
		this.id = td.getID();
		this.url = td.getURL();
		this.bi = td.getBill();
		this._comments.addAll(td.getComments());
	}
	public String getURL() {
		return url;
	}
	public List<Comment> getComments() {
		return _comments;
	}
	public void removeComment(Comment c) {
		int ind = _comments.indexOf(c);
		if(ind != -1) {
			_comments.remove(ind);
		}
	}
	public ThreadDescription getSameAsThread() {
		return sameAsThread;
	}
	public void setComments(List<Comment> comments) {
		this._comments = comments;
	}
	public void setBill(BillInfo bi) {
		this.bi = bi;
	}
	public BillInfo getBill() {
		return bi;
	}
	public void setURL(String url) {
		this.url = url;
	}
	public void setSameAsThread(ThreadDescription sameAsThread) {
		this.sameAsThread = sameAsThread;
	}
}