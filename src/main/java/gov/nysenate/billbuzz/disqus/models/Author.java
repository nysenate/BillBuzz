package gov.nysenate.billbuzz.disqus.models;

import java.util.Date;



public class Author extends BaseObject
{
    private String username;
    private String about;
    private String name;
    private String url;
    private boolean isAnonymous;
    private double rep;
    private double reputation;
    private boolean isFollowing;
    private boolean isFollowedBy;
    private String profileUrl;
    private String emailHash;
    private String location;
    private boolean isPrimary;
    private Date joinedAt;
    private Avatar avatar;

    public Avatar getAvatar()
    {
        return avatar;
    }
    public void setAvatar(Avatar avatar)
    {
        this.avatar = avatar;
    }
    public Date getJoinedAt()
    {
        return joinedAt;
    }
    public void setJoinedAt(Date joinedAt)
    {
        this.joinedAt = joinedAt;
    }
    public boolean getIsPrimary()
    {
        return isPrimary;
    }
    public void setIsPrimary(boolean isPrimary)
    {
        this.isPrimary = isPrimary;
    }
    public String getLocation()
    {
        return location;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }
    public String getEmailHash()
    {
        return emailHash;
    }
    public void setEmailHash(String emailHash)
    {
        this.emailHash = emailHash;
    }
    public boolean getIsFollowing()
    {
        return isFollowing;
    }
    public void setIsFollowing(boolean isFollowing)
    {
        this.isFollowing = isFollowing;
    }
    public String getProfileUrl()
    {
        return profileUrl;
    }
    public void setProfileUrl(String profileUrl)
    {
        this.profileUrl = profileUrl;
    }
    public boolean getIsFollowedBy()
    {
        return isFollowedBy;
    }
    public void setIsFollowedBy(boolean isFollowedBy)
    {
        this.isFollowedBy = isFollowedBy;
    }
    public double getRep()
    {
        return rep;
    }
    public void setRep(double reputation)
    {
        this.rep = reputation;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getAbout()
    {
        return about;
    }
    public void setAbout(String about)
    {
        this.about = about;
    }
    public boolean getIsAnonymous()
    {
        return isAnonymous;
    }
    public void setIsAnonymous(boolean isAnonymous)
    {
        this.isAnonymous = isAnonymous;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public double getReputation()
    {
        return reputation;
    }
    public void setReputation(double reputation)
    {
        this.reputation = reputation;
    }

}
