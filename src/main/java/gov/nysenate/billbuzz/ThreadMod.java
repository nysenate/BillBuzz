package gov.nysenate.billbuzz;

import gov.nysenate.billbuzz.model.BillInfo;
import gov.nysenate.billbuzz.model.Comment;
import gov.nysenate.billbuzz.model.DisqusObject;
import gov.nysenate.billbuzz.model.ForumDescription;
import gov.nysenate.billbuzz.model.ThreadDescription;
import gov.nysenate.billbuzz.persist.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author jaredwilliams
 * 
 * ThreadMod handles all threads read from Disqus, it decides what is valid and what isn't (based on if a post has been moderated,
 * if it is from the senate, assembly, etc...
 *
 */
public class ThreadMod {
	private List<ThreadDescription> _uT; //list to hold unmoderated comments
	private List<Comment> _uC; //list to hold unmoderated comments
	private Disqus _d;
	
	/*
	 * initializes _uC and _uT which are globals used to keep track of unmoderated comments
	 * _d is the Disqus object used to make calls to the Disqus API
	 * 
	 */
	public ThreadMod() {
		_uC = new ArrayList<Comment>();
		_uT = new ArrayList<ThreadDescription>();
		_d = new Disqus();
	}
	/**
	 * @param date is the date range of threads that should be read from Disqus in format YYYY-MM-DD:TXX:XX
	 * @returns a list of threads with their comments that are approved to be seen
	 */
	public List<ThreadDescription> getApprovedThreads(String date) {
		//get threads based on date
		List<ThreadDescription> newTLst = buildUpdateInformation(date);
		//determine which threads are approved
		newTLst = getValidThreads(newTLst);
		//get threads from last run of this program that were unmoderated at that time
		List<ThreadDescription> unmodTLst = readUnmoderatedThreads();
		//determine if they are now valid
		unmodTLst = getValidThreads(unmodTLst);
		//save threads from this run that are unmoderated
		saveUnmoderatedThreads();
		//return a combined list of new threads and those that were previously unmoderated
		return combineThreadLists(newTLst, unmodTLst);
	}
	
	/**
	 * This function combines two lists of ThreadDescription.  The reason it is necessary is if a thread were in the list for the new run
	 * and if a thread from a previously unmoderated post was added in as well they would show up as two seperate comments.  This combines
	 * the threads so the issue is avoided.
	 * @param l1 is a list of ThreadDescription
	 * @param l2 is a list of ThreadDescription
	 * @return the two list combined
	 */
	private List<ThreadDescription> combineThreadLists(List<ThreadDescription> l1, List<ThreadDescription> l2) {
		List<ThreadDescription> ret = new ArrayList<ThreadDescription>();
		ret.addAll(l1);
		for(ThreadDescription td : l2) {
			int tInd = containsDisqusObject(l1, td);
			if(tInd == -1) { //if it's an entirely new thread
				ret.add(td);
			}
			else { //if the thread already exists combine comments and add it
				ThreadDescription temp = l1.get(tInd);
				ret = addThread(ret, temp);
			}
		}
		return ret;
	}
	
