package gov.nysenate.billbuzz.disqus;

import gov.nysenate.billbuzz.disqus.models.BaseObject;
import gov.nysenate.billbuzz.disqus.models.Forum;
import gov.nysenate.billbuzz.disqus.models.Post;
import gov.nysenate.billbuzz.disqus.models.Thread;
import gov.nysenate.billbuzz.model.Comment;
import gov.nysenate.billbuzz.model.ThreadDescription;
import gov.nysenate.billbuzz.util.Config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Disqus {
    public static class ForumResponse extends Response<Forum> {}
    public static class ThreadResponse extends Response<Thread> {}
    public static class PostResponse extends Response<Post> {}

	private final SimpleDateFormat disqusDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final String TEMPLATE = "https://disqus.com/api/3.0/%s.json?api_key=%s&api_secret=%s&access_token=%s&%s";

	private final String PUBLIC_KEY;
	private final String SECRET_KEY;
	private final String ACCESS_TOKEN;

	public Disqus(String publicKey, String secretKey, String accessToken) {
	    PUBLIC_KEY = publicKey;
	    SECRET_KEY = secretKey;
	    ACCESS_TOKEN = accessToken;
	}

	public static void main(String[] args) throws Exception
	{
	    Disqus disqus = new Disqus(Config.getValue("disqus.public_key"), Config.getValue("disqus.secret_key"), Config.getValue("disqus.access_token"));

	    for (Forum forum : disqus.userListForumsAll()) {
	        System.out.println(String.format("[%s] %s - %s", forum.getID(), forum.getFounder(), forum.getName()));
	        Response<Thread> response = (Response<Thread>)disqus.getResponse("forums/listThreads", ThreadResponse.class, "forum="+forum.getID());
	        for (Thread thread : response.getResponse()) {
	            System.out.println(String.format("[%s] %s - %s - %s", thread.getID(), thread.getTitle(), thread.getCreatedAt(), thread.getLink()));
	        }
	    }
	}

	public List<Forum> userListForumsAll(String...params) throws IOException
	{
	    Response<Forum> response = userListForums(params);
	    if (response.getCode()!=0) {
	        return null;
	    }
	    return (List<Forum>)fetchAll(response);
	}

    public Response<Forum> userListForums(String...params) throws IOException
    {
        return (Response<Forum>)getResponse("users/listForums", ForumResponse.class, params);
    }

	public List<Thread> forumsListThreadsAll(String...params) throws IOException
	{
        Response<Thread> response = forumsListThreads(params);
        if (response.getCode()!=0) {
            return null;
        }
        return (List<Thread>)fetchAll(response);
	}

    public Response<Thread> forumsListThreads(String...params) throws IOException
    {
        return (Response<Thread>)getResponse("forums/listThreads", ThreadResponse.class, params);
    }

    public List<Post> forumsListPostsAll(String...params) throws IOException
    {
        Response<Post> response = forumsListPosts(params);
        if (response.getCode()!=0) {
            return null;
        }
        return (List<Post>)fetchAll(response);
    }

    public Response<Post> forumsListPosts(String...params) throws IOException
    {
        return (Response<Post>)getResponse("forums/listPosts", PostResponse.class, params);
    }

	public List<Post> threadsListPostsAll(String...params) throws IOException
	{
        Response<Post> response = threadsListPosts(params);
        if (response.getCode()!=0) {
            return null;
        }
        return (List<Post>)fetchAll(response);
	}

	public Response<Post> threadsListPosts(String...params) throws IOException
	{
	    return (Response<Post>)getResponse("threads/listPosts", PostResponse.class, params);
	}

	/**
	 * @param key is the API key for the Disqus Account
	 * @param forumID is the ID of the forum being searched
	 * @param sinceDate determines what threads should be indexed (only threads after sinceDate)
	 * @returns List<ThreadDescriptions> of all the threads in a particular forum
	 * @throws Exception
	 */
	public List<ThreadDescription> getUpdatedThreads(String forumID, String sinceDate) throws IOException
	{
		List<ThreadDescription> updatedThreads = new ArrayList<ThreadDescription>();
		Response<Post> response = (Response<Post>)getResponse("posts/list",PostResponse.class,"forum_id="+forumID,"since="+sinceDate);
//		Gson gson = new Gson();
//		while(itr.hasNext()) {
//			updatedThreads.add(gson.fromJson(itr.next(), ThreadDescription.class));
//		}
		return updatedThreads;
	}

	/**
	 * @param key is the API key for the Disqus account
	 * @param threadID is the ID of the thread being searched
	 * @param date determines what posts should be indexed (only posts after date), date being passed as "@" is a special case used simply to retrieve data
	 * @returns List<Comment> of all the comments in a particular thread
	 * @throws Exception
	 */
	public List<Comment> getThreadPosts(String threadID, String date) throws IOException
	{
		List<Comment> comments = new ArrayList<Comment>();
		Response<Post> response = (Response<Post>)getResponse("threads/listPosts",PostResponse.class,"thread="+threadID);//,"exclude=spam");
//		Gson gson = new Gson();
//		while(itr.hasNext()) {
//			JsonElement curEl = itr.next();
//			JsonObject curObj = curEl.getAsJsonObject();
//			Comment newcomment = gson.fromJson(curEl, Comment.class);
//			newcomment.setThreadInfo(curObj.get("thread").toString()); //save thread information within comment since the
//			newcomment = getComment(curObj, newcomment);				//disqus api doesn't work well for just retrieving thread information from a threadid
//			if(compareDates(newcomment.getCreatedAt(), date) || date.equals("@")) { //if date is valid
//				comments.add(newcomment);
//			}
//		}
		return comments;
	}

//	public Comment getComment(JsonObject j, Comment c) {
//		if(j.get("author") != null) { //if post not anonymous
//			c.setUsername(j.get("author").getAsJsonObject().get("username").toString().replaceAll("\"", ""));
//			c.setDisplayName(j.get("author").getAsJsonObject().get("display_name").toString().replaceAll("\"", ""));
//			c.setEmail(j.get("author").getAsJsonObject().get("email").toString().replaceAll("\"", ""));
//		}
//		else { //if post is anonymous
//			c.setUsername(j.get("anonymous_author").getAsJsonObject().get("name").toString().replaceAll("\"", ""));
//			c.setEmail(j.get("anonymous_author").getAsJsonObject().get("email").toString().replaceAll("\"", ""));
//		}
//		return c;
//	}

    public Response<?> getResponse(String method, Class<? extends Response<?>> responseType, String...args) throws IOException
    {
        String url = String.format(TEMPLATE, method, PUBLIC_KEY, SECRET_KEY, ACCESS_TOKEN, StringUtils.join(args));
        String responseString = Request.Get(url).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.getDeserializationConfig().with(disqusDateFormat);
        Response<?> response = mapper.readValue(responseString, responseType);
        response.setMethod(method);
        response.setArgs(args);
        return response;
    }

    public List<? extends BaseObject> fetchAll(Response<? extends BaseObject> response) throws IOException
    {
        List<BaseObject> results = (List<BaseObject>)response.getResponse();
        while(response.getCursor().getHasNext()) {
            response = getNext(response);
            for (BaseObject o : response.getResponse()) {
                results.add(o);
            }
        }
        return results;
    }

    public Response<? extends BaseObject> getNext(Response<? extends BaseObject> response) throws IOException
    {
        Class<? extends Response<?>> responseClass = (Class<? extends Response<?>>) response.getClass();
        List<String> args = new ArrayList(Arrays.asList(response.getArgs()));
        args.add("cursor="+response.getCursor().getNext());
        Response<? extends BaseObject> newResponse = getResponse(response.getMethod(), responseClass, args.toArray(new String[]{}));
        // Make sure to re-use the old args that don't contain cursor
        newResponse.setArgs(response.getArgs());
        return newResponse;
    }
}


