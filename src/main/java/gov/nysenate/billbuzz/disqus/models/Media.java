package gov.nysenate.billbuzz.disqus.models;


public class Media
{
    private String forum;
    private String thread;
    private String location;
    private String post;
    private String thumbnailURL;
    private String type;
    private MediaMetadata metadata;

    public String getForum()
    {
        return forum;
    }

    public void setForum(String forum)
    {
        this.forum = forum;
    }

    public String getThread()
    {
        return thread;
    }

    public void setThread(String thread)
    {
        this.thread = thread;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getPost()
    {
        return post;
    }

    public void setPost(String post)
    {
        this.post = post;
    }

    public String getThumbnailURL()
    {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL)
    {
        this.thumbnailURL = thumbnailURL;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public MediaMetadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(MediaMetadata metadata)
    {
        this.metadata = metadata;
    }
}
