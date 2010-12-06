package comment.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import comment.model.persist.DisqusComment;
import comment.model.persist.DisqusThread;

public class ObjectHelper {
	
	public static Date searchDate(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			return df.parse(date);
		}
		catch (ParseException e) {
			return null;
		}		
	}

	public static Date dateFromString(String date) {
		date = date.split(" -")[0];
		
		DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
		
		try {
			return df.parse(date);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String billFromUrl(String url) {
		Pattern p = Pattern.compile("[a-zA-Z]\\d+[a-zA-Z]?");
		
		Matcher m = p.matcher(url);
		
		if(m.find()) {
			return url.substring(m.start(), m.end());
		}
		
		return null;
		
	}
	
	public static List<DisqusComment> mergeComments(List<DisqusComment> lst1, List<DisqusComment> lst2) {
		
		Map<String,DisqusComment> m1 = mapFromList(lst1);
		
		for(DisqusComment c:lst2) {
			
			if(!m1.containsKey(c.getIp() + c.getDate().toString())) {
				
				lst1.add(c);				
				
			}
			
		}
		
		return lst1;		
		
	}
	
	public static Map<String,DisqusComment> mapFromList(List<DisqusComment> list) {
		Map<String,DisqusComment> map = new HashMap<String,DisqusComment>();
		
		for(DisqusComment c: list) {
			
			String key = c.getIp() + c.getDate().toString();
			
			map.put(key, c);
			
		}
		
		return map;
	}
	
	public static List<DisqusComment> filterComments(List<DisqusComment> dclst,String d1, String d2) {
		
		List<DisqusComment> ret = new ArrayList<DisqusComment>();
		
		Date nd1 = ObjectHelper.searchDate(d1);
		Date nd2 = null;
		if(d2 == null) {
			nd2 = new Date();
		}
		else {
			nd2 = ObjectHelper.searchDate(d2);
		}
		
		for(DisqusComment dc:dclst) {
			Date d = dc.getDate();
			
			
			if((d.after(nd1) && d.before(nd2))) {
				ret.add(dc);
			}
			
		}		
		
		return ret;
	}
	
	
	public static List<DisqusThread> postsByDate(Collection<DisqusThread> lst, String d1, String d2) {
	
		DisqusThread[] doar = new DisqusThread[lst.size()];
		
		int i = 0;
		for(DisqusThread dt:lst) {
			DisqusThread t = dt;
			t.setComments(filterComments(dt.getComments(), d1, d2));
			doar[i] = t;
			i++;
		}
		
		
		doar = sortByComments(doar, 0, doar.length-1);
		
		return (List<DisqusThread>)arrayToList(doar);
		
	}
	
	
	
	public static DisqusThread[] sortByComments(DisqusThread[] sos, int low, int high) {		
		if(high > low) {
			int partitionPivot = (int)(Math.random()*(high-low) + low);
			int newPivot = partition(sos, low, high, partitionPivot);
			sortByComments(sos, low, newPivot-1);
			sortByComments(sos, newPivot+1,high);			
		}		
		return sos;
	}
	
	private static int partition(DisqusThread[] sos, int low, int high, int pivot) {
		DisqusThread so = sos[pivot];
		
		swap(sos, pivot, high);
		
		int index = low;
		
		for(int i = low; i < high; i++) {
			if((sos[i]).getComments().size() > (so.getComments().size())) {
				swap(sos, i, index);
				index++;
			}
		}
		
		swap(sos, high, index);
		
		return index;
	}
	
	private static void swap(DisqusThread[] sos, int i, int j) {
		DisqusThread temp = sos[i];
		sos[i] = sos[j];
		sos[j] = temp;
	}
	
	public static Collection<? extends Object> arrayToList(Object[] lst) {
		List<Object> objs = new ArrayList<Object>();
		
		for(Object o:lst) {
			objs.add(o);
		}		
		
		return objs;
	}
}








