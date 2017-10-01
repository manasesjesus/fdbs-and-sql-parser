package fed.fdbs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to log trace, info and error messages to the console and to file.
 * It uses log4j2 API and it is configured in the log4j2.xml resource file.
 * It also has a method to log the results of a FedResultSet object.
 *
 */
public class FedLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(FedLogger.class);
	private static boolean echo = true;

    public static void trace (String message) {
        if (echo) {
            LOGGER.trace(message);
        }
	}
	
	public static void info (String message) {
	    if (echo) {
	        LOGGER.info(message);
	    }
	}
	
	public static void error (String message) {
        LOGGER.error(message);
    }
	
	/**
	 * @param mode
	 *     OFF - Turns off the logger levels TRACE and INFO
	 *     other value - turns it on
	 */
	public static void setEcho (String mode) {
        echo = !mode.equals("OFF");
        LOGGER.trace("TRACE and INFO Logger levels has been " + (echo ? "enabled" : "disabled"));
    }
	
	public static void printFedResults (FedResultSet frs) throws FedException {
	    printFedResults(frs, 21);
	}
	
	public static void printFedResults (FedResultSet frs, final int COLUMN_WIDTH) throws FedException {
        String message = "";
	    int columns = frs.getColumnCount();
        
        if (!frs.next()) { 
            message = "0 rows selected";
        } else {
            for (int i = 1; i <= columns; i++) {
                String cn = frs.getColumnName(i).replace("#$#", "(*)")
                               .replace("_$_", "(").replace("$#$", ".").replace("_#_", ")");
                
                message += cn;
                for (int k = cn.length(); k < COLUMN_WIDTH; k++) message += " ";
            }
            message += "\n";

            for (int i = 1; i <= columns; i++) {
                for (int k = 1; k < COLUMN_WIDTH; k++) message += "=";
                message += " ";
            }
            message += "\n";
            
            int rows = 0;
            do {
                for (int i = 1; i <= columns; i++) {
                    Object val = frs.getValue(i); 
                    String cn  = val != null ? val.toString(): "(null)"; 
                    
                    message += cn;
                    for (int k = cn.length(); k < COLUMN_WIDTH; k++) message += " ";
                }
                message += "\n";
                rows++;
            } while (frs.next());
            message = rows + " rows selected\n\n" + message;
        }
	    
        LOGGER.debug(message);
    }
}
