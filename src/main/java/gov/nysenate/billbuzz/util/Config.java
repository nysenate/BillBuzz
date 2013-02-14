package gov.nysenate.billbuzz.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;


public class Config {
    private static final Logger logger = Logger.getLogger(Config.class);

    /** Pattern to replace {{variables}} used in the property file. */
    private static final Pattern variablePattern = Pattern.compile("\\{\\{(.*?)\\}\\}");

    /** Provides access to the property file */
    private static PropertiesConfiguration config;

    /** Load the given property file and set the listener to track file changes. */
    private static void load_configuration() throws ConfigurationException
    {
        Config.config = new PropertiesConfiguration("app.properties");
        Config.config.setReloadingStrategy(new FileChangedReloadingStrategy());
        logger.debug("Loaded config for " + Config.config.getPath());
    }

    /**
     * Reads property file and returns value for given key. If the value contains
     * a {{key}} it will be replaced with the value of that key.
     *
     * @param key - Property key to look up the value for.
     * @return String - Value of property or empty string if not found.
     */
    public static String getValue(String key)
    {
        try {
            if (config == null) {
                load_configuration();
            }
            String value = Config.config.getString(key, "");
            logger.trace(String.format("Reading config[%s] = %s", key, value));
            String resolvedValue = resolveVariables(value);
            if (value != resolvedValue)
            {
                value = resolvedValue;
                Config.config.setProperty(key, value);
            }
            return value;
        }
        catch (ConfigurationException e) {
            logger.error("Could not load app.properties", e);
            return null;
        }
    }

    /**
     * Resolves variables in the property value.
     * @param value
     * @return
     */
    private static String resolveVariables(String value)
    {
        Matcher variableMatcher = variablePattern.matcher(value);
        while(variableMatcher.find()) {
            String variable = variableMatcher.group(1);
            String replacement = getValue(variable);
            value = value.replace("{{"+variable+"}}", replacement);
            variableMatcher = variablePattern.matcher(value);
        }
        return value;
    }
}
