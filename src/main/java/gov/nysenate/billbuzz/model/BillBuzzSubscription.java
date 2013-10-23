package gov.nysenate.billbuzz.model;

import java.util.Date;

public class BillBuzzSubscription
{
    private Long id;
    private Long userId;
    private String category;
    private String value;
    private Date createdAt;

    public BillBuzzSubscription()
    {

    }

    public BillBuzzSubscription(Long userId, String key, String value, Date createdAt)
    {
        this.setUserId(userId);
        this.setCategory(key);
        this.setValue(value);
        this.setCreatedAt(createdAt);
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public String getCategory()
    {
        return category;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }
    public Long getUserId()
    {
        return userId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
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
