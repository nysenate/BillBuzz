package bbsignup.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jared Williams
 *	This class is the property loader and accessor.  It allows the properties file
 *	to be accessed statically
 */
public class Resource {
	
	private static String resource = "app.properties";
	private static InputStream INPUT;
	private static Properties properties;
	
	/*
	 * If current context is servlet grab resource stream and load props, otherwise
	 * use typical file reader
	 * 
	 */
	private static Properties load() {
		try{
			if(properties == null) {
				properties = new Properties();
				init();
				properties.load(INPUT);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			properties = null;
		}
		return properties;
	}
	
	public static void init() throws FileNotFoundException {
		INPUT = new FileInputStream(new File(resource));
	}
	
	public static String get(String key) {
		return load().getProperty(key);
	}
}
