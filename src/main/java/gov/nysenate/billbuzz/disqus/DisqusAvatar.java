package gov.nysenate.billbuzz.disqus;


public class DisqusAvatar extends DisqusImage
{
    private DisqusImage small;
    private DisqusImage large;

    private boolean isCustom;

    public DisqusAvatar() {}

    public boolean getIsCustom()
    {
        return isCustom;
    }

    public void setCustom(boolean isCustom)
    {
        this.isCustom = isCustom;
    }

    public DisqusImage getSmall()
    {
        return small;
    }

    public void setSmall(DisqusImage small)
    {
        this.small = small;
    }

    public DisqusImage getLarge()
    {
        return large;
    }

    public void setLarge(DisqusImage large)
    {
        this.large = large;
    }
}
