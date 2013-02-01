package gov.nysenate.billbuzz.persist;

import java.util.Calendar;
import java.util.List;

@SuppressWarnings("unchecked")
public class Controller {

	public static void saveUnmoderated(List<String> lst) {
		
		PMF.saveUnmoderated(lst);
		
	}	
	
	public static void setLastUse(String lastUse) {
		
		PMF.setLastUse(lastUse);	
		
	}
	
	public static List<Senator> getSenators() {
		
		return (List<Senator>) PMF.getDetachedObjects(Senator.class);	
		
	}
	
	public static List<User> getUsers() {
		
		return (List<User>) PMF.getDetachedObjects(User.class);	
		
	}
	
	public static String formatDate(int i) {
		
		return ((i<10) ? "0" + i : "" + i);
		
	}
	
	public static String getLastUse() {
		
		String ret = null;		
		
		List<LastUse> lst = (List<LastUse>) PMF.getDetachedObjects(LastUse.class);
		
		Calendar c = Calendar.getInstance();
		
		String date = (c.get(Calendar.YEAR) + "-"
				+ formatDate(c.get(Calendar.MONTH)+1) + "-"
				+ formatDate(c.get(Calendar.DAY_OF_MONTH)) + "T"
				+ formatDate(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ formatDate(c.get(Calendar.MINUTE)));
		
		if(lst.size() == 0) {
			
			ret = date;			
		}
		else {
			
			LastUse lu = lst.iterator().next();
			
			ret = lu.getLastUse();			
		}
		
		
		
		setLastUse(date);
	
		return ret;
	}
	

	public static List<String> getUnmoderated() {
		Unmoderated u = null;
		
		List<Unmoderated> lst = (List<Unmoderated>)PMF.getDetachedObjects(Unmoderated.class);
		
		if(lst.size() == 0) {
			u = new Unmoderated();
		}
		else {
			u = lst.iterator().next();			
		}
		
		return u.getUnmoderated();
	}
	
}
