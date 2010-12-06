package gov.nysenate.billbuzz.persist;


import java.util.Collection;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;



@SuppressWarnings("unchecked")
public class PMF {
	
	private static PersistenceManagerFactory pmf = null;
	
	
	public PMF() {
		
	}
		
	public synchronized static PersistenceManager getPersistenceManager() {
		if(pmf == null) {
			
			pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		}
		
		return pmf.getPersistenceManager();
		
	}
		
	public static void saveUnmoderated(List<String> lst) {
		PersistenceManager pm = PMF.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();
			
			List<Unmoderated> tlst = (List<Unmoderated>)PMF.getObjects(pm, Unmoderated.class);
			
			if(tlst.size() == 0) {
				pm.makePersistent(new Unmoderated(lst));
			}
			else {
				Unmoderated u = tlst.iterator().next();
				u.setUnmoderated(lst);
			}
			
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
	
	}
	
	
	
	public static void setLastUse(String lastUse) {
		
		PersistenceManager pm = PMF.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();
			
			List<LastUse> lst = (List<LastUse>)PMF.getObjects(pm, LastUse.class);
			
			if(lst.size() == 0) {
				pm.makePersistent(new LastUse(lastUse));
			}
			else {
				LastUse lu = lst.iterator().next();
				
				lu.setLastUse(lastUse);
			}
			
			tx.commit();
		}
		finally {
			if(tx.isActive()){
				tx.rollback();				
			}
			pm.close();
		}
		
	}
	
	
	public static Object getDetachedObject(Class objClass, String key, String value) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		Object o = null;
		
		try {
			tx.begin();
			
			o = getObject(pm,objClass,key,value);
			
			o = pm.detachCopy(o);
						
			tx.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			
			pm.close();
			
			
		}
		
		
		return o;
	}	
	
	public static Object getObject(PersistenceManager pm, Class objClass, String key, String value) {
		
		Extent e = pm.getExtent(objClass,true);
		
		Query q = pm.newQuery(e, key + "==\"" + value + "\"");
		
		
		Collection c = (Collection)q.execute();
		
		if(c.isEmpty()) {
			return null;
		}
		
		return c.iterator().next();
		
	}
	
	
	public static Collection getDetachedObjects(Class objClass) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx =pm.currentTransaction();
		
		Collection c = null;
		
		try {
			tx.begin();
			
			c = getObjects(pm, objClass);
			
			c = pm.detachCopyAll(c);
			
			tx.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(tx.isActive()) {
				
				tx.rollback();
			}
			pm.close();
		}
		
		
		
		return c;
		
	}
	
	
	public static Collection getObjects(PersistenceManager pm, Class objClass) {
		
		Extent e = pm.getExtent(objClass);
		
		Query q = pm.newQuery(e);
		
		return (Collection)q.execute();
		
	}
	
	
	
	public static boolean persistObject(Object o) {
		
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();

			pm.makePersistent(o);
			
			tx.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
		finally {
			
			if(tx.isActive()) {
				
				tx.rollback();
				
			}
			pm.close();
		}
			
		return false;
	}
}

