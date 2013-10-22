package gov.nysenate.billbuzz.disqus;

public class DisqusImage
{
    private String cache;
    private String permalink;

    public DisqusImage() {}

    public String getCache()
    {
        return cache;
    }
    public void setCache(String cache)
    {
        this.cache = cache;
    }
    public String getPermalink()
    {
        return permalink;
    }
    public void setPermalink(String permalink)
    {
        this.permalink = permalink;
    }
}
