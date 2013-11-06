package gov.nysenate.billbuzz.disqus.models;


public class Avatar extends Image
{
    private Image small;
    private Image large;

    private boolean isCustom;

    public Avatar() {}

    public boolean getIsCustom()
    {
        return isCustom;
    }

    public void setCustom(boolean isCustom)
    {
        this.isCustom = isCustom;
    }

    public Image getSmall()
    {
        return small;
    }

    public void setSmall(Image small)
    {
        this.small = small;
    }

    public Image getLarge()
    {
        return large;
    }

    public void setLarge(Image large)
    {
        this.large = large;
    }
}
