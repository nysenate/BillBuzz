package gov.nysenate.billbuzz.model;


public class BillBuzzSenator implements Comparable<BillBuzzSenator>
{
    private Long id;
    private String name;
    private Integer sessionYear;
    private boolean active;
    private String shortName;

    public BillBuzzSenator()
    {

    }

    public BillBuzzSenator(String name, String shortName, Integer year)
    {
        this.setName(name);
        this.setSessionYear(year);
        this.setShortName(shortName);
        this.setActive(true);
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getSessionYear()
    {
        return sessionYear;
    }

    public void setSessionYear(Integer sessionYear)
    {
        this.sessionYear = sessionYear;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public int compareTo(BillBuzzSenator other)
    {
        int cmpName = this.getShortName().compareTo(other.getShortName());
        int cmpYear = this.getSessionYear().compareTo(other.getSessionYear());
        return cmpName == 0 ? cmpYear : cmpName;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String toString()
    {
        return this.getShortName();
    }
}
