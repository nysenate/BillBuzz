package gov.nysenate.billbuzz.model;

import java.util.Date;

public class BillBuzzUpdate
{
    private Long id = null;
    private Date createdAt = null;
    private Date sentAt = null;

    public BillBuzzUpdate()
    {
        this.setCreatedAt(createdAt);
    }

    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
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
