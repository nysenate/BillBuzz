package gov.nysenate.billbuzz.model.openleg;

import java.util.ArrayList;

public class Vote
{
    private long timestamp;
    private int ayes;
    private int nays;
    private int abstains;
    private int excused;
    private ArrayList<Voter> voters;

    public long getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getAyes()
    {
        return ayes;
    }
    public void setAyes(int ayes)
    {
        this.ayes = ayes;
    }

    public int getNays()
    {
        return nays;
    }
    public void setNays(int nays)
    {
        this.nays = nays;
    }

    public int getAbstains()
    {
        return abstains;
    }
    public void setAbstains(int abstains)
    {
        this.abstains = abstains;
    }

    public int getExcused()
    {
        return excused;
    }
    public void setExcused(int excused)
    {
        this.excused = excused;
    }

    public ArrayList<Voter> getVoters()
    {
        return voters;
    }
    public void setVoters(ArrayList<Voter> voters)
    {
        this.voters = voters;
    }
}
