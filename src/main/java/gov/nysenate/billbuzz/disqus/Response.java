package gov.nysenate.billbuzz.disqus;

import gov.nysenate.billbuzz.disqus.models.BaseObject;
import gov.nysenate.billbuzz.disqus.models.Cursor;

import java.util.List;

public class Response<T extends BaseObject> {
    private Cursor cursor;
    private Integer code;
    private List<T> response;
    private String method;
    private String[] args = new String[]{};

    public Response() {}

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
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
