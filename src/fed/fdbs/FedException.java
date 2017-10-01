package fed.fdbs;

import java.sql.SQLException;

/**
 * Custom Exception class of the FDBS
 *
 */
public class FedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Create a FedException object that receives a SQLException object
	 * and calls the parent(super) method with a custom message.
	 * 
	 * @param e the thrown SQLException
	 */
	public FedException (SQLException e) {
        super(e.getErrorCode() + ": " + e.getMessage().trim());
    }
}
