package gov.nysenate.billbuzz.util;

import gov.nysenate.util.Config;
import gov.nysenate.util.DB;
import gov.nysenate.util.Mailer;
import gov.nysenate.util.listener.NYSenateConfigurationListener;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

public class Application
{
  public static Logger logger = Logger.getLogger(Application.class);

  /** Static factory instance */
  protected static Application appInstance = new Application();

  /** Default values */
  protected static final String prodPropertyFileName = "app.properties";
  protected static final String testPropertyFileName = "test.app.properties";

  /** Dependency instances */
  protected NYSenateConfigurationListener configurationListener;
  protected Config config;
  protected DB db;

  /**
   * Public access call to build()
   * @return boolean - If true then build succeeded
   */
  public static boolean bootstrap()
  {
    return bootstrap(prodPropertyFileName);
  }

  public static boolean bootstrap(String propertyFileName)
  {
    try {
      logger.info("------------------------------");
      logger.info("  INITIALIZING BILLBUZZ   ");
      logger.info("------------------------------");

      appInstance.config = new Config(propertyFileName);
      appInstance.db = new DB(appInstance.config, "mysqldb");
      // Set the location of the mailing templates, and disable logging.
      Velocity.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, appInstance.config.getValue("mailer.template_dir"));
      Velocity.setProperty(RuntimeConstants.RUNTIME_LOG, "/tmp/velocity.log");
      Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
      Velocity.init();
      return true;
    }
    catch (Exception ex) {
      logger.fatal("An exception occurred while building dependencies");
      logger.fatal(ex.getMessage(), ex);
    }
    return false;
  }

  public static void shutdown() throws IOException
  {
    try {
      appInstance.db.getDataSource().purge();
      appInstance.db.getDataSource().close(true);
    }
    catch(Exception e) {
      logger.info("Failed to purge and close data connections.");
    }
  }

  public static Config getConfig()
  {
    return appInstance.config;
  }

  public static DB getDB()
  {
    return appInstance.db;
  }
}
