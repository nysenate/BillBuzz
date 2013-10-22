package gov.nysenate.billbuzz.disqus;


public class DisqusObjectResponse <T extends DisqusPrimaryObject>
{;
    private Integer code;
    private T response;
    private String method;
    private String[] args = new String[]{};

    public DisqusObjectResponse() {}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
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
