package gov.nysenate.billbuzz.model;

import java.util.Date;

public class BillBuzzUpdate
{
    private Integer id = null;
    private Date createdAt = null;
    private Date sentAt = null;

    public BillBuzzUpdate()
    {
        this.setCreatedAt(createdAt);
    }

    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getSentAt()
    {
        return sentAt;
    }
    public void setSentAt(Date sentAt)
    {
        this.sentAt = sentAt;
    }
}
