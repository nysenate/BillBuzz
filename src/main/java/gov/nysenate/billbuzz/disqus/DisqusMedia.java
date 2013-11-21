package gov.nysenate.billbuzz.disqus;


public class DisqusMedia extends DisqusPrimaryObject
{
    private String forum;
    private String thread;
    private String location;
    private String post;
    private String type;
    private DisqusMediaMetadata metadata;
    private String description;
    private String title;
    private String url;
    private String mediaType;
    private String resolvedUrl;

    // Yes, they actually use both of these somehow
    private String thumbnailUrl;
    private String thumbnailURL;
    private int thumbnailWidth;
    private int thumbnailHeight;

    private String html;
    private int htmlHeight;
    private int htmlWidth;

    private String providerName;

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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public DisqusMediaMetadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(DisqusMediaMetadata metadata)
    {
        this.metadata = metadata;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getHtml()
    {
        return html;
    }

    public void setHtml(String html)
    {
        this.html = html;
    }

    public String getResolvedUrl()
    {
        return resolvedUrl;
    }

    public void setResolvedUrl(String resolvedUrl)
    {
        this.resolvedUrl = resolvedUrl;
    }

    public int getHtmlHeight()
    {
        return htmlHeight;
    }

    public void setHtmlHeight(int htmlHeight)
    {
        this.htmlHeight = htmlHeight;
    }

    public int getThumbnailWidth()
    {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth)
    {
        this.thumbnailWidth = thumbnailWidth;
    }

    public String getThumbnailURL()
    {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL)
    {
        this.thumbnailURL = thumbnailURL;
    }

    public int getThumbnailHeight()
    {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight)
    {
        this.thumbnailHeight = thumbnailHeight;
    }

    public int getHtmlWidth()
    {
        return htmlWidth;
    }

    public void setHtmlWidth(int htmlWidth)
    {
        this.htmlWidth = htmlWidth;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getProviderName()
    {
        return providerName;
    }

    public void setProviderName(String providerName)
    {
        this.providerName = providerName;
    }
}
