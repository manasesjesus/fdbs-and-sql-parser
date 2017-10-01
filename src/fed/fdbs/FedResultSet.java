package fed.fdbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * It works similar to a JDBC ResultSet class by aggregating three of them. It provides a polymorphic 
 * getValue method to call getString or getInt methods based on the column type. 
 *
 */
public class FedResultSet implements FedResultSetInterface {
    private ResultSet[] rs;
    private int rs_index;
    private boolean function;

    /**
     * Create a FedResultSet that contains the ResultSet objects obtained after the execution of a
     * SELECT FedStatement.
     * 
     * @param rs1
     * @param rs2
     * @param rs3
     */
    public FedResultSet (ResultSet rs1, ResultSet rs2, ResultSet rs3, boolean function) {
        this.rs = new ResultSet[] { null, rs1, rs2, rs3 };
        this.rs_index = 1;
        this.function = function;
    }
        

    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#next()
     */
    @Override
    public boolean next () throws FedException {
        try {
            if (rs[1] != null && rs[1].next()) {
                rs_index = 1;
                return true;
            }
            else if (!function && rs[2] != null && rs[2].next()) {
                rs_index = 2;
                return true;
            }
            else if (!function && rs[3] != null && rs[3].next()) {
                rs_index = 3;
                return true;
            }            
        } catch (SQLException e) {
            throw new FedException(e);
        }
        
        rs_index = 1;
        return false;
    }

    
    /**
     * Retrieve the value of the designated column in the current row of this FedResultSet object 
     * as an instance of String/Integer.
     * 
     * @param colIndex
     * @return an Object instance of String or Integer
     * @throws FedException
     */
    public Object getValue (int colIndex) throws FedException {
        if (rs[rs_index] != null) {            
            return getColumnType(colIndex) == Types.VARCHAR ? getString(colIndex) : getInt(colIndex);
        }
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#getString(int)
     */
    @Override
    public String getString (int columnIndex) throws FedException {
        try {
            if (rs[rs_index] != null) {
                return rs[rs_index].getString(columnIndex);
            }
        } catch (SQLException e) {
            throw new FedException(e);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#getInt(int)
     */
    @Override
    public int getInt (int columnIndex) throws FedException {
        try {
            if (!function && rs[rs_index] != null) {
                return rs[rs_index].getInt(columnIndex);
            }
            else if (function) {
                return (rs[1] != null  ? rs[1].getInt(columnIndex) : 0) +
                       (rs[2] != null && rs[2].next() ? rs[2].getInt(columnIndex) : 0) +
                       (rs[3] != null && rs[3].next() ? rs[3].getInt(columnIndex) : 0); 
            }
        } catch (SQLException e) {
            throw new FedException(e);
        }

        return 0;
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#getColumnCount()
     */
    @Override
    public int getColumnCount () throws FedException {
        try {
            if (rs[rs_index] != null) {
                return rs[rs_index].getMetaData().getColumnCount();
            }
        } catch (SQLException e) {
            throw new FedException(e);
        }

        return 0;
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#getColumnName(int)
     */
    @Override
    public String getColumnName (int index) throws FedException {
        try {
            if (rs[rs_index] != null) {
                return rs[rs_index].getMetaData().getColumnName(index);
            }
        } catch (SQLException e) {
            throw new FedException(e);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#getColumnType(int)
     */
    @Override
    public int getColumnType (int index) throws FedException {
        try {
            if (rs[rs_index] != null) {
                return rs[rs_index].getMetaData().getColumnType(index);
            }
        } catch (SQLException e) {
            throw new FedException(e);
        }

        return Types.OTHER;
    }

    /* (non-Javadoc)
     * @see fedjdbc.FedResultSetInterface#close()
     */
    @Override
    public void close () throws FedException {
        try {
            if (rs[1] != null) rs[1].close();
            if (rs[2] != null) rs[2].close();
            if (rs[3] != null) rs[3].close();
        } catch (SQLException e) {
            throw new FedException(e);
        }
    }
    
}
