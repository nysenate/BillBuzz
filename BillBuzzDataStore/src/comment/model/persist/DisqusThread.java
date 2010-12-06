package comment.model.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import comment.model.xml.*;
import comment.parser.ObjectHelper;
import comments.generated.Post;

@PersistenceCapable
public class DisqusThread {

	private String url;
	
	@PrimaryKey
	private String bill;
	
	private Date first;
	
	private Date last;
	
	@Persistent(defaultFetchGroup="true")
	private List<DisqusComment> comments;
	
	private int size;
	
	public DisqusThread () {
		comments = new ArrayList<DisqusComment>();
	}
	
	public DisqusThread (List<Post> posts) {
		url = posts.iterator().next().getLink();
		bill = ObjectHelper.billFromUrl(url);
		
		comments = new ArrayList<DisqusComment>();
		
		for(Post p: posts) {
			/*Date d = ObjectHelper.dateFromString(p.getCreatedAt());
			
			if(first == null || d.before(first)) {
				first = d;
			}
			
			if(last == null || d.after(last)) {
				last = d;
			}
			comments.add(new DisqusComment(c));*/
		}
		
		setSize();
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public void setBill(String bill) {
		this.bill = bill;
	}
	
	public String getUrl() {
		return url;
	}
	public String getBill() {
		return bill;
	}
	public Date getFirst() {
		return first;
	}
	public Date getLast() {
		return last;
	}
	public List<DisqusComment> getComments() {
		return comments;
	}
	public int getSize() {
		return size;
	}
	
	
	public void setComments(List<DisqusComment> comments) {
		for(DisqusComment c: comments) {
			Date d = c.getDate();
			
			if(first == null || d.before(first)) {
				first = d;
			}
			
			if(last == null || d.after(last)) {
				last = d;
			}
		}
		this.comments = comments;
		setSize();
	}
	public void setSize() {
		this.size = comments.size();
	}
	
}
