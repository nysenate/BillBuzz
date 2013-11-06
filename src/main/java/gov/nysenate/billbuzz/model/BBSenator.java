package gov.nysenate.billbuzz.model;


import java.util.ArrayList;
import java.util.List;

public class BBSenator
{
	private String name;
	private List<ThreadDescription> threads;

	public BBSenator(String name)
	{
	    this.name = name;
	    this.threads = new ArrayList<ThreadDescription>();
	}

	public String getName()
	{
        return name;
    }

	public void setName(String name)
	{
		this.name = name;
	}

    public List<ThreadDescription> getThreads()
    {
        return threads;
    }

	public void setThreads(List<ThreadDescription> threads)
	{
		this.threads = threads;
	}

	public void addThread(ThreadDescription td)
	{
		threads.add(td);
	}
}
