package gov.nysenate.billbuzz.disqus.models;

public class Cursor
{
    private String prev;
    private String next;
    private boolean hasNext;
    private boolean hasPrev;
    private Integer total;
    private String id;
    private boolean more;

    public Cursor() {}

    public boolean hasMore()
    {
        return more;
    }

    public void setMore(boolean more)
    {
        this.more = more;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }

    public boolean getHasPrev()
    {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev)
    {
        this.hasPrev = hasPrev;
    }

    public boolean getHasNext()
    {
        return hasNext;
    }

    public void setHasNext(boolean hasNext)
    {
        this.hasNext = hasNext;
    }

    public String getNext()
    {
        return next;
    }

    public void setNext(String next)
    {
        this.next = next;
    }

    public String getPrev()
    {
        return prev;
    }

    public void setPrev(String prev)
    {
        this.prev = prev;
    }
}
