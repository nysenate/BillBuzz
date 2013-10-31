package gov.nysenate.billbuzz.model;

import java.util.List;

public class BillBuzzSenator
{
    private Long id;
    private String name;
    private Integer session;
    private String shortName;
    private List<BillBuzzParty> parties;

    public BillBuzzSenator()
    {

    }

    public BillBuzzSenator(String name, String shortName, Integer year, List<BillBuzzParty> parties)
    {
        this.setName(name);
        this.setSession(year);
        this.setShortName(shortName);
        this.setParties(parties);
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getSession()
    {
        return session;
    }

    public void setSession(Integer session)
    {
        this.session = session;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public List<BillBuzzParty> getParties()
    {
        return parties;
    }

    public void setParties(List<BillBuzzParty> parties)
    {
        this.parties = parties;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }


}
