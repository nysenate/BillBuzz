package gov.nysenate.billbuzz.disqus;


import java.util.List;

public class DisqusListResponse<T extends DisqusPrimaryObject>
{
    private DisqusCursor cursor;
    private Integer code;
    private List<T> response;
    private String method;
    private String[] args = new String[]{};

    public DisqusListResponse() {}

    public DisqusCursor getCursor() {
        return cursor;
    }

    public void setCursor(DisqusCursor cursor) {
        this.cursor = cursor;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<T> getResponse() {
        return response;
    }

    public void setResponse(List<T> response) {
        this.response = response;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String[] getArgs()
    {
        return args;
    }

    public void setArgs(String[] args)
    {
        this.args = args;
    }
}
