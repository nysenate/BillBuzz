package gov.nysenate.billbuzz.disqus;

public class DisqusForumSettings {
    private boolean allowAnonPost;
    private boolean audienceSyncEnabled;
    private boolean allowMedia;
    private boolean hasReactions;
    private boolean ssoRequired;
    private boolean backplaneEnabled;

    public DisqusForumSettings() {}

    public boolean hasAllowAnonPost() {
        return allowAnonPost;
    }

    public void setAllowAnonPost(boolean allowAnonPost) {
        this.allowAnonPost = allowAnonPost;
    }

    public boolean hasAudienceSyncEnabled() {
        return audienceSyncEnabled;
    }

    public void setAudienceSyncEnabled(boolean audienceSyncEnabled) {
        this.audienceSyncEnabled = audienceSyncEnabled;
    }

    public boolean hasAllowMedia() {
        return allowMedia;
    }

    public void setAllowMedia(boolean allowMedia) {
        this.allowMedia = allowMedia;
    }

    public boolean hasHasReactions() {
        return hasReactions;
    }

    public void setHasReactions(boolean hasReactions) {
        this.hasReactions = hasReactions;
    }

    public boolean hasSsoRequired() {
        return ssoRequired;
    }

    public void setSsoRequired(boolean ssoRequired) {
        this.ssoRequired = ssoRequired;
    }

    public boolean hasBackplaneEnabled() {
        return backplaneEnabled;
    }

    public void setBackplaneEnabled(boolean backplaneEnabled) {
        this.backplaneEnabled = backplaneEnabled;
    }
}