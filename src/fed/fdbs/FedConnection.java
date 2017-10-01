package fed.fdbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class contains three SQL Connection objects that hold the connections to three 
 * databases (DBs). The default DBs are set in the FedConfig class.
 * 
 * It provides a subject of the Connection objects to perform commits, rollbacks and
 * enable/disable the auto commits.
 *
 */
public class FedConnection implements FedConnectionInterface {
    private Connection conn1;
    private Connection conn2;
    private Connection conn3;
    private FedStatement fstmt;
    
    
    /**
     * Create a FedConnection object to connect to the DBs with the default user
     * 
     * @throws FedException
     */
    public FedConnection () throws FedException {
        this(FedConfig.getUser(), FedConfig.getPasswd());
    }
    
    
    /**
     * Create a FedConnection object to connect to the DBs with the specified username/password
     * and sets the FedStatement object.
     * 
     * @param username
     * @param password
     * @throws FedException
     */
    public FedConnection (String username, String password) throws FedException {
        try {
            
            FedLogger.trace("Connecting to DB(" + FedConfig.getDb1Name() + ")...");
            conn1 = DriverManager.getConnection(FedConfig.getDb1(), username, password);
            
            FedLogger.trace("Connecting to DB(" + FedConfig.getDb2Name() + ")...");
            conn2 = DriverManager.getConnection(FedConfig.getDb2(), username, password);
            
            FedLogger.trace("Connecting to DB(" + FedConfig.getDb3Name() + ")...");
            conn3 = DriverManager.getConnection(FedConfig.getDb3(), username, password);
            
            fstmt = new FedStatement(conn1, conn2, conn3, this);
            
        } catch (SQLException e) {
            throw new FedException(e);
        }
    }
    
    
    /* (non-Javadoc)
     * @see fedjdbc.FedConnectionInterface#setAutoCommit(boolean)
     */
    @Override
    public void setAutoCommit (boolean autoCommit) throws FedException {
        try {
            FedLogger.trace("Setting autoCommit to " + autoCommit);
            if (conn1 != null) conn1.setAutoCommit(autoCommit);
            if (conn2 != null) conn2.setAutoCommit(autoCommit);
            if (conn3 != null) conn3.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new FedException(e);
        }
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedConnectionInterface#getAutoCommit()
     */
    @Override
    public boolean getAutoCommit () throws FedException {
        try {
            if (conn1 != null) return conn1.getAutoCommit();
        } catch (SQLException e) {
            throw new FedException(e);
        }   
        return false;
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedConnectionInterface#commit()
     */
    @Override
    public void commit () throws FedException {
        try {
            FedLogger.trace("Making changes permanent to the databases...");
            if (conn1 != null && !conn1.getAutoCommit()) conn1.commit();
            if (conn2 != null && !conn2.getAutoCommit()) conn2.commit();
            if (conn3 != null && !conn3.getAutoCommit()) conn3.commit();
        } catch (SQLException e) {
            throw new FedException(e);
        }
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedConnectionInterface#rollback()
     */
    @Override
    public void rollback () throws FedException {
        try {
            FedLogger.trace("Undoing changes made during the current transaction...");
            if (conn1 != null) conn1.rollback();
            if (conn2 != null) conn2.rollback();
            if (conn3 != null) conn3.rollback();
        } catch (SQLException e) {
            throw new FedException(e);
        }
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedConnectionInterface#close()
     */
    @Override
    public void close () throws FedException {
        try {
            if (fstmt != null) {
                fstmt.close();
            }
            if (conn1 != null) { 
                FedLogger.trace("Closing DB(" + FedConfig.getDb1Name() + ") connection...");
                conn1.close();
            }
            if (conn2 != null) {
                FedLogger.trace("Closing DB(" + FedConfig.getDb2Name() + ") connection...");
                conn2.close();
            }
            if (conn3 != null) {
                FedLogger.trace("Closing DB(" + FedConfig.getDb3Name() + ") connection...");
                conn3.close();
            }
        } catch (SQLException e) {
            throw new FedException(e);
        }
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedConnectionInterface#getStatement()
     */
    @Override
    public FedStatement getStatement () {
        return fstmt;
    }

}
