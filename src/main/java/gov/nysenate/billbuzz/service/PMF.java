package gov.nysenate.billbuzz.service;

import gov.nysenate.billbuzz.ObjectHelper;
import gov.nysenate.billbuzz.model.persist.DisqusThread;
import gov.nysenate.billbuzz.model.persist.LastUse;
import gov.nysenate.billbuzz.model.persist.Unmoderated;

import java.util.Collection;
import java.util.Date;
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

    public synchronized static PersistenceManager getPersistenceManager()
    {
        if(pmf == null) {
            pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
        }
        return pmf.getPersistenceManager();
    }

    public static Object getDetachedObject(Class<?> objClass, String key, String value)
    {
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

    public static Object getObject(PersistenceManager pm, Class<?> objClass, String key, String value)
    {
        Extent<?> e = pm.getExtent(objClass,true);
        Query q = pm.newQuery(e, key + "==\"" + value + "\"");
        Collection<?> c = (Collection<?>)q.execute();
        if(c.isEmpty()) {
            return null;
        }
        return c.iterator().next();
    }

    public static Collection<?> getDetachedObjects(Class<?> objClass)
    {
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

    public static Collection<?> getObjects(PersistenceManager pm, Class<?> objClass)
    {
        Extent<?> e = pm.getExtent(objClass);
        Query q = pm.newQuery(e);
        return (Collection<?>)q.execute();
    }

    public static boolean persistObject(Object... objs)
    {
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

    public static boolean deleteObjectById(Class<?> clazz, String key, String id)
    {
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

    public static boolean deleteObjects(Class<?>[] clazz, String[] key, String[] id)
    {
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

    public static void deleteObjects(Class<?> clazz)
    {
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

    public static void saveUnmoderated(List<String> lst)
    {
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

    public static void setLastUse(String lastUse)
    {
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

    public static Collection<DisqusThread> threadByDate(String d1, String d2)
    {
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

            c = (Collection<DisqusThread>)q.execute(nd1, nd2);
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


    public static void persistDisqusThread(DisqusThread dt)
    {
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

    public static DisqusThread getDisqusThread(String bill)
    {
        return (DisqusThread) getDetachedObject(DisqusThread.class, "bill", bill);
    }
}
