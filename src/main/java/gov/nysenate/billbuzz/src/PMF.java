package gov.nysenate.billbuzz.src;

import java.util.Collection;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

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
	
	
	
	public static Object getDetachedObject(Class<?> objClass, String key, String value) {
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
	
	public static Object getObject(PersistenceManager pm, Class<?> objClass, String key, String value) {
		
		Extent<?> e = pm.getExtent(objClass,true);
		
		Query q = pm.newQuery(e, key + "==\"" + value + "\"");
		
		
		Collection<?> c = (Collection<?>)q.execute();
		
		if(c.isEmpty()) {
			return null;
		}
		
		return c.iterator().next();
		
	}
	
	
	public static Collection<?> getDetachedObjects(Class<?> objClass) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx =pm.currentTransaction();
		
		Collection<?> c = null;
		
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
	
	
	public static Collection<?> getObjects(PersistenceManager pm, Class<?> objClass) {
		
		Extent<?> e = pm.getExtent(objClass);
		
		Query q = pm.newQuery(e);
		
		return (Collection<?>)q.execute();
		
	}
	
	
	
	public static boolean persistObject(Object... objs) {
		
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();
			
			for(Object o:objs) {
				pm.makePersistent(o);
			}
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
			
		return true;
	}
	
	public static boolean deleteObjectById(Class<?> clazz, String key, String id) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		Object o = null;
		
		try {
			tx.begin();
			
			o = PMF.getObject(pm, clazz, key, id);
			
			pm.deletePersistent(o);
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		if(o == null) {
			return false;
		}
		
		return true;
	}
	
	public static boolean deleteObjects(Class<?>[] clazz, String[] key, String[] id) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		Object o = null;
		
		try {
			tx.begin();
			
			for(int i = 0; i < clazz.length; i++) {
				o = PMF.getObject(pm, clazz[i], key[i], id[i]);
				pm.deletePersistent(o);
			}
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		if(o == null) {
			return false;
		}
		
		return true;
	}
	
	public static void deleteObjects(Class<?> clazz) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
				
		try {
			tx.begin();
			
			Collection<?> objs = getObjects(pm, clazz);
			
			for(Object o:objs) {
				pm.deletePersistent(o);
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
}
