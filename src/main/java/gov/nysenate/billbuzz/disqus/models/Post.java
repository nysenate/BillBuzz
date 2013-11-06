package gov.nysenate.billbuzz.disqus.models;


import java.util.ArrayList;
import java.util.Date;

public class Post extends BaseObject
{
    private boolean juliaFlagged;
    private String forum;
    private String parent;
    private Author author;
    private ArrayList<Media> media;
    private Integer points;
    private boolean isFlagged;
    private Integer dislikes;
    private String raw_message;
    private boolean isApproved;
    private boolean isSpam;
    private Integer userScore;
    private String thread;
    private Integer numReports;
    private Date createdAt;
    private boolean isEdited;
    private String message;
    private boolean isHighlighted;
    private boolean isDeleted;
    private Integer likes;

    public Post() {}

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

    public void setHighlighted(boolean isHighlighted)
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

    public String getThread()
    {
        return thread;
    }

    public void setThread(String thread)
    {
        this.thread = thread;
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

    public boolean isApproved()
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

    public String getRaw_message()
    {
        return raw_message;
    }

    public void setRaw_message(String rawMessage)
    {
        this.raw_message = rawMessage;
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

    public ArrayList<Media> getMedia()
    {
        return media;
    }

    public void setMedia(ArrayList<Media> media)
    {
        this.media = media;
    }

    public Author getAuthor()
    {
        return author;
    }

    public void setAuthor(Author author)
    {
        this.author = author;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }

    public String getForum()
    {
        return forum;
    }

    public void setForum(String forum)
    {
        this.forum = forum;
    }

    public boolean isJuliaFlagged()
    {
        return juliaFlagged;
    }

    public void setJuliaFlagged(boolean juliaFlagged)
    {
        this.juliaFlagged = juliaFlagged;
    }

}
