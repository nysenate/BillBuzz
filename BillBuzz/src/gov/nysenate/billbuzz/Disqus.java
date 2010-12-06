package gov.nysenate.billbuzz;

import gov.nysenate.billbuzz.model.CallContents;
import gov.nysenate.billbuzz.model.Comment;
import gov.nysenate.billbuzz.model.ForumDescription;
import gov.nysenate.billbuzz.model.ThreadDescription;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.*;
import java.util.*;
/**
 * @author Jared
 *This Class connects to the Disqus API and based on a user/system given date returns an Object in the following format:
 *	|--->Forum 1:
 *	|		|--->Thread 1:
 *	|		|		|--->Post 1: etc
 *	|		|		|--->Post 2: etc
 *	|		|--->Thread 2:
 *	|				|--->Post 1: etc
 *	|
 *	|--->Forum 2:
 *	|		|--->Thread 1:
 *	|				|--->Post 1: etc
 *	|
 *	|--->etc...
 */
public class Disqus {	
	private static String _website = Resource.get("website");
	private static String _api = Resource.get("api");
	private static String _dateDivide = "[\\-T:]";
	private static String _key = Resource.get("key");	
	public Disqus() {
	}
	
	/**
	 * @param command is the command being sent to the Disqus API
	 * @returns the command result from Disqus
	 * @throws Exception
	 */
	public String commandResults(String command) {
		for(int i = 1; i < 5; i++) {
			try {
				BufferedReader reader;
				reader = new BufferedReader(new InputStreamReader(new URL(command).openStream()));
				String line = reader.readLine();
				reader.close();
				return line;
			} catch (MalformedURLException e) {
				System.out.println("Unable to connect... attempt #" + i);
			} catch (IOException e) {
				System.out.println("Unable to connect... attempt #" + i);
			}
		}
		System.out.println("Connect failed, terminating program...\n");
		System.exit(0);
		return null;
	}
	
	/**Disqus returns every command in the following format: {message="" code="" succeeded=""}
	 * this function decompiles the message and puts it in to a CallContents object
	 * @param in is the result returned from commandResults from Disqus
	 * @returns CallContents object with the information from the command results
	 */
	public CallContents messageContents(String in) {
		JsonParser parser = new JsonParser();
		JsonObject command = (JsonObject)parser.parse(in);
		JsonElement message = command.get("message");
		JsonElement code = command.get("code");
		JsonElement success = command.get("succeeded");
		return new CallContents(message.toString(), code.toString(), success.toString());
	}
	
	public Iterator<JsonElement> resultIterator(String command) {
		String results = commandResults(command);
		CallContents cc = messageContents(results);
		JsonParser parser = new JsonParser();
		JsonArray array = (JsonArray)parser.parse(cc.getMessage());
		return array.iterator();
	}	
	
	
	/**
	 * @return List<ForumDescription> of all the forums associated with the partciuarl Disqus account
	 * @throws Exception
	 */
	public List<ForumDescription> forumList() {
		List<ForumDescription> forumDescriptions = new ArrayList<ForumDescription>();		
		String results = _website + "get_forum_list?" + "user_api_key=" + _key + _api;
		Iterator<JsonElement> itr = resultIterator(results);
		Gson gson = new Gson();
		while(itr.hasNext()) {
			forumDescriptions.add(gson.fromJson(itr.next(), ForumDescription.class));
		}
		return forumDescriptions;
	}
	
	/**
	 * @param key is the API key for the Disqus Account
	 * @param forumID is the ID of the forum being searched
	 * @param sinceDate determines what threads should be indexed (only threads after sinceDate)
	 * @returns List<ThreadDescriptions> of all the threads in a particular forum
	 * @throws Exception
	 */
	public List<ThreadDescription> getUpdatedThreads(String forumID, String sinceDate) {
		List<ThreadDescription> updatedThreads = new ArrayList<ThreadDescription>();
		String results = _website + "get_updated_threads?" + "user_api_key=" + _key + "&forum_id=" + forumID + "&since=" + sinceDate + _api;
		Iterator<JsonElement> itr = resultIterator(results);
		Gson gson = new Gson();
		while(itr.hasNext()) {
			updatedThreads.add(gson.fromJson(itr.next(), ThreadDescription.class));	
		}
		return updatedThreads;
	}
	
	/**
	 * @param key is the API key for the Disqus account
	 * @param threadID is the ID of the thread being searched
	 * @param date determines what posts should be indexed (only posts after date), date being passed as "@" is a special case used simply to retrieve data
	 * @returns List<Comment> of all the comments in a particular thread
	 * @throws Exception
	 */
	public List<Comment> getThreadPosts(String threadID, String date) {
		List<Comment> comments = new ArrayList<Comment>();
		String results = _website + "get_thread_posts?" + "user_api_key=" + _key + "&thread_id=" + threadID + "&exclude=spam" + _api;
		Iterator<JsonElement> itr = resultIterator(results);
		Gson gson = new Gson();
		while(itr.hasNext()) {		
			JsonElement curEl = itr.next();
			JsonObject curObj = curEl.getAsJsonObject();		
			Comment newcomment = gson.fromJson(curEl, Comment.class);
			newcomment.setThreadInfo(curObj.get("thread").toString()); //save thread information within comment since the
			newcomment = getComment(curObj, newcomment);				//disqus api doesn't work well for just retrieving thread information from a threadid
			if(compareDates(newcomment.getCreatedAt(), date) || date.equals("@")) { //if date is valid
				comments.add(newcomment);
			}
		}	
		return comments;
	}
	
	public Comment getComment(JsonObject j, Comment c) {
		if(j.get("author") != null) { //if post not anonymous
			c.setUsername(j.get("author").getAsJsonObject().get("username").toString().replaceAll("\"", ""));
			c.setDisplayName(j.get("author").getAsJsonObject().get("display_name").toString().replaceAll("\"", ""));
			c.setEmail(j.get("author").getAsJsonObject().get("email").toString().replaceAll("\"", ""));
		}
		else { //if post is anonymous
			c.setUsername(j.get("anonymous_author").getAsJsonObject().get("name").toString().replaceAll("\"", ""));
			c.setEmail(j.get("anonymous_author").getAsJsonObject().get("email").toString().replaceAll("\"", ""));
		}
		return c;
	}
	
	
	/**
	 * @param s is a string array that should be convereted to int[]
	 * @returns an int array from s[]
	 */
	public int[] stringArrayToInt(String s[]) {
		int ret[] = new int[s.length];
		for(int i = 0; i < s.length; i++) {
			
			while(s[i].startsWith("0") && s[i].compareTo("00") != 0) {
				s[i] = s[i].substring(1);
			}
			ret[i] = Integer.parseInt(s[i]);
		}
		return ret;
	}
	
	/**
	 * @param commentDate is the date of a given comment
	 * @param givenDate is the date given by the user/system that determines what posts should be indexed
	 * @return
	 */
	public boolean compareDates(String cd, String gd) {
		if(gd.equals("@")) {
			return true;
		}
		int[] commentDate = stringArrayToInt(cd.split(_dateDivide));
		int[] givenDate = stringArrayToInt(gd.split(_dateDivide));
		for(int i = 0; i < commentDate.length; i++) {
			if(commentDate[i] > givenDate[i]) {
				return true;
			}
			else if(commentDate[i] < givenDate[i]) {
				break;
			}
		}
		return false;
	}
}