	/**
	 * This function seperates threads/comments ready to be posted and those
	 * that still must be moderated
	 * @param tdlst is a list of unchecked ThreadDescription from disqus
	 * @returns a list of validated threads
	 */
	private List<ThreadDescription> getValidThreads(List<ThreadDescription> tdlst) {
		List<ThreadDescription> retTd = new ArrayList<ThreadDescription>();
		for(ThreadDescription td : tdlst) {
			_uC.clear(); //clear unmoderated comments
			ThreadDescription newtd = null;
			List<Comment> clst = td.getComments();
			List<Comment> newclst = new ArrayList<Comment>();
			for(Comment c : clst) {
				if(validComment(c)) {
					if(newtd == null) { 
						//if thihs is the first comment being added generate thread
						newtd = tdFromString(c.getThreadInfo());
					}
					//add comment
					newclst.add(c);
				}
				else {
					//if the post is unmoderated at it to unmoderated comments
					_uC.add(c);
				}
			}
			
			String bill = OpenLegXML.getBillFromURL(td.getURL());
			if(bill.equals(""))
				continue;
			else {
				if(bill.indexOf("-") == -1) {
					td.setURL(td.getURL() + "-2009");
				}
				
			}
			
			//if a new thread has been created then there are accepted comments
			if(newtd != null) {
				newtd.setComments(newclst);
				newtd.setBill(new OpenLegXML().getBillByURL(td.getURL()));
				retTd = addThread(retTd, newtd);
			}
			//if _uC is not empty unmoderated comments have been found, a thread is generated and added to _uT
			if(!_uC.isEmpty()) {
				ThreadDescription temp = tdFromString(_uC.get(0).getThreadInfo());
				temp.setBill(new OpenLegXML().getBillByURL(td.getURL()));
				temp.getComments().addAll(_uC);
				_uT = addThread(_uT, temp);				
			}
		}
		return retTd;
	}
	
	
	
	
	/**
	 * This function uses Disqus to build the overall thread->comments structure
	 * @param date is the date that determines what posts should be indexed
	 * @returns List<ForumDescription> of all of the forums associated with a Disqus account
	 * @throws Exception
	 */	
	private List<ThreadDescription> buildUpdateInformation(String date) {
		System.out.println("Reading from Disqus... (connection errors will be reported)");
		List<ThreadDescription> ret = new ArrayList<ThreadDescription>(); //empty forum list to be returned
		List<ForumDescription> forums = _d.forumList();
		Iterator<ForumDescription> forumItr = forums.iterator();
		while(forumItr.hasNext()) { //iterate forums
			List<ThreadDescription> threads = _d.getUpdatedThreads(forumItr.next().getID(), date);
			Iterator<ThreadDescription> threadItr = threads.iterator();
			while(threadItr.hasNext()) { //iterate threads
				ThreadDescription td = threadItr.next();
				td.setComments(_d.getThreadPosts(td.getID(), date));
				if(td.getComments().size() > 0) { //if there are approved comments in the thread
					ret.add(td);	
				}
			}
		}
		return ret;
	}
	
	/**
	 * This function iteratively moves though lst.  If it finds an instance of td it combines the comments
	 * for each Thread so there isn't any overlapping, otherwise it simply adds the Thread to the list
	 * @param lst is a list of ThreadDescription objects
	 * @param td is a thread that is to be added to lst
	 * @returns a new list of ThreadDescription
	 */
	private List<ThreadDescription> addThread(List<ThreadDescription> lst, ThreadDescription td) {
		int tIndex = hasSameAsThread(lst,td);
		//if the bill be added already has an assembly or senate equivalent
		if(tIndex != -1) { 
			ThreadDescription temp = lst.get(tIndex);
			String temps = temp.getBill().getBillId();
			if(temps.startsWith("A")) { //if temp is the assembly bill
				td.setSameAsThread(temp);
				lst.set(tIndex, td);
			}
			else { //if temp is a senate bill
				temp.setSameAsThread(td);
				lst.set(tIndex, temp);
			}			
			return lst;
		}
		
		//if the thread doesn't exist simply add it
		tIndex = containsDisqusObject(lst, td);
		if(tIndex == -1) { //if there isn't an occurance of the thread
			lst.add(td);
			return lst;
		}
		
		//if there is an occurance
		ThreadDescription temp = lst.get(tIndex);
		List<Comment> tempComList = temp.getComments();
		List<Comment> comList = td.getComments();
		List<Comment> newList = new ArrayList<Comment>();
		newList.addAll(tempComList);
		for(Comment c : comList) {
			int cIndex = containsDisqusObject(tempComList, c);
			if(cIndex == -1) { //if it's a new comment
				newList.add(c);
			}
			else { //if the comment already existed
				//comment already exists
			}				
		}
		temp.setComments(newList); //add comments to the nthread
		lst.set(tIndex, temp); //replace thread in list
		return lst;			
	}
	
	/**
	 * checks to see if lst contains a thread that is the sameas equivalent of td
	 * @param lst is a list of ThreadDescription
	 * @param td is the ThreadDescription being searched for
	 * @returns -1 if not found, the index of the thread otherwise
	 */
	private int hasSameAsThread(List<ThreadDescription> lst, ThreadDescription td) {
		BillInfo tdB = td.getBill();
		for(ThreadDescription lstTd: lst) {
			BillInfo lstB = lstTd.getBill();
			String s1 = tdB.getAssemblySameAs();
			String s2 = lstB.getBillId();
			if(!s1.equals("") && s1.equals(s2)) {
				return lst.indexOf(lstTd);
			}			
		}
		return -1;
	}
	
