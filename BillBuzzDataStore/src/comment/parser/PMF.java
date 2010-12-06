package comment.parser;

import java.util.Collection;
import java.util.Date;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import comment.model.persist.DisqusThread;

@SuppressWarnings("unchecked")
public class PMF {

	private static PersistenceManagerFactory pmf = null;
	
	
	
	public PMF () {
		
	}
	
	public static PersistenceManager getPersistenceManager() {
		if(pmf == null) {
			pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		}
		
		return pmf.getPersistenceManager();
	}
	
	public static Collection<DisqusThread> threadByDate(String d1, String d2) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		Date nd1 = ObjectHelper.searchDate(d1);
		Date nd2 = null;
		if(d2 == null) {
			nd2 = new Date();
		}
		else {
			nd2 = ObjectHelper.searchDate(d2);
		}		
		
		Collection<DisqusThread> c = null;
		
		try {
			tx.begin();
			
			Query q = pm.newQuery(DisqusThread.class, "last >= nd1 && last <= nd2");
			
			q.declareImports("import java.util.Date");
			q.declareParameters("java.util.Date nd1, java.util.Date nd2");
			q.setOrdering("last ascending");
			
			c = (Collection)q.execute(nd1, nd2);
			
			c = pm.detachCopyAll(c);
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		return c;
	}
	
	
	public static void persistDisqusThread(DisqusThread dt) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();
			
			DisqusThread temp = (DisqusThread)getObject(pm, DisqusThread.class, "bill", dt.getBill());
			
			if(temp != null) {
				System.out.println("merging comments: " + dt.getComments().size() + " : " + temp.getComments().size());
				temp.setComments(ObjectHelper.mergeComments(dt.getComments(), temp.getComments()));
				System.out.println("--new size: " + temp.getComments().size() + "\n");
			}
			else {
				pm.makePersistent(dt);
			}
			
			
			tx.commit();			
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.flush();
			pm.close();
		}
	}
	
	
	public static DisqusThread getDisqusThread(String bill) {
		return (DisqusThread) getDetachedObject(DisqusThread.class, "bill", bill);
	}
	
	
	public static void persistObject(Object o) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();
			
			pm.makePersistent(o);
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	
	public static Collection getObjects(PersistenceManager pm, Class objClass) {
		
		Query q = pm.newQuery(objClass);
		
		return (Collection)q.execute();
		
	}
	
	public static Collection getDetachedObjects(Class objClass) {
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		Collection c = null;
		
		try {
			tx.begin();
			
			c = getObjects(pm, objClass);
			
			c = pm.detachCopyAll(c);
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		return c;
	}
	
	public static Object getObject(PersistenceManager pm, Class objClass, String key, String val) {
				
		Collection c = null;
		
		Query q = pm.newQuery(objClass, key + " ==\"" + val + "\"");
		
		c = (Collection)q.execute();
		
		if(!c.isEmpty()) {
			return c.iterator().next();
		}
		
		return null;
		
	}
	
	public static Object getDetachedObject(Class objClass, String key, String val) {
		
		PersistenceManager pm = getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		Object o = null;
		
		try {
			tx.begin();
			
			
			o = getObject(pm, objClass, key, val);
			
			if(o != null) {
				o = pm.detachCopy(o);
			}
			
			tx.commit();
		}
		finally {
			if(tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		
		return o;
		
	}
	
}











