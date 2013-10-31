package gov.nysenate.billbuzz.model;

import java.util.ArrayList;
import java.util.Date;

public class BillBuzzThread implements Comparable<BillBuzzThread>
{
    private String id;
    private String billId;
    private String sponsor;

    private String forumId;

    private String authorId;
    private BillBuzzAuthor author;

    private boolean isDeleted;
    private boolean isClosed;
    private boolean userSubscription;

    private String category;
    private String feed;
    private String link;
    private String slug;

    private String title;
    private Integer posts;
    private String message;
    private ArrayList<String> identifiers;

    private Integer likes;
    private Integer dislikes;
    private Integer userScore;

    private Date createdAt;
    private Date updatedAt;

    public BillBuzzThread()
    {

    }

    public boolean getIsDeleted()
    {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted)
    {
        this.isDeleted = deleted;
    }

    public boolean getIsClosed()
    {
        return isClosed;
    }

    public void setIsClosed(boolean closed)
    {
        this.isClosed = closed;
    }

    public boolean getUserSubscription()
    {
        return userSubscription;
    }

    public void setUserSubscription(boolean userSubscription)
    {
        this.userSubscription = userSubscription;
    }

    public String getFeed()
    {
        return feed;
    }

    public void setFeed(String feed)
    {
        this.feed = feed;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getForumId()
    {
        return forumId;
    }

    public void setForumId(String forumId)
    {
        this.forumId = forumId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getPosts()
    {
        return posts;
    }

    public void setPosts(Integer posts)
    {
        this.posts = posts;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers(ArrayList<String> identifiers)
    {
        this.identifiers = identifiers;
    }

    public Integer getLikes()
    {
        return likes;
    }

    public void setLikes(Integer likes)
    {
        this.likes = likes;
    }

    public Integer getDislikes()
    {
        return dislikes;
    }

    public void setDislikes(Integer dislikes)
    {
        this.dislikes = dislikes;
    }

    public String getAuthorId()
    {
        return authorId;
    }

    public void setAuthorId(String authorId)
    {
        this.authorId = authorId;
    }

    public Integer getUserScore()
    {
        return userScore;
    }

    public void setUserScore(Integer userScore)
    {
        this.userScore = userScore;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public BillBuzzAuthor getAuthor()
    {
        return author;
    }

    public void setAuthor(BillBuzzAuthor author)
    {
        this.author = author;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getBillId()
    {
        return billId;
    }

    public void setBillId(String billId)
    {
        this.billId = billId;
    }

    public String getSponsor()
    {
        return sponsor;
    }

    public void setSponsor(String sponsor)
    {
        this.sponsor = sponsor;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    @Override
    public int compareTo(BillBuzzThread other)
    {
        return this.getId().compareTo(other.getId());
    }
}
