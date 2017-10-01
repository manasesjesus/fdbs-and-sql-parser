package test;

import java.util.HashSet;
import java.util.Set;

import fed.fdbs.FedConfig;
import fed.fdbs.FedConnection;
import fed.fdbs.FedException;
import fed.fdbs.FedLogger;
import fed.fdbs.FedPseudoDriver;
import fed.fdbs.FedResultSet;
import fed.fdbs.FedStatement;


/**
 * Test of big tables
 * 
 */
public class FedBigTableTest {
    
    public static void main (String[] args) {
        try {
            // Load system configuration or terminate if fails to load
            FedConfig.loadProperties(); 
            
            FedConnection fco = null;
            FedStatement fst  = null;
            FedResultSet rs   = null;
            
            Set<String> cities = new HashSet<>();
            Set<Integer> pks   = new HashSet<>(); 
            String statement   = "SELECT * FROM R100K";
            
            long start_time;
            
            try {
                fco = new FedPseudoDriver().getConnection();
                fco.setAutoCommit(false);
                fst = fco.getStatement();
                fst.setFetchSize(0);
                
                for (int i = 0; i <= 1; i++) {
                    start_time = System.currentTimeMillis();
                    rs = (FedResultSet) fst.execute(statement);
                    while (rs.next()) {
                        cities.add(rs.getString(9));    // STADT100
                    }
                    FedLogger.info("Total unique cities:     " + cities.size());
                    FedLogger.info(getElapsedTime(start_time, System.currentTimeMillis()));
                    
                    start_time = System.currentTimeMillis();
                    rs = (FedResultSet) fst.execute(statement);
                    while (rs.next()) {
                        pks.add(rs.getInt(1));          // PK
                    }
                    int total = 0;
                    for (int pk : pks) {
                        total += pks.contains(pk * 2) ? 1 : 0;
                    }
                    FedLogger.info("Total unique PK*2:           " + total);
                    FedLogger.info(getElapsedTime(start_time, System.currentTimeMillis()));
 
                    fst.setFetchSize(50000);
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
            
        } catch (Exception e) {
            System.out.println("Oops! Unhandled exception! Reason:\n" + e.getMessage());;
        }
    }
    
    
    /** Calculates the elapsed time
    *
    * @param start time (milliseconds)
    * @param end time (milliseconds)
    * @return a formatted string with the elapsed time
    */
   private static String getElapsedTime (long start, long end) {
       long total = end - start;

       return "Elapsed time:   \t\t" +
               (total >= 60000 ? (total / 1000 / 60) + "m, " + (total / 1000 % 60) + "s" : "") +
               (total >= 1000 && total < 60000 ? (total / 1000) + "s, " + (total % 1000) + "ms" : "") +
               (total < 1000 ? total + "ms" : "") + "\n";
   }
}
