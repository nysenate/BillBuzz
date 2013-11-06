package gov.nysenate.billbuzz.disqus.models;


public class Forum extends BaseObject
{
    private String url;
    private String name;
    private String founder;
    private String language;
    private ForumSettings settings;
    private Image favicon;

    public Forum() {}

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

    public ForumSettings getSettings() {
        return settings;
    }

    public void setSettings(ForumSettings settings) {
        this.settings = settings;
    }

    public Image getFavicon()
    {
        return favicon;
    }

    public void setFavicon(Image favicon)
    {
        this.favicon = favicon;
    }
}
