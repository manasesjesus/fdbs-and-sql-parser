package fed.fdbs;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * This class contains the default information of the databases used on the FDBS.
 *
 */
public class FedConfig {
    // Credentials
    private static String user;
    private static String passwd;

    // Databases
    private static String db1;
    private static String db2;
    private static String db3;
    
    // Directory containing the SQL scripts
    public static String scripts;
    
    // Number of rows that should be fetched
    public static int fetchsize; 
    
    // Parser check
    public static boolean validate;
    
    // Default configuration file
    private static final String PROPERTIES = "config/fdbs.properties";
    
    // Name of the log file
    public static String logfile;
    
    
    public static void loadProperties () {
        try {
            
            Properties props = new Properties();
            Reader config = new FileReader(PROPERTIES);
            
            props.load(config);
            user   = props.getProperty("user");
            passwd = props.getProperty("passwd");
            
            db1 = props.getProperty("db1");
            db2 = props.getProperty("db2");
            db3 = props.getProperty("db3");
            
            scripts   = props.getProperty("scripts");
            validate  = Boolean.parseBoolean(props.getProperty("validate"));
            
            logfile = props.getProperty("logfile");
            logfile = logfile == null ? "fdbs.log" : logfile;
            System.setProperty("user.logfile", logfile);
            
            fetchsize = Integer.parseInt(props.getProperty("fetchsize") == null ? 
                                         "0" : props.getProperty("fetchsize").trim());
            
        } catch (IOException e) {
            System.out.println("[ERROR] Unable to locate the configuration file: " + PROPERTIES);
            System.exit(007); 
        } catch (NumberFormatException e) {
            // Wrong value for fetchsize in the properties file. Setting it to default.
            fetchsize = 0;
        } 
    }
    
    /*   Getters & Setters   */
    public static String getDb1Name () {
        return db1 != null ? db1.substring(db1.lastIndexOf(":") + 1) : null;
    }
    
    public static String getDb2Name () {
        return db2 != null ? db2.substring(db2.lastIndexOf(":") + 1) : null;
    }
    
    public static String getDb3Name () {
        return db3 != null ? db3.substring(db3.lastIndexOf(":") + 1) : null;
    }
    
    public static String getUser () {
        return user;
    }
    
    public static void setUser (String user) {
        FedConfig.user = user;
    }
    
    public static String getPasswd () {
        return passwd;
    }
    
    public static void setPasswd (String passwd) {
        FedConfig.passwd = passwd;
    }

    public static String getDb1 () {
        return db1;
    }

    public static void setDb1 (String db1) {
        FedConfig.db1 = db1;
    }

    public static String getDb2 () {
        return db2;
    }

    public static void setDb2 (String db2) {
        FedConfig.db2 = db2;
    }

    public static String getDb3 () {
        return db3;
    }

    public static void setDb3 (String db3) {
        FedConfig.db3 = db3;
    }
}
