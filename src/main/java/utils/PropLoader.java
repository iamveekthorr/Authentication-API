/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor Okonkwo
 */
public class PropLoader {

    private static InputStream reader;
    public static Properties properties = new Properties();

    public static Properties loadPropertiesFile() {
        try {
            reader = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("config.properties");
            properties.load(reader);
            return properties;
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Logged", ex);
        }
        return null;
    }
}
