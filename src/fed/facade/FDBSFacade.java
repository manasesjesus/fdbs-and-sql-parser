package fed.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fed.fdbs.FedConfig;
import fed.fdbs.FedConnection;
import fed.fdbs.FedException;
import fed.fdbs.FedFileReader;
import fed.fdbs.FedLogger;
import fed.fdbs.FedPseudoDriver;
import fed.fdbs.FedResultSet;
import fed.fdbs.FedShutdown;
import fed.fdbs.FedStatement;
import fed.parser.ParseException;
import fed.parser.TokenMgrError;

/**
 * The main entry point of the system and it implements the Facade design pattern. A user writing 
 * a java application only has to define the configuration properties file (see FedConfig) and the 
 * directory containing all the SQL scripts to be executed.
 *
 */
public class FDBSFacade {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new FedShutdown()));
    }
    
    public void start () throws IOException {

        // Load system configuration or terminate if fails to load
        FedConfig.loadProperties(); 

        System.out.println("Application started. Loading APIs...");
        
        File[] files = new File(FedConfig.scripts).listFiles();
        List<String> sql_files = new ArrayList<>(); 
        
        if (files == null) {
            throw new IOException("[ERROR] Can't read \"" + FedConfig.scripts + "\" directory.");
        }
        
        FedLogger.trace("Obtaining SQL test files from \"" + FedConfig.scripts + "\"...");
        for (File file : files) {
            if (file.isFile() && file.getName().toUpperCase().endsWith(".SQL")) {
                sql_files.add(file.getPath());
            }
        }
        FedLogger.trace("Starting FDBS...");
        
        long start_time = System.currentTimeMillis();
        executeScripts(sql_files);
        
        FedLogger.trace("All scripts were executed in " + getElapsedTime(start_time, System.currentTimeMillis()) + "\n");
    }
    
    private void executeScripts (List<String> sql_files) {
        FedConnection fco = null;
        
        try {
            fco = new FedPseudoDriver().getConnection();
            fco.setAutoCommit(false);
            
            for (String sql_file : sql_files) {
                FedLogger.trace("**************************************************************************");
                FedLogger.trace((FedConfig.validate ? "Validating" : "Reading") + " SQL statements in " + sql_file + "...");
                
                try {
                    FedStatement fst  = fco.getStatement();
                    List<String> sqls = FedFileReader.getSQLStatements(sql_file);
                    long start_time   = System.currentTimeMillis();
    
                    for (String statement : sqls) {
                        try {
                            /* TODO: Want to fork it?
                             * - For scripts only containing INSERT statements, 
                             *   implement FedPreparedStatement to improve performance
                             */
                            Object rs = fst.execute(statement);
                                
                            statement = statement.toUpperCase();
                            if (rs instanceof Integer && (statement.contains("DELETE") || statement.contains("UPDATE"))) {
                                FedLogger.info(rs + " rows " + (statement.contains("DELETE") ? "deleted" : "updated"));
                            }
                            else if (rs instanceof FedResultSet) {
                                FedLogger.printFedResults((FedResultSet) rs);
                            }
                        }
                        catch (FedException e) {
                            FedLogger.error("FDBS Execution Exception: " + e.getMessage());
                        }
                        catch (ParseException | TokenMgrError pe) {
                            FedLogger.error("FDBS Parser Exception: " + pe.getMessage().trim());
                        }
                    }
                    FedLogger.trace("Execution completed in " + getElapsedTime(start_time, System.currentTimeMillis()) + "\n");
                }
                catch (FileNotFoundException ff) {
                    System.out.println(ff.getMessage());
                }
            }
        } 
        catch (FedException e) {
            FedLogger.error("FDBS Exception: " + e.getMessage());
            try {
                if (fco != null) { 
                    fco.rollback();
                    fco.close();
                    fco = null;
                }
            } catch (FedException fe) {
                FedLogger.error("FDBS Rollback Exception: " + fe.getMessage());
            }
        }
        finally {
            if (fco != null) {
                try {
                    //fco.setAutoCommit(true);
                    fco.close();
                } catch (FedException fe) {
                    FedLogger.error("FDBS Commit/Close Exception: " + fe.getMessage());
                }
            }
        }
    }
    
    private static String getElapsedTime (long start, long end) {
        long total = end - start;

        return  (total >= 60000 ? (total / 1000 / 60) + "m, " + (total / 1000 % 60) + "s" : "") +
                (total >= 1000 && total < 60000 ? (total / 1000) + "s, " + (total % 1000) + "ms" : "") +
                (total < 1000 ? total + "ms" : "");
    }
    
}
