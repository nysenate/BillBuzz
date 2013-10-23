package gov.nysenate.billbuzz.model;

import java.util.Date;

public class BillBuzzConfirmation
{
    private Long id;
    private String action;
    private String code;
    private Long userId;
    private BillBuzzUser user;
    private Date createdAt;
    private Date expiresAt;
    private Date usedAt;

    public BillBuzzConfirmation()
    {

    }

    public BillBuzzConfirmation(Long userId, String action, String code, Date createdAt, Date expiresAt)
    {
        this.setUserId(userId);
        this.setAction(action);
        this.setCode(code);
        this.setCreatedAt(createdAt);
        this.setExpiresAt(expiresAt);
    }

    public String getCode()
    {
        return code;
    }

    public boolean isExpired()
    {
        return this.expiresAt != null && this.expiresAt.before(new Date());
    }

    public boolean isUsed()
    {
        return this.usedAt != null;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Date getUsedAt()
    {
        return usedAt;
    }

    public void setUsedAt(Date usedAt)
    {
        this.usedAt = usedAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt()
    {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    public BillBuzzUser getUser()
    {
        return user;
    }

    public void setUser(BillBuzzUser user)
    {
        this.user = user;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
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
