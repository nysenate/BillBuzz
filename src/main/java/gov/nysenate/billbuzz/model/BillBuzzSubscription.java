package gov.nysenate.billbuzz.model;

import java.util.Date;

public class BillBuzzSubscription
{
    private Integer id;
    private Integer userId;
    private String category;
    private String value;
    private Date createdAt;

    public BillBuzzSubscription()
    {

    }

    public BillBuzzSubscription(Integer userId, String key, String value, Date createdAt)
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
    public Integer getUserId()
    {
        return userId;
    }
    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

}