	/**
	 * This function opens the file containing the previously unmoderated posts, reads through it
	 * and detects if posts labeled unmoderated from a previous run of BillBuzz have had their state changed.
	 * If they have they're sent to be dispatched, if not they remain in the unmoderated post file
	 * @returns the new list with any previously unmoderated coments and threads
	 * @throws Exception
	 */
	private List<ThreadDescription> readUnmoderatedThreads() {
		
		List<String> unmoderated = Controller.getUnmoderated();
		
		List<ThreadDescription> lst = new ArrayList<ThreadDescription>();
		try {
			for(String in:unmoderated) {
				
				ThreadDescription tempThread = null;
				
				List<Comment> comList = new ArrayList<Comment>(); //this will store comments that are now approved
				
				String[] vals = in.split(","); //[0] is id, [1]-[n] are post ids
				
				List<Comment> com = _d.getThreadPosts(vals[0], "@"); //get comments from associated thread
				
				for(Comment c: com) {			
					//iterates through comments for given thread
					for(int i = 1; i < vals.length; i++) { 
						//if the given comment id matches a comment in the thread
						if(vals[i].equals(c.getID())) { 
							//if the thread is null initialize it
							if(tempThread == null) { 								
								tempThread = tdFromString(c.getThreadInfo());
							}
							//add comment to thread
							comList.add(c); 
						}
					}			
				}
				
				tempThread.setComments(comList);
				
				lst.add(tempThread);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}
	
	
	
	/**Compiles list from _uC and _uT of currently unmoderated comments in to the following form:
	 * 		  	<threadid 1>,<commentid 1>,<commentid 2>,...,<commentid n>
	 * 			...
	 * 			<threadid m>,<commentid 1>,<commentid 2>,...,<commentid n>
	 * @throws IOException 
	 * @returns CSV list of unmoderated information
	 */
	private void saveUnmoderatedThreads() {
		
		List<String> unmoderated = new ArrayList<String>();
		
		for(ThreadDescription td : _uT) { //iterate threads with unmoderated posts
			String thread = td.getID(); //add thread id to string
			for(Comment c : td.getComments()) { //iterate posts in a thread
				thread += "," + c.getID(); //add post ids
			}
			unmoderated.add(thread);
			if(td.getSameAsThread() != null) {
				String sameas = td.getSameAsThread().getID();
				for(Comment c : td.getSameAsThread().getComments()) { //iterate posts in a thread
					sameas += "," + c.getID(); //add post ids
				}
				unmoderated.add(sameas);
			}
		}
		
		Controller.saveUnmoderated(unmoderated);
	}
	
	/**
	 * @param lst is a list of DisqusObject objects
	 * @param d is a DisqusObject
	 * @returns the index, if available, of d in lst based on d.getID()
	 */
	private int containsDisqusObject(List<?> lst, Object d) {
		DisqusObject dObj = (DisqusObject)d;
		Iterator<?> itr = lst.iterator();
		while(itr.hasNext()) {
			DisqusObject t = (DisqusObject)itr.next();
			if(t.getID().equals(dObj.getID())) {
				return lst.indexOf(t);
			}
		}		
		return -1;
	}
	
	/**
	 * @param s is a string with the JSON information from Disqus for a thread
	 * @returns a ThreadDescription object from the JSON information
	 */
	private ThreadDescription tdFromString(String s) {
		JsonParser parser = new JsonParser();
		JsonElement je = (JsonElement)parser.parse(s);
		Gson gson = new Gson();
		return gson.fromJson(je, ThreadDescription.class);
	}
	
	/**
	 * @param c is the comment to be validated
	 * @returns true if valid, false otherwise
	 */
	private boolean validComment(Comment c) {
		if(!c.getHasBeenModerated().equals("true")) {
			return false;
		}
		if(!c.getStatus().equals("approved")) {
			return false;
		}
		return true;
	}
}
