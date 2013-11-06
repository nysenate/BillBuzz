package gov.nysenate.billbuzz.model.openleg;

public class Action
{
    private String action;
    private long timestamp;

    public String getAction()
    {
        return action;
    }
    public void setAction(String action)
    {
        this.action = action;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
