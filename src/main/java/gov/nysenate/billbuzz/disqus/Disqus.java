package gov.nysenate.billbuzz.disqus;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Connector for the Disqus API.
 *
 * The Disqus API has two distinct response types: Single document and document list. Each of these response types
 * is extended by a specific document type represented so that they system is type safe. List responses contain a
 * cursor which can be used to fetch the next page repeatedly and list out all results as necessary.
 *
 * Methods are named according to their API path. user/listForums => userListForums
 * Parameters are passed in apiMethod("key=value", "key2=value2") format. Values must already be escaped if necessary.
 *
 * NOTE: At time of writing, the API since= parameter was broken; confirmed by Disqus support. No ETA on a fix.
 *
 * @author GraylinKim
 *
 */
@SuppressWarnings("unchecked")
public class Disqus
{
    private final Logger logger = Logger.getLogger(Disqus.class);

    public static class ForumListResponse extends DisqusListResponse<DisqusForum> {}
    public static class ThreadListResponse extends DisqusListResponse<DisqusThread> {}
    public static class PostListResponse extends DisqusListResponse<DisqusPost> {}
    public static class ForumResponse extends DisqusObjectResponse<DisqusForum> {}
    public static class ThreadResponse extends DisqusObjectResponse<DisqusThread> {}
    public static class PostResponse extends DisqusObjectResponse<DisqusPost> {}
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private final String TEMPLATE = "http://disqus.com/api/3.0/%s.json?api_key=%s&api_secret=%s&access_token=%s&%s";
	private final String PUBLIC_KEY;
	private final String SECRET_KEY;
	private final String ACCESS_TOKEN;

	public Disqus(String publicKey, String secretKey, String accessToken)
	{
	    PUBLIC_KEY = publicKey;
	    SECRET_KEY = secretKey;
	    ACCESS_TOKEN = accessToken;
	}

	public DisqusPost postDetails(String...params) throws IOException
	{
	    DisqusObjectResponse<DisqusPost> response = (DisqusObjectResponse<DisqusPost>)getResponse("posts/details", PostResponse.class, params);
        if (response.getCode()!=0) {
            return null;
        }
        return response.getResponse();
	}

	public List<DisqusForum> userListForumsAll(String...params) throws IOException
	{
	    DisqusListResponse<DisqusForum> response = userListForums(params);
	    if (response.getCode()!=0) {
	        return null;
	    }
	    return (List<DisqusForum>)fetchAll(response);
	}

    public DisqusListResponse<DisqusForum> userListForums(String...params) throws IOException
    {
        return (DisqusListResponse<DisqusForum>)getListResponse("users/listForums", ForumListResponse.class, params);
    }

	public List<DisqusThread> forumsListThreadsAll(String...params) throws IOException
	{
        DisqusListResponse<DisqusThread> response = forumsListThreads(params);
        if (response.getCode()!=0) {
            return null;
        }
        return (List<DisqusThread>)fetchAll(response);
	}

    public DisqusListResponse<DisqusThread> forumsListThreads(String...params) throws IOException
    {
        return (DisqusListResponse<DisqusThread>)getListResponse("forums/listThreads", ThreadListResponse.class, params);
    }

    public List<DisqusPost> forumsListPostsAll(String...params) throws IOException
    {
        DisqusListResponse<DisqusPost> response = forumsListPosts(params);
        if (response.getCode()!=0) {
            return null;
        }
        return (List<DisqusPost>)fetchAll(response);
    }

    public DisqusListResponse<DisqusPost> forumsListPosts(String...params) throws IOException
    {
        return (DisqusListResponse<DisqusPost>)getListResponse("forums/listPosts", PostListResponse.class, params);
    }

	public List<DisqusPost> threadsListPostsAll(String...params) throws IOException
	{
        DisqusListResponse<DisqusPost> response = threadsListPosts(params);
        if (response.getCode()!=0) {
            return null;
        }
        return (List<DisqusPost>)fetchAll(response);
	}

	public DisqusListResponse<DisqusPost> threadsListPosts(String...params) throws IOException
	{
	    return (DisqusListResponse<DisqusPost>)getListResponse("threads/listPosts", PostListResponse.class, params);
	}

    public DisqusObjectResponse<?> getResponse(String method, Class<? extends DisqusObjectResponse<?>> responseType, String...args) throws IOException
    {
        String url = String.format(TEMPLATE, method, PUBLIC_KEY, SECRET_KEY, ACCESS_TOKEN, StringUtils.join(args, "&"));
        logger.info("Fetching: "+url);
        String responseString = Request.Get(url).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.getDeserializationConfig().withDateFormat(dateFormat);
        DisqusObjectResponse<?> response = mapper.readValue(responseString, responseType);
        response.setMethod(method);
        response.setArgs(args);
        return response;
    }

    public DisqusListResponse<?> getListResponse(String method, Class<? extends DisqusListResponse<?>> responseType, String...args) throws IOException
    {
        String url = String.format(TEMPLATE, method, PUBLIC_KEY, SECRET_KEY, ACCESS_TOKEN, StringUtils.join(args, "&"));
        logger.info("Fetching: "+url);
        String responseString = Request.Get(url).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.getDeserializationConfig().withDateFormat(dateFormat);
        DisqusListResponse<?> response = mapper.readValue(responseString, responseType);
        response.setMethod(method);
        response.setArgs(args);
        return response;
    }

    public List<? extends DisqusPrimaryObject> fetchAll(DisqusListResponse<? extends DisqusPrimaryObject> response) throws IOException
    {
        List<DisqusPrimaryObject> results = (List<DisqusPrimaryObject>)response.getResponse();
        while(response.getCursor().getHasNext()) {
            response = getNext(response);
            for (DisqusPrimaryObject o : response.getResponse()) {
                results.add(o);
            }
        }
        return results;
    }

    public DisqusListResponse<? extends DisqusPrimaryObject> getNext(DisqusListResponse<? extends DisqusPrimaryObject> response) throws IOException
    {
        Class<? extends DisqusListResponse<?>> responseClass = (Class<? extends DisqusListResponse<?>>) response.getClass();
        List<String> args = new ArrayList<String>(Arrays.asList(response.getArgs()));
        args.add("cursor="+response.getCursor().getNext());
        DisqusListResponse<? extends DisqusPrimaryObject> newResponse = getListResponse(response.getMethod(), responseClass, args.toArray(new String[]{}));
        // Make sure to re-use the old args that don't contain cursor
        newResponse.setArgs(response.getArgs());
        return newResponse;
    }
}


