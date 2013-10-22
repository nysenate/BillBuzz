package gov.nysenate.billbuzz.model;

import java.util.HashMap;

public class BillBuzzParty
{
    private String id;
    private String name;

    private static HashMap<String, String> nameMap = new HashMap<String, String>();
    static {
        nameMap.put("D", "Democratic");
        nameMap.put("WF", "Working Families");
        nameMap.put("C", "Conservative");
        nameMap.put("R", "Republican");
        nameMap.put("IP", "Independence");
    }
    public BillBuzzParty(String id)
    {
        id = id.toUpperCase();
        this.setId(id);
        this.setName(nameMap.get(id));
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
