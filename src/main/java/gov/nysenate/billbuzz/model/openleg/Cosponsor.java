package gov.nysenate.billbuzz.model.openleg;

public class Cosponsor
{
    private String cosponsor;

    public Cosponsor() {}

    public Cosponsor(String cosponsor)
    {
        setCosponsor(cosponsor);
    }

    public String getCosponsor()
    {
        return cosponsor;
    }

    public void setCosponsor(String cosponsor)
    {
        this.cosponsor = cosponsor;
    }

}
