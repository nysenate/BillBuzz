package gov.nysenate.billbuzz.disqus;


public class DisqusForum extends DisqusPrimaryObject
{
    private String url;
    private String name;
    private String founder;
    private String language;
    private DisqusForumSettings settings;
    private DisqusImage favicon;

    public DisqusForum() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DisqusForumSettings getSettings() {
        return settings;
    }

    public void setSettings(DisqusForumSettings settings) {
        this.settings = settings;
    }

    public DisqusImage getFavicon()
    {
        return favicon;
    }

    public void setFavicon(DisqusImage favicon)
    {
        this.favicon = favicon;
    }
}
