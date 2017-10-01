package fed.fdbs;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * It defines the names of the METADATA and OPERATION tables, and provides methods to create them 
 * (if not yet created). It also provides a method to drop all the temporal tables used on the 
 * distributed joins.
 *
 */
public class FedCatalog {
    // Catalog tables
    public static final String METADATA  = "FEDMETADATA";
    public static final String OPERATION = "FEDOPERATION";
    public static final String TEMPREFIX = "FT$_";
    
    // DB codes
    public static final String ERRORA00942 = "ORA-00942";      // Table or view does not exist (Oracle)
    public static final int SUCCESS        = 1; 
    public static final int NO_EXECUTION   = 0; 
    
    // Statement of the main DB connection
    private static Statement stmt;

    
    /* Main DB Statement Setter */
    public static void setStatement (Statement stmt) {
        FedCatalog.stmt = stmt;
    }
    
    
    /**
     * @throws SQLException if it's not possible to create the METADATA table
     */
    public static void createMetadataTable () throws SQLException {
        if (stmt != null) {
            // Create metadata table if it doesn't exist
            try {       
                stmt.executeQuery("SELECT * FROM " + METADATA);
            } catch (SQLException e) {
                if (e.getMessage().contains(ERRORA00942)) {
                    FedLogger.trace("Creating " + METADATA + " table...");
                    stmt.executeUpdate("CREATE TABLE " + METADATA  
                            + " (ftable VARCHAR(20), fcolumn VARCHAR(20), left INTEGER, right INTEGER)");
                }
            }
        }
    }
    
    /**
     * @throws SQLException if it's not possible to create the OPERATION table
     */
    public static void createOperationTable () throws SQLException {
        if (stmt != null) {
            // Create operation table if it doesn't exist
            try {       
                stmt.executeQuery("SELECT * FROM " + OPERATION);
            } catch (SQLException e) {
                if (e.getMessage().contains(ERRORA00942)) {
                    FedLogger.trace("Creating " + OPERATION + " table...");
                    stmt.executeUpdate("CREATE TABLE " + OPERATION + " (TEMP INTEGER)");
                }
            }
        }
    }
    
    /**
     * @param table     
     *          the table to search on the catalog
     * @return A ResultSet containing the metadata of the distributed table if this exists, null otherwise.
     * @throws SQLException
     */
    public static ResultSet getMetadata (String table) throws SQLException {
        return stmt != null ? stmt.executeQuery("SELECT * FROM " + METADATA + " WHERE ftable = '" + table + "'") : null;
    }
    
    
    /* ****************** Federated JOIN Methods ****************** */
    
    /**
     * Returns a new query with the name of the temporal table to be joined.
     * 
     * @param sql
     *          the original query
     * @param table
     *          the table to be joined
     * @return
     *          the new query with the name of the temporal table
     */
    public static String getTempQuery (String sql, String table) {
        return sql.replace(table + ".", TEMPREFIX + table + ".")
                  .replace(table + ",", TEMPREFIX + table + ",")
                  .replace(table + " ", TEMPREFIX + table + " ");
    }
    
    
    /**
     * Creates a temporal copy of a table
     *  
     * @param table
     *          the table to be copied
     * @throws SQLException
     */
    public static void createTempTable (String table) throws SQLException {
        if (stmt != null) {
            stmt.executeUpdate("CREATE TABLE " + TEMPREFIX + table + " AS SELECT * FROM " + table);
        }
    }
    
    /**
     * Inserts the rows from db2.table, [db3.table] into db1.tempTable
     * 
     * @param table
     *          inserts go to this table
     * @param rst
     *          all rows from db2, [db3]
     * @throws SQLException
     */
    public static void insertIntoTempTable (String table, ResultSet rst) throws SQLException {
        /* TODO: Want to fork it?
         * - Implement inserts with PreparedStatement to improve performance
         */
        List<String> inserts = new ArrayList<>();
        
        while (rst != null && rst.next()) {
            ResultSetMetaData meta = rst.getMetaData();
            String insert = "INSERT INTO " + TEMPREFIX + table + " VALUES (";
            
            for (int i = 1, colcount = meta.getColumnCount(); i <= colcount; i++) {
                boolean is_int = meta.getColumnType(i) != Types.VARCHAR; 
                insert += (is_int ? rst.getInt(i) : rst.getString(i) == null ? "null" : 
                                    "'" + rst.getString(i) + "'")
                        + (i+1 <= colcount ? ", " : ")");
            }
            inserts.add(insert);
        }
        
        for (String ins : inserts) {
            stmt.executeUpdate(ins);
        }
    }
    
    /**
     * Drops temporal tables 
     * 
     * @throws FedException
     */
    public static void dropTempTables () {
        // SELECT 'DROP TABLE ' || table_name AS sqlcmd FROM user_tables WHERE table_name LIKE 'FT$_%';

        if (stmt != null) {
            try {
                if (stmt.isClosed()) {
                    stmt = DriverManager.getConnection(FedConfig.getDb1(), FedConfig.getUser(), FedConfig.getPasswd()).createStatement();
                }
                
                List<String> drops = new ArrayList<>();
                ResultSet rs = stmt.executeQuery("SELECT 'DROP TABLE ' || table_name AS sqlcmd " 
                                                 + "FROM user_tables WHERE table_name LIKE " 
                                                 + "'" + TEMPREFIX + "%'");
                
                while (rs != null && rs.next()) {
                    drops.add(rs.getString("sqlcmd"));
                }
                
                for (String drop : drops) {
                    stmt.executeUpdate(drop);
                }
            }
            catch (SQLException e) {
                FedLogger.error("FDBS Execution Exception: Not possible to drop the temporal tables at the moment...");
            }
        }
    }
}
