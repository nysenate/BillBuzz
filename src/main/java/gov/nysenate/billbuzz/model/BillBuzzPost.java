package gov.nysenate.billbuzz.model;

import java.util.Date;

public class BillBuzzPost
{
    private String id;
    private String forumId;

    private String authorId;
    private BillBuzzAuthor author;

    private String threadId;
    private BillBuzzThread thread;

    private String parentId;
    private BillBuzzPost parent;

    private Integer points;
    private Integer likes;
    private Integer dislikes;
    private Integer userScore;
    private Integer numReports;

    private String rawMessage;
    private String message;

    private boolean juliaFlagged;
    private boolean isFlagged;
    private boolean isApproved;
    private boolean isSpam;
    private boolean isEdited;
    private boolean isHighlighted;
    private boolean isDeleted;

    private Date createdAt;
    private Date updatedAt;

    public BillBuzzPost()
    {

    }

    public Integer getLikes()
    {
        return likes;
    }
    public void setLikes(Integer likes)
    {
        this.likes = likes;
    }

    public boolean getIsDeleted()
    {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted)
    {
        this.isDeleted = isDeleted;
    }

    public boolean getIsHighlighted()
    {
        return isHighlighted;
    }

    public void setIsHighlighted(boolean isHighlighted)
    {
        this.isHighlighted = isHighlighted;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean getIsEdited()
    {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited)
    {
        this.isEdited = isEdited;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Integer getNumReports()
    {
        return numReports;
    }

    public void setNumReports(Integer numReports)
    {
        this.numReports = numReports;
    }

    public String getThreadId()
    {
        return threadId;
    }

    public void setThreadId(String threadId)
    {
        this.threadId = threadId;
    }

    public Integer getUserScore()
    {
        return userScore;
    }

    public void setUserScore(Integer userScore)
    {
        this.userScore = userScore;
    }

    public boolean getIsSpam()
    {
        return isSpam;
    }

    public void setIsSpam(boolean isSpam)
    {
        this.isSpam = isSpam;
    }

    public boolean getIsApproved()
    {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved)
    {
        this.isApproved = isApproved;
    }

    public Integer getDislikes()
    {
        return dislikes;
    }

    public void setDislikes(Integer dislikes)
    {
        this.dislikes = dislikes;
    }

    public String getRawMessage()
    {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage)
    {
        this.rawMessage = rawMessage;
    }

    public boolean getIsFlagged()
    {
        return isFlagged;
    }

    public void setIsFlagged(boolean isFlagged)
    {
        this.isFlagged = isFlagged;
    }

    public Integer getPoints()
    {
        return points;
    }

    public void setPoints(Integer points)
    {
        this.points = points;
    }

    public BillBuzzAuthor getAuthor()
    {
        return author;
    }

    public void setAuthor(BillBuzzAuthor author)
    {
        this.author = author;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parent)
    {
        this.parentId = parent;
    }

    public String getForumId()
    {
        return forumId;
    }

    public void setForumId(String forumId)
    {
        this.forumId = forumId;
    }

    public boolean isJuliaFlagged()
    {
        return juliaFlagged;
    }

    public void setJuliaFlagged(boolean juliaFlagged)
    {
        this.juliaFlagged = juliaFlagged;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAuthorId()
    {
        return authorId;
    }

    public void setAuthorId(String authorId)
    {
        this.authorId = authorId;
    }

    public BillBuzzThread getThread()
    {
        return thread;
    }

    public void setThread(BillBuzzThread thread)
    {
        this.thread = thread;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public BillBuzzPost getParent()
    {
        return parent;
    }

    public void setParent(BillBuzzPost parent)
    {
        this.parent = parent;
    }
}
